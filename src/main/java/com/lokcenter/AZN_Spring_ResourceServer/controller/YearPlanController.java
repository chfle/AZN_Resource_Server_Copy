package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.WorkTimeRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.WorkTime;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.YearOverViewList;
import com.lokcenter.AZN_Spring_ResourceServer.services.VacationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

/**
 * Yearplan related controller
 */
@RestController
@RequestMapping("/yearplan")
public class YearPlanController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private YearOverViewList yearOverViewList;

    @Autowired
    private WorkTimeRepository workTimeRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private VacationService vacationService;

    /**
     * Store all needed Year Plan Data
     */
    @AllArgsConstructor
    @NoArgsConstructor
    private static class YearPlan {

        /**
            Fullname from Microsoft User
         */
        @Setter
        @Getter
        private String FullName;

        /**
         * Current Worktime from user
         *
         * @implNote must be checked with the current date
         */
        @Setter
        @Getter
        private String DailyWorkTime;

        @Setter
        @Getter
        private String weeklyWorkTime;

        @Setter
        @Getter
        private Date workTimeDate;

        @Setter
        @Getter
        private Map<String, Map<String, Object>> years;

        @Setter
        @Getter
        /*
         * Total Time from each year
         */
        private Time totalTimeAccount;


        // Vacation
        /**
         * All Vacation Days available
         */
        @Setter
        @Getter
        private int totalVacationDays;

        /**
         * Vacation from last year
         */
        @Setter
        @Getter
        private int vacationFromLastYear;

        @Setter
        @Getter
        private int usedVacationDays;
    }

    /**
     * Get yearplan by user
     *
     * @return Json String
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getYearPlanByUser(Authentication auth) throws Exception {
        Jwt jwt = (Jwt) auth.getPrincipal();
        String name = jwt.getClaim("unique_name");

        // get userId;
        Optional<Users> user = userRepository.findByUsername(name);

        if (user.isPresent()) {
            // get realname from user
            String realname = jwt.getClaim("name");
            var yearPlanCurrent = new YearPlan();

            yearPlanCurrent.setFullName(realname);
            yearPlanCurrent.setYears(yearOverViewList.getYearsListByUser(user.get()));

            // create last year
            var calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -1);
            int lastYear = calendar.get(Calendar.YEAR);


            // query tables
            Optional<WorkTime> workTime = workTimeRepository.getMostRecentWorkTimeByUser(user.get());
            Optional<String> soll = workTimeRepository.getMostRecentSollByUser(user.get());
            Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(user.get().getUserId());

            // get worktime stuff

            if (workTime.isPresent() && soll.isPresent()) {
                yearPlanCurrent.setDailyWorkTime(soll.get());
                yearPlanCurrent.setWorkTimeDate(workTime.get().getDate());

                SimpleDateFormat p = new SimpleDateFormat("HH:mm");
                java.util.Date date = p.parse(soll.get());

                double timeAsMin = ((date.getHours() * 60 + date.getMinutes()) * 5) / 60.0;

                yearPlanCurrent.setWeeklyWorkTime(timeAsMin + "h");
            }

            // Note: should only do vacation stuff.
            // userinfo stuff
            if (optionalUserInfo.isPresent()) {
                // get vacation from last year
                Map<String, String> avVacation = optionalUserInfo.get().getAvailableVacation();

                yearPlanCurrent.setVacationFromLastYear(Integer.
                        parseInt(avVacation.getOrDefault(String.valueOf(lastYear), "0")));

                // get available vacation days from this year
                yearPlanCurrent.setTotalVacationDays(
                        Integer.parseInt(
                                avVacation.getOrDefault(
                                        String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
                                        "0")));

            }

            // count used vacation days from day plan data
            long usedDays = dayPlanDataRepository.countByUserIdAndVacationTrue(user.get().getUserId());

            yearPlanCurrent.setUsedVacationDays((int) usedDays);

            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(yearPlanCurrent);
        }

        throw new Exception("Bad Request");
    }
}