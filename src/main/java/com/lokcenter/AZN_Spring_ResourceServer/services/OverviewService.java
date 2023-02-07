package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.controller.OverviewController;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IUuidable;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.RequestsRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.TimeConvert;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.ControllerHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class OverviewService {
    @Autowired
    private final RequestsRepository requestsRepository;

    @Autowired
    private final DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private final ControllerHelper controllerHelper;

    @Autowired
    private UserInfoRepository userInfoRepository;


    /**
     * Get requested date ranges for calendar
     */
    @Async
    public CompletableFuture<Set<OverviewController.DateRange>> getRequestDataByRange(Optional<Users> user, Date first, Date last) {
        Set<OverviewController.DateRange> rangeData = new HashSet<>();

       if (user.isPresent())  {
           Iterable<Requests> requests = requestsRepository.getRequestsByRange(first, last, user.get());

           // convert requests to DataRange
           for (var r: requests) {
               String text;
               Tags tag;

               if (r.getType() == RequestTypeEnum.rGLAZ) {
                   text = "GLAZ (wartend)";
                   tag = Tags.rGLAZ;
               } else {
                   text = "Urlaub (wartend)";
                   tag = Tags.rUrlaub;
               }

               rangeData.add(new OverviewController.DateRangeComment(r.getStartDate(), r.getEndDate(), tag, r.getUuid(), text));
           }
       }

       return CompletableFuture.completedFuture(rangeData);
    }

    /**
     * Get Dayplan Data for calendar

     */
    @Async
    public CompletableFuture<Set<OverviewController.DateRange>> getUserDependingDayplanData(
            Optional<Users> user, Date first, Date last) {

        Set<OverviewController.DateRange> rangeData = new HashSet<>();

        if (user.isPresent()) {
            // day plan data
            Iterable<DayPlanData> dayPlanDatas = dayPlanDataRepository.getAllByUserWhereTrue(user.get(), first, last);

            Map<UUID, ArrayList<IUuidable>> dayPlanMap = controllerHelper.mapByUUID(dayPlanDatas);

            // get min and max range from dayPlanData
            for (var dpv: dayPlanMap.entrySet()) {
                if (dpv.getValue().size() > 1) {
                    rangeData.add(new OverviewController.DateRangeComment(
                            dpv.getValue().stream().map(uuiDable ->
                                    ((DayPlanData)uuiDable).getSetDate()).min(Date::compareTo).get(),
                            dpv.getValue().stream().map(uuiDable ->
                                    ((DayPlanData)uuiDable).getSetDate()).max(Date::compareTo).get(),
                            DayPlanData.getTag((DayPlanData)dpv.getValue().get(0)),
                            dpv.getKey(),
                            DayPlanData.getTag((DayPlanData)dpv.getValue().get(0)).name()
                    ));
                } else {
                    DayPlanData  dayPlanData = (DayPlanData)dpv.getValue().get(0);
                    rangeData.add(
                            new OverviewController.DateRangeComment(dayPlanData.getSetDate(),
                                    dayPlanData.getSetDate(),
                                    DayPlanData.getTag(dayPlanData),
                                    dpv.getKey(),
                                    DayPlanData.getTag(dayPlanData).name()
                            ));
                }
            }
        }

        return CompletableFuture.completedFuture(rangeData);
    }

    /**
     * Free vacation from userinfo
     */
    @Async
    public CompletableFuture<Integer> getFreeVacationDays(int year, Optional<Users> users) {
        int availableVacation = 0;

        if (users.isPresent()) {
            Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(users.get().getUserId());

            if (optionalUserInfo.isPresent()) {
                UserInfo userInfo = optionalUserInfo.get();

                String lastYearCount = userInfo.getAvailableVacation().getOrDefault(String.valueOf(year -1), "0");

                availableVacation =  Integer.parseInt(lastYearCount) + Integer.parseInt(userInfo.getAvailableVacation().getOrDefault(String.valueOf(year), "0"));
            }
        }

        return CompletableFuture.completedFuture(availableVacation);
    }

    @Async
    public CompletableFuture<Integer> getVacationDaysUsedByRequests(int year, Optional<Users> users) {
        AtomicInteger vacationDays = new AtomicInteger();

        if (users.isPresent()) {
            Iterable<Requests> requests = requestsRepository.getVacationRequestsByYear(year);

            StreamSupport.stream(requests.spliterator(), true).forEach((req) -> {
                var start = TimeConvert.convertToLocalDateViaInstant(new java.util.Date(req.getStartDate().getTime()));
                var end = TimeConvert.convertToLocalDateViaInstant(new java.util.Date(req.getEndDate().getTime()));

                // go over each day from start to end and set request value
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    // only count days from current year
                    if (date.getYear() != year) {
                        continue;
                    }

                    // add new vacation day
                    vacationDays.getAndIncrement();
                }
            });
        }

        return CompletableFuture.completedFuture(vacationDays.get());
    }
}