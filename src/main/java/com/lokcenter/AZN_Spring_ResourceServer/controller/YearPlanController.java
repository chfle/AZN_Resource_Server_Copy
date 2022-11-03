package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.util.Reflection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/yearplan")
public class YearPlanController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    void addToMap(Map<Year, Map<String, Object>> yearData, Map<Year, Object> data, Field field) {
        // go over each data point
        for (var entry: data.entrySet()) {
            if (yearData.containsKey(entry.getKey())) {
                yearData.get(entry.getKey()).put(field.getName(), entry.getValue());
            }
        }
    }

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
        private Time DailyWorkTime;

        @Setter
        @Getter
        private Map<Year, Map<String, Object>> years;

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
         * Vacation used
         */
        @Setter
        @Getter
        private int usedVacationDays;

        /**
         * Vacation from last year
         */
        @Setter
        @Getter
        private int vacationFromLastYear;
    }

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

            UserInfo userInfo = new UserInfo();
            userInfo.setUsers(user.get());

//
//            userInfo.setSickDays(Map.of(Year.of(2022), 6, Year.of(2023), 5));
//            userInfo.setVacationSick(Map.of(Year.of(2022), 2, Year.of(2023), 4));
//            userInfo.setGlazDays(Map.of(Year.of(2022), 1, Year.of(2023), 3));
//            userInfo.setAvailableVacation(Map.of(Year.of(2022), 29, Year.of(2023), 4));

//            userInfoRepository.save(userInfo);

            // Todo: Count checked Dayplans without any checkbox checked


            // All userinfo by user
            Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(user.get().getUserId());

            if (optionalUserInfo.isPresent()) {
                var userinfo = optionalUserInfo.get();

                Map<Year, Map<String, Object>> yearDataMap =  userinfo.yearToMap();

                // add to result class
                yearPlanCurrent.setYears(yearDataMap);
            }

            yearPlanCurrent.setFullName(realname);

            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(yearPlanCurrent);
        }

        throw new Exception("Bad Request");
    }
}
