package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/overview")
public class OverviewController {
    @Autowired
    private GeneralVacationRepository generalVacationRepository;

    @AllArgsConstructor
    @ToString
    /*
     * Converts multiple Calendar classes to a Range.
     */
    private abstract class DateRange {
       @Setter
       @Getter
       private Date startDate;

       @Setter
       @Getter
       private Date endDate;
    }

    @ToString
    class DateRangeComment extends DateRange {
        @Setter
        @Getter
        private String comment;
        private DateRangeComment(Date startDate, Date endDate) {
            super(startDate, endDate);
        }

        public DateRangeComment(Date startDate, Date endDate, String comment) {
            this(startDate, endDate);
            this.comment = comment;
        }
    }

    @ResponseBody
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getOverviewByMonth(
            @RequestParam(name = "firstday", required = false) String firstDay,
            @RequestParam(name = "lastday", required = false) String lastDay,
            @RequestParam(name = "month", required = false) String month,
            @RequestParam(name = "year", required = false) String year) throws JsonProcessingException, ParseException {

        // TODO: Query Tables and pack it into one JSON BLOB
        if (firstDay == null || lastDay == null) {
            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(new DayPlanData());
        }
        String format = "dd-MM-yyyy";

        int yearParsed = Integer.parseInt(year);
        var sdf = new SimpleDateFormat(format);

        // we should use last year for start year
        if (month.equals("01")) {
            yearParsed -= 1;
        }
        String startDate = String.format("%s-%s-%s", firstDay,
                month.equals("01")? 12 : Integer.parseInt(month) - 1,
                yearParsed);

        yearParsed = Integer.parseInt(year);

        // should use new year as start year
        if (month.equals("12")) {
            yearParsed += 1;
        }

        String endDate = String.format("%s-%s-%s", lastDay,
                month.equals("12") ? 1 : Integer.parseInt(month) + 1, yearParsed);

        var generalVacations =
                generalVacationRepository.
                        getGeneralVacationByDateBetween(
                                new java.sql.Date(sdf.parse(startDate).getTime()),
                                new java.sql.Date(sdf.parse(endDate).getTime()));

        // map all general vacation with the same comment
        Map<String, ArrayList<GeneralVacation>> generalVacationByComment = new HashMap<>();

        for (var gv : generalVacations) {
            if (generalVacationByComment.containsKey(gv.getComment())) {
                generalVacationByComment.get(gv.getComment()).add(gv);
            } else {
                generalVacationByComment.put(gv.getComment(), new ArrayList<>(List.of(gv)));
            }
        }

        List<DateRange> dateRanges = new ArrayList<>();



        System.out.println(dateRanges);

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(new DayPlanData());
    }
}
