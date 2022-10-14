package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.RequestsRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestsRepository requestsRepository;

    @AllArgsConstructor
    /*
     * Converts multiple Calendar classes to a Range.
     */
    private abstract static class DateRange {
       @Setter
       @Getter
       private Date start;

       @Setter
       @Getter
       private Date end;

       @Setter
       @Getter
       private Tags tag;

       @Setter
       @Getter
       private String id;

       @Setter
       @Getter
       private String text;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DateRange dateRange = (DateRange) o;
            return start.equals(dateRange.start);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start);
        }
    }

    static
    class DateRangeComment extends DateRange {
        @Setter
        @Getter
        private String comment;

        public DateRangeComment(Date start, Date end, Tags tag, String id, String text) {
            super(start, end, tag, id, text);
        }

        @Override
        public String toString() {
            return "DateRangeComment{" +
                    "comment='" + comment + '\'' +
                    ", startDate=" + this.getStart() +
                    ", endDate=" + this.getEnd() +
                    '}';
        }
    }

    private Set<DateRange> getUserDependingData(Optional<Users> user, Date first, Date last) {
        Set<DateRange> rangeData = new HashSet<>();

       if (user.isPresent()) {
           // requests stuff
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

               rangeData.add(new DateRangeComment(r.getStartDate(), r.getEndDate(), tag, r.getUuid(), text));

               // day plan data

           }
       }


        return rangeData;
    }
    @ResponseBody
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getOverviewByMonth(
            @RequestParam(name = "firstday", required = false) String firstDay,
            @RequestParam(name = "lastday", required = false) String lastDay,
            @RequestParam(name = "month", required = false) String month,
            @RequestParam(name = "year", required = false) String year,
            @RequestParam(name = "role", required = true) String role,
            @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws JsonProcessingException, ParseException {

        Set<DateRange> dateRanges = new HashSet<>();

        // stop without first and last day
        if (firstDay == null || lastDay == null) {
            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(dateRanges);
        }

        // Stuff without roles or userid
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
        Map<String, ArrayList<GeneralVacation>> generalVacationByUUID = new HashMap<>();

        for (var gv : generalVacations) {
            if (generalVacationByUUID.containsKey(gv.getUuid())) {
                generalVacationByUUID.get(gv.getUuid()).add(gv);
            } else {
                generalVacationByUUID.put(gv.getUuid(), new ArrayList<>(List.of(gv)));
            }
        }

        // get min and max date from general vacation
        for (var gv: generalVacationByUUID.entrySet()) {
            if (gv.getValue().size() > 1) {
                dateRanges.add(new DateRangeComment(
                        gv.getValue().stream().map(GeneralVacation::getDate).min(Date::compareTo).get(),
                        gv.getValue().stream().map(GeneralVacation::getDate).max(Date::compareTo).get(),
                        Tags.gUrlaub,
                        gv.getKey(),
                        gv.getValue().get(0).getComment()
                ));
            } else {
                dateRanges.add(
                        new DateRangeComment(gv.getValue().get(0).getDate(),
                                gv.getValue().get(0).getDate(),
                                Tags.gUrlaub,
                                gv.getKey(),
                                gv.getValue().get(0).getComment()
                                ));
            }
        }

        System.out.println(dateRanges);

        // roles and userid stuff
        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);
            dateRanges.addAll(getUserDependingData(user, new java.sql.Date(sdf.parse(startDate).getTime()),
                    new java.sql.Date(sdf.parse(endDate).getTime())));
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                dateRanges.addAll(getUserDependingData(user, new java.sql.Date(sdf.parse(startDate).getTime()),
                        new java.sql.Date(sdf.parse(endDate).getTime())));
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(dateRanges);
    }
}
