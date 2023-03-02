package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.MonthPlanKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.MessagesRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.MonthPlanRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Messages;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.MonthPlan;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.services.MonthPlanService;
import com.lokcenter.AZN_Spring_ResourceServer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Monthplan related controller
 */

@RestController
@RequestMapping("/monthplan")
public class MonthPlanController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MonthPlanRepository monthPlanRepository;

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private MonthPlanService monthPlanService;

    @Autowired
    private UserService userService;

    private Map<String, Object> getStatus(Optional<Users> user, Map<String, Object> payload) {
        Map<String, Object> ret = new HashMap<>();
        if (user.isPresent()) {
            var month = monthPlanRepository.findById(new MonthPlanKey(
                    user.get().getUserId(),
                    Integer.parseInt((String)payload.get("year")) ,
                    Integer.parseInt((String)payload.get("month"))));

            if (month.isPresent()) {
                ret.put("submitted", month.get().getSubmitted());
                ret.put("accepted", month.get().getAccepted());
            }
        }

        return ret;
    }

    /**
     * Get monthplan by userid or user
     * @param month requested month
     * @param year requested year
     * @param role role if admin
     * @param userid userid if admin
     *
     * @return Json string of data
     *
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping
    String getMonthPlan( @RequestParam(name = "month", required = true) String month,
                         @RequestParam(name = "year", required = true) String year,
                         @RequestParam(name = "role", required = true) String role,
                         @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws IOException, ParseException, ExecutionException, InterruptedException {

        CompletableFuture<List<Map<String, Object>>> monthData = new CompletableFuture<>();

        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;

            monthData = monthPlanService.getDayPlansOfMonth(month, year, userService.findByName(name));
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                monthData = monthPlanService.getDayPlansOfMonth(month, year, userService.findById(Long.valueOf(userid)));
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(monthData.get());
    }

    /**
     * Submit Monthplan
     *
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PutMapping("/submit")
    @ResponseBody
    Boolean submitMonthPlan(@RequestBody Map<String, Object> payload, Authentication auth) {
        try {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);

            if (user.isPresent()) {
                var MK = new MonthPlanKey(
                        user.get().getUserId(),
                        Integer.parseInt((String)payload.get("year")) ,
                        (Integer) payload.get("month"));

                // check if monthplan exists
                if (monthPlanRepository.findById(MK).isPresent()) {
                    monthPlanRepository.deleteById(MK);
                }

                MonthPlan monthPlan = new MonthPlan();
                monthPlan.setUsers(user.get());
                monthPlan.setUserId(user.get().getUserId());
                monthPlan.setSubmitted(true);
                monthPlan.setAccepted(false);
                monthPlan.setMonth((Integer) payload.get("month"));
                monthPlan.setYear(Integer.parseInt((String)payload.get("year")));

                monthPlanRepository.save(monthPlan);

               return monthPlanRepository.findById(new MonthPlanKey(
                       user.get().getUserId(),
                       Integer.parseInt((String)payload.get("year")) ,
                       (Integer) payload.get("month"))).isPresent();
            } else {
                return false;
            }
        }catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Show status of monthplan
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping("/status")
    String getSubmittedStatus(@RequestBody Map<String, Object> payload, Authentication auth) throws JsonProcessingException {
        Map<String, Object> ret = new HashMap<>();
       try {
           if (!payload.containsKey("userId")) {
               Jwt jwt = (Jwt) auth.getPrincipal();

               String name = jwt.getClaim("unique_name");

               // get userId;
               Optional<Users> user = userRepository.findByUsername(name);
               ret = getStatus(user, payload);
           } else {
               if (payload.containsKey("role") && payload.containsKey("userId")) {
                   if (payload.get("role").equals("ROLE_Admin")) {
                       Optional<Users> user = userRepository.findById(Long.parseLong((String)payload.get("userId")));
                       ret = getStatus(user, payload);
                   }
               }
           }
       }catch (Exception exception) {
          exception.printStackTrace();
       }

       return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(ret);
    }

    /**
     * get all messages by user for a month
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping("/messages")
    String getMessagesByUserAndMonth(@RequestBody Map<String, Object> payload, Authentication auth) throws JsonProcessingException {
        List<Map<String, Object>> resData = new ArrayList<>();

        try {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);

            if (user.isPresent()) {
                Iterable<Messages> messages =  messagesRepository.findMessagesByUserIdAndYearAndMonth(
                        user.get().getUserId(), (String)payload.get("year"), (String)payload.get("month"));

                // get only message date messageId from message
                for (var message: messages) {
                    resData.add(new HashMap<>(
                            Map.of("message", message.getMessage(),
                                    "date", message.getDate(),
                                    "messageId", message.getMessageId())));
                }
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(resData);
    }
}
