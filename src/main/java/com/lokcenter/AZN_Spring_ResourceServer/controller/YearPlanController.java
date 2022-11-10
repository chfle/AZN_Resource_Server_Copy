package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.YearOverViewList;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/yearplan")
public class YearPlanController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private YearOverViewList yearOverViewList;

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

            yearPlanCurrent.setFullName(realname);
            yearPlanCurrent.setYears(yearOverViewList.getYearsListByUser(user.get()));

            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(yearPlanCurrent);
        }

        throw new Exception("Bad Request");
    }
}