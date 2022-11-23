package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * This Controller is view only. No Data should be changed!!!
 */
@RestController
@RequestMapping("/monthplan")
public class MonthPlanController {
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping
    String getMonthPlan( @RequestParam(name = "month", required = true) String month,
                         @RequestParam(name = "year", required = true) String year,
                         @RequestParam(name = "role", required = true) String role,
                         @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws JsonProcessingException {

        List<DayPlanData> monthData = new ArrayList<>();

        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);

            // TODO: Get data
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));

                // TODO: get data
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(monthData);
    }
}
