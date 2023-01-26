package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.*;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.ControllerHelper;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.Pair;
import com.lokcenter.AZN_Spring_ResourceServer.services.GeneralVacationService;
import com.lokcenter.AZN_Spring_ResourceServer.services.OverviewService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * OverviewController
 * <p>
 * Is responsible for all calendar requests
 */
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

    @Autowired
    private ControllerHelper controllerHelper;

    @Autowired
    private GeneralVacationService generalVacationService;

    @Autowired
    private  OverviewService overviewService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    /**
     * Stats for overview side bar
     */
    public static class Stats {
        @Setter
        @Getter
        int availableVacation;

        @Setter
        @Getter
        int availableVacationWithRequestedCount;

        @Setter
        @Getter
        Time balanceTime;

        @Setter
        @Getter
        UserInfo.Balance balance;
    }

    @AllArgsConstructor
    /*
     * Converts multiple Calendar classes to a Range.
     */
    public abstract static class DateRange {
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
       private UUID id;

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

    public static class DateRangeComment extends DateRange {
        @Setter
        @Getter
        private String comment;

        public DateRangeComment(Date start, Date end, Tags tag, java.util.UUID id, String text) {
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
            @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws JsonProcessingException, ParseException, ExecutionException, InterruptedException {

        CompletableFuture<Set<DateRange>> userDataSet =  new CompletableFuture<>();
        CompletableFuture<Set<DateRange>> requestRange = new CompletableFuture<>();
        Set<DateRange> dateRanges = new HashSet<>();
        String format = "dd-MM-yyyy";

        var sdf = new SimpleDateFormat(format);

        // stop without first and last day
        if (firstDay == null || lastDay == null) {
            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(dateRanges);
        }

        // Stuff without roles or userid
        Pair<String, String> dates = controllerHelper.parseStartEndDate(firstDay, lastDay, year, month);

        String startDate = dates.getKey();
        String endDate = dates.getValue();

         CompletableFuture<Set<DateRange>> genRange = generalVacationService.MinMaxGeneralVacation(new java.sql.Date(sdf.parse(startDate).getTime()),
                new java.sql.Date(sdf.parse(endDate).getTime()));

        // roles and userid stuff
        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);
            userDataSet = overviewService.getUserDependingDayplanData(user, new java.sql.Date(sdf.parse(startDate).getTime()),
                    new java.sql.Date(sdf.parse(endDate).getTime()));

            requestRange = overviewService.getRequestDataByRange(user, new java.sql.Date(sdf.parse(startDate).getTime()),
                    new java.sql.Date(sdf.parse(endDate).getTime()));
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                userDataSet = overviewService.getUserDependingDayplanData(user, new java.sql.Date(sdf.parse(startDate).getTime()),
                        new java.sql.Date(sdf.parse(endDate).getTime()));

                requestRange = overviewService.getRequestDataByRange(user, new java.sql.Date(sdf.parse(startDate).getTime()),
                        new java.sql.Date(sdf.parse(endDate).getTime()));
            }
        }


        dateRanges.addAll(genRange.get());
        dateRanges.addAll(requestRange.get());
        dateRanges.addAll(userDataSet.get());

        return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(dateRanges);
    }

    /**
     * Post data from Calendar request
     * @return Boolean value, true if request was successful and false if not.
     */
    @ResponseBody()
    @PostMapping()
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    Boolean postRequests(@RequestBody Map<String, Object> payload, Authentication auth) {
        System.out.println(payload);

        try {
            Jwt jwt = (Jwt) auth.getPrincipal();
            String name = jwt.getClaim("unique_name");
            Optional<Users> user = userRepository.findByUsername(name);

            if (user.isPresent()) {
                Optional<Requests> requests = controllerHelper.getValidNonExistingRequest(payload, user.get());

                if (requests.isPresent()) {
                    requestsRepository.insertSave(requests.get());
                    return true;
                }
            }
           return false;

        } catch (Exception exception) {

            exception.printStackTrace();

            return false;
        }
    }

    /**
     * Get Dayplan data dates where start and end time is set

     * @return json
     * @throws JsonProcessingException
     */
    @GetMapping("/dayt")
    String getDayPlanDataWithTime(
            @RequestParam(name = "firstday", required = false) String firstDay,
            @RequestParam(name = "lastday", required = false) String lastDay,
            @RequestParam(name = "month", required = false) String month,
            @RequestParam(name = "year", required = false) String year,
            Authentication auth) throws JsonProcessingException, ParseException {

        Set<String> dayPlansDone = new HashSet<>();

        var sdf = new SimpleDateFormat("dd-MM-yyyy");

        Pair<String, String> dates = controllerHelper.parseStartEndDate(firstDay, lastDay, year, month);

        String startDate = dates.getKey();
        String endDate = dates.getValue();

        // get userId
        Jwt jwt = (Jwt) auth.getPrincipal();

        String name = jwt.getClaim("unique_name");

        // get userId;
        Optional<Users> user = userRepository.findByUsername(name);

        if (user.isPresent()) {
            Iterable<DayPlanData> dayPlanData = dayPlanDataRepository.
                    getDayPlanDataBySetDateBetweenAndUserId
                            (new Date(sdf.parse(startDate).getTime()), new Date(sdf.parse(endDate).getTime()), user.get().getUserId());


            for (var dpd: dayPlanData) {
               dayPlansDone.add(new SimpleDateFormat("yyyy-MM-dd").format(dpd.getSetDate()));
            }

            System.out.println(dayPlansDone);
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(dayPlansDone);
    }

    @GetMapping("/stats")
    String getStats(@RequestParam(name = "year", required = false) String year,
                    @RequestParam(name = "role", required = true) String role,
                    @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws JsonProcessingException, ExecutionException, InterruptedException {

        Stats stats = new Stats();

        // roles and userid stuff
        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);

            var one  = overviewService.getFreeVacationDays(Integer.parseInt(year), user);
            var two  = overviewService.getVacationDaysUsedByRequests(Integer.parseInt(year), user);

            stats.setAvailableVacation(one.get());
            stats.setAvailableVacationWithRequestedCount(one.get() - two.get());
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                stats.setAvailableVacation(overviewService.getFreeVacationDays(Integer.parseInt(year), user).get());
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(stats);
    }

}