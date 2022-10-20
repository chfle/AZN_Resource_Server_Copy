package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
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

import java.sql.Time;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/yearplan")
public class YearPlanController {
    @Autowired
    private UserRepository userRepository;

    @AllArgsConstructor
    @NoArgsConstructor
    private class YearPlan {
        @AllArgsConstructor
        @NoArgsConstructor
        public static class YearData {
            /**
             * Current year
             */
            @Setter
            @Getter
            private int year;

            // Days

            /**
             * Workdays from this year
             */
            @Setter
            @Getter
            private int workDays;

            /**
             * Sickdays from this year
             */
            @Setter
            @Getter
            private int sick;

            /**
             * Vacation days from this year
             */
            @Setter
            @Getter
            private int vacation;

            /**
             * glaz days from this year
             */
            @Setter
            @Getter int glaz;

            @Setter
            @Getter
            /*
             * Only from this year example 2013
             */
            private Time CurrentYearTotalTimeAccount;
        }
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
        private Map<Integer, YearData> years;

        @Setter
        @Getter
        /*
         * Total Time from each year
         */
        private Time totalTimeaccount;


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

            yearPlanCurrent.setFullName(realname);

            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(yearPlanCurrent);
        }

        throw new Exception("Bad Request");
    }
}
