package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.controller.OverviewController;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.UUIDable;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.RequestsRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.ControllerHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class OverviewService {
    @Autowired
    private final RequestsRepository requestsRepository;

    @Autowired
    private final DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private final ControllerHelper controllerHelper;

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

            Map<UUID, ArrayList<UUIDable>> dayPlanMap = controllerHelper.mapByUUID(dayPlanDatas);

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
}
