package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.TimeConvert;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Get Monthplan related data
 */
@Service
@AllArgsConstructor
public class MonthPlanService {
    @Autowired
    private final GeneralVacationRepository generalVacationRepository;

    @Autowired
    private final DayPlanDataRepository dayPlanDataRepository;

    /**
     * Get dayplan data by user
     */
    @Async
    public CompletableFuture<Map<String, Object>> getDayPlanDataByUserAndDate(Optional<Users> user, java.sql.Date date) throws IOException, ParseException {
        Optional<DayPlanData> dayPlanData = Optional.empty();

        if (user.isPresent()) {
            var dayPlanDataKey = new DayPlanDataKey(user.get().getUserId(), date);

            // check general vacation for requested day
            Optional<GeneralVacation> optionalGeneralVacation = generalVacationRepository.getGeneralVacationByDate(date);

            if (optionalGeneralVacation.isPresent()) {
                var dpdTemp = new DayPlanData();

                // must be set for later usage
                dpdTemp.setSetDate(date);
                dpdTemp.setUsers(user.get());

                // set the right tag
                switch (optionalGeneralVacation.get().getTag()) {
                    case gUrlaub -> dpdTemp.setVacation(true);
                    case gFeiertag -> dpdTemp.setHoliday(true);
                }
                dayPlanData = Optional.of(dpdTemp);
            } else {
                dayPlanData = dayPlanDataRepository.findById(dayPlanDataKey);
            }
        }

        if (dayPlanData.isPresent()) {
            var dpd = dayPlanData.get();

            // create dayplan object
            return CompletableFuture.completedFuture(new HashMap<>(Map.of(
                    "start", dpd.getWorkTime() != null ? dpd.getWorkTime().getStart(): "",
                    "end", dpd.getWorkTime() != null ? dpd.getWorkTime().getEnd(): "",
                    "pause", dpd.getWorkTime() != null ? dpd.getWorkTime().getPause(): "",
                    "glaz", dpd.getGlaz(),
                    "sick", dpd.getSick(),
                    "vacation", dpd.getVacation(),
                    "holiday", dpd.getHoliday(),
                    "school", dpd.getSchool(),
                    "comment", dpd.getComment())));
        }

        return CompletableFuture.completedFuture(new HashMap<>());
    }

    /**
     * Get dayplan of each month
     */
    @Async
    public CompletableFuture<List<Map<String, Object>>> getDayPlansOfMonth(String month, String year,
                                                                           CompletableFuture<Optional<Users>> user) throws ParseException, IOException, ExecutionException, InterruptedException {
        List<Map<String, Object>> dpd = new ArrayList<>();

        if (user.get().isPresent()) {
            // convert to date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = sdf.parse(String.format("1/%s/%s", month, year));

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            // get the last day of month
            int lastDayMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            // go over month
            for (LocalDate i = TimeConvert.convertToLocalDateViaInstant(date);
                 !i.isAfter(TimeConvert.
                         convertToLocalDateViaInstant(sdf.parse(String.format("%s/%s/%s",lastDayMonth, month, year)))); i = i.plusDays(1))
            {
                CompletableFuture<Map<String, Object>> dayPlanDataOptional =
                        getDayPlanDataByUserAndDate(user.get(),
                                new java.sql.Date(TimeConvert.convertToDateViaInstant(i).getTime()));

                dpd.add(dayPlanDataOptional.get());
            }
        }

        return CompletableFuture.completedFuture(dpd);
    }

}
