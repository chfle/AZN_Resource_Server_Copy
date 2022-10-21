package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.UUIDable;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.RequestsRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
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

    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

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

    private Map<String, ArrayList<UUIDable>> mapByUUID(Iterable<? extends UUIDable> uuiDableCollection) {
        Map<String, ArrayList<UUIDable>> map = new HashMap<>();

        for (var uuidable : uuiDableCollection) {
            if (map.containsKey(uuidable.getUuid())) {
                map.get(uuidable.getUuid()).add(uuidable);
            } else {
                map.put(uuidable.getUuid(), new ArrayList<>(List.of(uuidable)));
            }
        }

        return map;
    }

    private Set<DateRange> getUserDependingData(Optional<Users> user, Date first, Date last) {
        Set<DateRange> rangeData = new HashSet<>();

        System.out.println(user.get().getUserId());

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
           }


           // day plan data
           Iterable<DayPlanData> dayPlanDatas = dayPlanDataRepository.getAllByUserWhereTrue(user.get(), first, last);

           Map<String, ArrayList<UUIDable>> dayPlanMap = mapByUUID(dayPlanDatas);

           // get min and max range from dayPlanData
           for (var dpv: dayPlanMap.entrySet()) {
               if (dpv.getValue().size() > 1) {
                   rangeData.add(new DateRangeComment(
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
                           new DateRangeComment(dayPlanData.getSetDate(),
                                   dayPlanData.getSetDate(),
                                   DayPlanData.getTag(dayPlanData),
                                   dpv.getKey(),
                                   DayPlanData.getTag(dayPlanData).name()
                           ));
               }
           }
       }



        return rangeData;
    }

    /**
     * Get Data from All Databases to get everything to the calendar
     *
     * @param firstDay first day from the calendar
     * @param lastDay last day from the calendar
     * @param month month to query
     * @param year year to query
     * @param role user role
     * @param userid userid -> Admin role needed
     * @param auth authentication
     * @return Requested data as json format
     */
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
        Map<String, ArrayList<UUIDable>> generalVacationByUUID = mapByUUID(generalVacations);


        // get min and max date from general vacation
        for (var gv: generalVacationByUUID.entrySet()) {
            if (gv.getValue().size() > 1) {
                dateRanges.add(new DateRangeComment(
                        gv.getValue().stream().map(uuiDable ->
                                ((GeneralVacation)uuiDable).getDate()).min(Date::compareTo).get(),
                        gv.getValue().stream().map(uuiDable ->
                                ((GeneralVacation)uuiDable).getDate()).max(Date::compareTo).get(),
                        ((GeneralVacation)gv.getValue().get(0)).getTag() == Tags.gFeiertag ? Tags.gFeiertag: Tags.gUrlaub,
                        gv.getKey(),
                        ((GeneralVacation)gv.getValue().get(0)).getComment()
                ));
            } else {
                GeneralVacation generalVacation = (GeneralVacation)gv.getValue().get(0);
                dateRanges.add(
                        new DateRangeComment(generalVacation.getDate(),
                                generalVacation.getDate(),
                                generalVacation.getTag() == Tags.gFeiertag ? Tags.gFeiertag: Tags.gUrlaub,
                                gv.getKey(),
                                generalVacation.getComment()
                                ));
            }
        }

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


    /**
     * Post data from Calendar request
     * @return Boolean value
     */
    @ResponseBody()
    @PostMapping("/requests")
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    Boolean postRequests() {
        return true;
    }
}