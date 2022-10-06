package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.azure.spring.cloud.autoconfigure.aad.implementation.oauth2.AadOAuth2AuthenticatedPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;

/**
 * Dayplan endpoint
 *
 * @version 1.01 - 01-07-2022
 */

@RestController
@RequestMapping("/dayplan")
public class DayPlanController {
    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private UserRepository userRepository;
    /**
     * Post User date
     * @param data user data
     * @return boolean
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping()
    boolean postDayPlan(@RequestBody Map<String, Object> data) {
        System.out.println(data.toString());
        return true;
    }

    /**
     * Get data by Date and User id
     *
     * @return String from JSON
     * @throws JsonProcessingException
     */
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping
    String getDayPlanData(@RequestParam(name = "date", required = true) String date,
                          @RequestParam(name = "role", required = true) String role,
                          @RequestParam(name = "userid", required = false) String userid,  Authentication Auth)
            throws JsonProcessingException, ParseException {
        Optional<DayPlanData> dayPlanData = Optional.empty();
        if (userid == null) {
            val authentication = SecurityContextHolder.getContext().getAuthentication();
            AadOAuth2AuthenticatedPrincipal principal = (AadOAuth2AuthenticatedPrincipal) authentication.getPrincipal();
            String name = (String) principal.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);

            if (user.isPresent()) {
                       dayPlanData = dayPlanDataRepository
                               .findById(new DayPlanDataKey(user.get(),
                                       new java.sql.Date(new SimpleDateFormat("dd-MM-yyyy")
                                               .parse(date).getTime())));

            }
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));

                if (user.isPresent()) {
                    System.out.println(user.get());

                    dayPlanData = dayPlanDataRepository
                            .findById(new DayPlanDataKey(user.get(),
                                    new java.sql.Date(new SimpleDateFormat("dd-MM-yyyy").parse(date)
                                            .getTime())));
                }
            }
        }
        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(dayPlanData.orElse(new DayPlanData())
        );
    }

}
