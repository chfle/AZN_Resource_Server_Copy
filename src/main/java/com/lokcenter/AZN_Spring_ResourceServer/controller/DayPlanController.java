package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.azure.spring.cloud.autoconfigure.aad.implementation.oauth2.AadOAuth2AuthenticatedPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
                          @RequestParam(name = "userid", required = false) String userid,
                          @RequestParam(name = "role", required = true) String role)
            throws JsonProcessingException {

        // TODO: get userid from users table or use userid if user is an admin

        if (userid == null) {
            val authentication = SecurityContextHolder.getContext().getAuthentication();
            AadOAuth2AuthenticatedPrincipal principal = (AadOAuth2AuthenticatedPrincipal) authentication.getPrincipal();
            String name = (String) principal.getClaim("unique_name");
            System.out.println(principal.getClaims());
        } else {
            // user must be admin to use userid

        }
        return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(new DayPlanData());
    }

}
