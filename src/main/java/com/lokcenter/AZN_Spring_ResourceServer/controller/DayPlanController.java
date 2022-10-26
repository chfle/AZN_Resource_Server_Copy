package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes.DayTime;
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
import java.sql.Time;
import java.sql.Timestamp;
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

     Optional<DayTime> setDayTime(Map<String, Object> data) {
         Optional<DayTime> optionalDateTime = Optional.empty();
         SimpleDateFormat sdf = new SimpleDateFormat("k:m");

        try {
            DayTime dayTime = new DayTime();

            dayTime.setStart(new Time(sdf.parse((String)data.get("start_time")).getTime()));
            dayTime.setEnd(new Time(sdf.parse((String)data.get("end_time")).getTime()));
            dayTime.setPause(new Time(sdf.parse((String)data.get("pause")).getTime()));

            optionalDateTime = Optional.of(dayTime);
        }catch (Exception ignore) {

        }

        return optionalDateTime;
    }

    boolean validDayPlanData(Map<String, Object> data) {
        try {
            // check if the following things are present
            return ((String) data.get("start_time")).trim().length() != 0
                    && ((String) data.get("end_time")).trim().length() != 0
                    && ((String) data.get("pause")).trim().length() != 0;
        }catch (Exception ignore) {
            return false;
        }
    }
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping()
    boolean postDayPlan(@RequestBody Map<String, Object> data, Authentication auth) {
        try {
            Jwt jwt = (Jwt) auth.getPrincipal();
            String name = jwt.getClaim("unique_name");
            Optional<Users> user = userRepository.findByUsername(name);

            // current date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("k:m");
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
                    // check if we have something in memcached
                    DayPlanDataKey dayPlanDataKey = new DayPlanDataKey();

                    dayPlanDataKey.setSetDate(d);
                    dayPlanDataKey.setUserId(user.get().getUserId());

                    Object obj = memService.getKeyValue(AznStrings.toString(dayPlanDataKey));

                    if (obj != null) {
                        var cachedDayPlanData = (DayPlanData)obj;

                        // check if post data is valid
                        if (validDayPlanData(data)) {
                            // change time
                            Optional<DayTime> dayTime = setDayTime(data);

                            cachedDayPlanData.setWorkTime(dayTime.get());

                            optionalDayPlanData = Optional.of(cachedDayPlanData);

                            // delete invalid cached data
                            memService.deleteKeyValue(AznStrings.toString((dayPlanDataKey)));
                        }
                    } else {
                           ObjectMapper objectMapper = new ObjectMapper();
                            //if all properties are not in class use this
                            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            DayPlanData dayPlanData = objectMapper.convertValue(data, DayPlanData.class);

                            dayPlanData.setUserId(user.get().getUserId());
                            dayPlanData.setSetDate(d);
                            dayPlanData.setUsers(user.get());

                        // save to memcached if data is not valid
                        if (!validDayPlanData(data)) {
                            // set time
                            DayTime dayTime = new DayTime();

                            dayTime.setStart(((String)data.get("start_time")).trim().length() == 0 ?
                                    null : new Time(sdf.parse((String)data.get("start_time")).getTime()));

                            dayTime.setEnd(((String)data.get("end_time")).trim().length() == 0 ?
                                    null : new Time(sdf.parse((String)data.get("end_time")).getTime()));

                            dayTime.setPause(((String)data.get("pause")).trim().length() == 0 ?
                                    null : new Time(sdf.parse((String)data.get("pause")).getTime()));


                            dayPlanData.setWorkTime(dayTime);

                            // save data to memcached
                            memService.storeKeyValue(AznStrings.toString(dayPlanDataKey), dayPlanData);
                        } else {
                            // create valid data object

                            dayPlanData.setWorkTime(setDayTime(data).get());

                            optionalDayPlanData = Optional.of(dayPlanData);
                        }
                    }

                }

                // check if Dayplan Data is valid
               if (optionalDayPlanData.isPresent()) {
                    dayPlanDataRepository.save(optionalDayPlanData.get());
                    return true;
               }

              return false;
            }

        } catch (Exception exception) {
            exception.printStackTrace();

            return false;
        }

        return false;
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
