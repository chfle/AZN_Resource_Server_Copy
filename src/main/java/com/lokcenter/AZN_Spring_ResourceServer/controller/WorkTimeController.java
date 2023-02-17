package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.WorkTimeRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Get Worktime related stuff
 */

@RestController
@RequestMapping("/worktime")
public class WorkTimeController {
    @Autowired
    private WorkTimeRepository workTimeRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Query soll value
     */
    String getSoll(Optional<Users> users) {
        if (users.isPresent()) {
            Optional<String> workTime = workTimeRepository.getMostRecentSollByUser(users.get());
            if (workTime.isPresent()) {
                return workTime.get();
            }
        }

        return "";
    }

    /**
     * Get current soll value from user
     */
    @GetMapping("/soll")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    String getSollTime(@RequestParam(name = "role", required = true) String role,
                       @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws JsonProcessingException {

        String soll = "";

        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);
            soll = getSoll(user);


        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                soll = getSoll(user);
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(soll);
    }
}
