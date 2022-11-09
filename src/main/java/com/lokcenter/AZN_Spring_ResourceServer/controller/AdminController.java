package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.*;

/**
 * Admin Controller
 * @version 2.0 08-11-2022
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;
    /**
     * Get all user data needed for the admin panel
     * @return json reprenstation of data
     */
    @GetMapping
    ResponseEntity<String> getUserData(Authentication auth, @RequestBody Map<String, Object> payload) throws Exception {
        // extract role
        if (payload.containsKey("role")) {
            String role = (String) payload.get("role");
            if (!role.equals("ROLE_Admin")) {
                // user should not be allowed!
                return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
            } else {
                Iterable<Users> users = userRepository.findAll();

                // list of ROLE_User and username users
                List<Pair<Long, String>> userUsers = new ArrayList<>();

                for (Users user: users) {
                    // user must include ROLE_User
                    if (user.getRoles().containsKey("ROLE_User")) {
                        userUsers.add(new Pair<>(user.getUserId(), user.getUsername()));
                    }
                }

                // return data
                List<Map<String, Object>> listUserData = new ArrayList<>();

               // go over each valid user
               for (Pair<Long, String> pair: userUsers) {
                   Map<String, Object> currentUserData = new HashMap<>();

                   currentUserData.put("name", pair.getValue());
                   currentUserData.put("userId", pair.getKey());

                   // Todo: get data from current year -> UserInfo.class
                   // get userinfo from each user
                   Optional<UserInfo> userInfo = userInfoRepository.findByUserId(pair.getKey());

                   if (userInfo.isPresent()) {
                       // current year
                       Calendar calendar = Calendar.getInstance();


                       String currSickDays = userInfo.get().getSickDays()
                               .getOrDefault(String.valueOf(calendar.get(Calendar.YEAR)), "0");

                       System.out.println(currSickDays);

                       String glaz = userInfo.get().getGlazDays()
                               .getOrDefault(String.valueOf(calendar.get(Calendar.YEAR)), "0");

                       String availableVacation = userInfo.get().getAvailableVacation()
                               .getOrDefault(String.valueOf(calendar.get(Calendar.YEAR)), "0");

                       // add values
                       currentUserData.put("sick", Integer.parseInt(currSickDays));
                       currentUserData.put("glaz", Integer.parseInt(glaz));
                       currentUserData.put("availableVacation", Integer.parseInt(availableVacation));
                   }

                    listUserData.add(currentUserData);
               }
                // TODO: GET Sick, Glaz, available vacation
                // TODO: Get requests
                // TODO: Get Zeitkonto
                return new ResponseEntity<>(new ObjectMapper().writer().
                        withDefaultPrettyPrinter()
                        .writeValueAsString(listUserData), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("", HttpStatus.CONFLICT);
        }
    }

    /**
     * Get YearPlan Data by individual users
     * @param userId User id from the requested user
     *
     * @return json data from an user
     */
    @GetMapping( "/years")
    String getYearsPlanInfoByUser(@RequestParam(name = "userid", required = true) String userId) {
        return "";
    }

    /**
     * update user data by user
     * @param userId User id from the requested user
     *
     * @return boolean
     */
    @PutMapping("/userdata")
    Boolean changeUserData(@RequestParam(name = "userid") String userId) {
        return true;
    }

    /**
     * Get all requests by User
     * @param userId User id from the requested user
     *
     * @return json data from an user
     */
    @GetMapping("/requests")
    String getRequestsByUser(@RequestParam(name = "userId") String userId) {
        return "";
    }
}