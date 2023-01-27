package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.services.BalanceService;
import org.apache.qpid.proton.codec.security.SaslOutcomeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/balance")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getBalanceTimeByUser( @RequestParam(name = "year", required = false) String year,
                                 @RequestParam(name = "role", required = true) String role,
                                 @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws JsonProcessingException, ParseException, ExecutionException, InterruptedException {

        String value = "";

        // roles and userid stuff
        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);

            value = balanceService.getBalanceTime(user, Integer.parseInt(year)).get();

        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                value = balanceService.getBalanceTime(user, Integer.parseInt(year)).get();

            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(value);
    }
}
