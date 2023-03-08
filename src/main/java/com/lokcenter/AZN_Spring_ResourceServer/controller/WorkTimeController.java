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

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    String getSoll(Optional<Users> users, Date date) {
        if (users.isPresent()) {
            Optional<String> workTime = workTimeRepository.getMostRecentSollByUserAndDate(users.get(), date);
            if (workTime.isPresent()) {
                return workTime.get();
            }
        }

        return "";
    }

    /**
     * Get soll by month
     * @param month requested month
     *
     * @return list of soll
     */
    List<String> getSollByMonth(Optional<Users> users, int month, int year) throws ParseException {
        List<String> sollList = new ArrayList<>();

        if (users.isPresent()) {
            Calendar calendar = Calendar.getInstance();

            // set with requested params
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);

            // get last day of month
            int last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            // get worktime for every day
            for (int currDayI = 1; currDayI <= last; currDayI++) {
                // create new date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
                java.sql.Date currDate = new java.sql.Date(sdf.parse(
                        String.format("%s-%s-%s", currDayI, month +1, year)).getTime());

                var optionalSoll  = workTimeRepository.
                        getMostRecentSollByUserAndDate(users.get(), currDate);

                if (optionalSoll.isPresent()) {
                    sollList.add(optionalSoll.get());
                } else {
                    sollList.add("00:00");
                }
            }

        }

        return sollList;
    }

    /**
     * Get soll time for every day of a given month
     * @param role user or admin
     * @param userid userid if admin
     * @param month requested month
     * @return json string with list of soll
     */
    @GetMapping("/sollMonth")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    String getSollByMonth(@RequestParam(name = "role", required = true) String role,
                          @RequestParam(name = "userid", required = false) String userid,
                          @RequestParam(name = "month", required = true) String month,
                          @RequestParam(name = "year", required = true) String year, Authentication auth) throws JsonProcessingException, ParseException {

        List<String> solls = new ArrayList<>();


        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);
            solls = getSollByMonth(user, Integer.parseInt(month), Integer.parseInt(year));
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                solls = getSollByMonth(user, Integer.parseInt(month), Integer.parseInt(year));
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(solls);
    }

    /**
     * Get current soll value from user and by day
     */
    @GetMapping("/soll")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    String getSollTime(@RequestParam(name = "role", required = true) String role,
                       @RequestParam(name = "userid", required = false) String userid,
                       @RequestParam(name = "date", required = true) String date, Authentication auth) throws JsonProcessingException, ParseException {

        String soll = "";
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date dt = new Date(df.parse(date).getTime());


        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);
            soll = getSoll(user, dt);


        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                soll = getSoll(user, dt);
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(soll);
    }
}
