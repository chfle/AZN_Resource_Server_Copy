package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.RequestsRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.YearOverViewList;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private RequestsRepository requestsRepository;

    @Autowired
    private YearOverViewList yearOverViewList;

    /**
     * Get all user data needed for the admin panel
     * @return json reprenstation of data
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
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

                   // get total requests
                   Iterable<Requests> requests = requestsRepository.findByUserId(pair.getKey());

                   currentUserData.put("requests", requests.spliterator().getExactSizeIfKnown());

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
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getYearsPlanInfoByUser(@RequestParam(name = "userid", required = true) String userId) throws JsonProcessingException {
        // find user by id
        Optional<Users> user = userRepository.findById((long) Integer.parseInt(userId));

        if (user.isPresent()) {
            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(yearOverViewList.getYearsListByUser(user.get()));
        }

        return "";
    }

    /**
     * update user data by user
     * @param userId User id from the requested user
     *
     * @return boolean
     */
    @PutMapping("/userdata")
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
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
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    String getRequestsByUser(@RequestParam(name = "userId") String userId) throws JsonProcessingException {
        Optional<Users> user = userRepository.findById(Long.valueOf(userId));

        if (user.isPresent()) {
            Iterable<Requests> requestsByUser = requestsRepository.findByUserId(user.get().getUserId());

            List<Map<String, Object>> shortedRequestsData = new ArrayList<>();

            for (Requests requests: requestsByUser) {
                var dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                shortedRequestsData.add(new HashMap<>(

                Map.of(
                                "tag", requests.getType().name(),
                                "startdate", dateFormat.format(requests.getStartDate()),
                                "enddate", dateFormat.format(requests.getEndDate())
                )));
            }


            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(shortedRequestsData);
        }
        return "";
    }

    @PutMapping("/requests/accept")
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    Boolean acceptRequestByUser(@RequestParam(name = "startDate", required = true) String startDate,
                                @RequestParam(name = "endDate", required = true) String endDate,
                                @RequestParam(name = "userid", required = true) String userId) {

        Optional<Users> users = userRepository.findById(Long.parseLong(userId));

        if (users.isPresent()) {

        }

        return false;
    }

    @DeleteMapping("/requests/delete")
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    Boolean deleteRequestByUser(@RequestParam(name = "startDate", required = true) String startDate,
                                @RequestParam(name = "endDate", required = true) String endDate,
                                @RequestParam(name = "userid", required = true) String userId) throws Exception {

        Optional<Users> users = userRepository.findById(Long.parseLong(userId));

        if (users.isPresent()) {
            var formatter = new SimpleDateFormat("dd-MM-yyyy");

            requestsRepository.deleteRequestsByStartDateAndEndDateAndUsers(
                    new Date(formatter.parse(startDate).getTime()),
                    new Date(formatter.parse(endDate).getTime()),
                    users.get().getUserId());

            return requestsRepository.findRequestsByStartDateAndEndDateAndUsers( new Date(formatter.parse(startDate).getTime()),
                    new Date(formatter.parse(endDate).getTime()),
                    users.get().getUserId()).isEmpty();
        }

        return false;
    }
}