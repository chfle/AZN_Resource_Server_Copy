package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.AznStrings;
import com.lokcenter.AZN_Spring_ResourceServer.services.MemService;
import net.bytebuddy.build.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @Autowired
    private MemService memService;
    /**
     * Post User date
     * @param data user data
     * @return boolean
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping()
    boolean postDayPlan(@RequestBody Map<String, Object> data, Authentication auth) {
        try {
            Jwt jwt = (Jwt) auth.getPrincipal();
            String name = jwt.getClaim("unique_name");
            Optional<Users> user = userRepository.findByUsername(name);

            // current date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date d = new Date(simpleDateFormat.parse((String)data.get("date")).getTime());

            if (user.isPresent()) {
                // should be empty if no valid data was found
                Optional<DayPlanData> optionalDayPlanData = Optional.empty();
                // see if user has some checked values for this day
                // if glaz, school, sick, vacation is checked ignore all time values
                if ((Boolean)data.get("school") || (Boolean)data.get("sick") || (Boolean)data.get("vacation") || (Boolean)data.get("glaz")) {
                    var dpd = new DayPlanData();

                    dpd.setGlaz((Boolean)data.get("glaz"));
                    dpd.setSick((Boolean)data.get("sick"));
                    dpd.setVacation((Boolean)data.get("vacation"));
                    dpd.setSchool((Boolean)data.get("school"));
                    dpd.setUsers(user.get());
                    dpd.setUserId(user.get().getUserId());
                    dpd.setSetDate(d);

                    optionalDayPlanData = Optional.of(dpd);
                } else {
                    // get all data
                    // check if we have something in memcached

                    // if all important fields are filled -> push to postgres
                }

                // check if Dayplan Data is valid
                optionalDayPlanData.ifPresent(dayPlanData -> dayPlanDataRepository.save(dayPlanData));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // TODO: Get and validate data
        // TODO: Check if object is in memcached
        // TODO: if all data is present push data to db
        System.out.println(data.toString());
        return true;
    }

    private Optional<DayPlanData> getDayPlanDataByUserAndDate(Optional<Users> user, Date date) throws IOException {
        Optional<DayPlanData> dayPlanData = Optional.empty();
        boolean cached = false;

        if (user.isPresent()) {
            var dayPlanDataKey = new DayPlanDataKey(user.get().getUserId(), date);

            // check if day plan data is in memcached
            Object obj = memService.getKeyValue(AznStrings.toString(dayPlanDataKey));

            if (obj != null) {
                dayPlanData = Optional.of((DayPlanData)obj);
                cached = true;
            } else {
                dayPlanData = dayPlanDataRepository.findById(dayPlanDataKey);
            }
        }



        // cache DayPlanData
        if (dayPlanData.isPresent() && !cached) {
            memService.storeKeyValue(AznStrings.toString(
                    new DayPlanDataKey(dayPlanData.get().getUsers().getUserId(),
                            dayPlanData.get().getSetDate())), dayPlanData.get());
        }

        return dayPlanData;
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
                          @RequestParam(name = "userid", required = false) String userid,  Authentication auth)
            throws IOException, ParseException {
        Optional<DayPlanData> dayPlanData = Optional.empty();
        Date askedDate = new java.sql.Date(new SimpleDateFormat("dd-MM-yyyy")
                                               .parse(date).getTime());
        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);
            dayPlanData = getDayPlanDataByUserAndDate(user, askedDate);
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                dayPlanData = getDayPlanDataByUserAndDate(user, askedDate);
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(dayPlanData.orElse(new DayPlanData())
        );
    }
}
