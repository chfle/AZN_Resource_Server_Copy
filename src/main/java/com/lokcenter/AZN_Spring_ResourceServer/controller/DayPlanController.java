package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.*;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.*;
import com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes.DayTime;
import com.lokcenter.AZN_Spring_ResourceServer.helper.TimeConvert;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.AznStrings;
import com.lokcenter.AZN_Spring_ResourceServer.services.MemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

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
    private GeneralVacationRepository generalVacationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemService memService;

    @Autowired
    private MonthPlanRepository monthPlanRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private WorkTimeRepository workTimeRepository;

    /**
     * Post User date
     *
     * @param data user data
     * @return boolean
     */
    Optional<DayTime> setDayTimeData(Map<String, Object> data) {
        Optional<DayTime> optionalDateTime = Optional.empty();
        SimpleDateFormat sdf = new SimpleDateFormat("k:m");

        try {
            DayTime dayTime = new DayTime();

            dayTime.setStart(new Time(sdf.parse((String) data.get("start_time")).getTime()));
            dayTime.setEnd(new Time(sdf.parse((String) data.get("end_time")).getTime()));
            dayTime.setPause(new Time(sdf.parse((String) data.get("pause")).getTime()));


            optionalDateTime = Optional.of(dayTime);
        } catch (Exception ignore) {

        }

        return optionalDateTime;
    }

    /**
     * Validate dayplan data time values
     *
     * @param data dayplan data
     * @implNote Incorrect time will not be checked (only empty fields)
     * @implNote Ignore Time if school is set
     */
    boolean validDayPlanData(Map<String, Object> data) {
        try {
            // if school is set time should not be validated
            if ((Boolean) data.get("school")) {
                return true;
            }

            // check if the following things are present
            return ((String) data.get("start_time")).trim().length() != 0
                    && ((String) data.get("end_time")).trim().length() != 0
                    && ((String) data.get("pause")).trim().length() != 0
                    && timeValueValid((String) data.get("start_time"))
                    && timeValueValid((String) data.get("end_time"))
                    && timeValueValid((String) data.get("pause"));
        } catch (Exception ignore) {
            return false;
        }
    }

    boolean timeValueValid(String timeAsString) throws ParseException {
        var smp = new SimpleDateFormat("hh:mm");

        LocalTime time = LocalTime.from(smp.parse(timeAsString).toInstant().atZone(ZoneId.systemDefault()));

        return !time.isBefore(LocalTime.of(0, 1));
    }

    Boolean isGeneralVacationDay(Date date) {
        return generalVacationRepository.getGeneralVacationByDate(date).isPresent();
    }

    /**
     * Post new dayplan from user
     *
     * @param data Dayplan Data
     * @return true or false
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping()
    @Transactional
    boolean postDayPlan(@RequestBody Map<String, Object> data, Authentication auth) {

        // saved in memcached or in db
        boolean saved = false;
        try {
            Jwt jwt = (Jwt) auth.getPrincipal();
            String name = jwt.getClaim("unique_name");
            Optional<Users> user = userRepository.findByUsername(name);

            // current date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("k:m");
            Date d = new Date(simpleDateFormat.parse((String) data.get("date")).getTime());


            if (user.isPresent()) {
                // check if glaz or vacation was altered by a hacker
                Optional<DayPlanData> tdp = dayPlanDataRepository.findByDateAndUserId(d, user.get().getUserId());

                if (tdp.isPresent()) {
                    // if dpd is present get the glaz and vacation values from the database
                    data.put("glaz", tdp.get().getGlaz());
                    data.put("vacation", tdp.get().getVacation());
                } else {
                    // there is no dpd, so we can't have any glaz or vacation
                    data.put("glaz", false);
                    data.put("vacation", false);
                }


                // should be empty if no valid data was found
                Optional<DayPlanData> optionalDayPlanData = Optional.empty();
                // see if user has some checked values for this day
                // if glaz, school, sick, vacation is checked ignore all time values
                Calendar calendar = Calendar.getInstance();

                calendar.setTime(d);

                // check if weekend
                if ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                        || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                        && ((Boolean) data.get("sick") || (Boolean) data.get("school"))) {
                    return false;
                }

                // if not a general vacation vacation day
                if ((Boolean) data.get("sick") || (Boolean) data.get("glaz") || (Boolean) data.get("vacation")) {
                    var dpd = new DayPlanData();

                    dpd.setGlaz((Boolean) data.get("glaz"));
                    dpd.setSick((Boolean) data.get("sick"));
                    dpd.setSchool((Boolean) data.get("school"));
                    dpd.setVacation((Boolean) data.get("vacation"));
                    dpd.setUsers(user.get());
                    dpd.setUserId(user.get().getUserId());
                    dpd.setSetDate(d);
                    dpd.setComment((String) data.get("comment"));

                    memService.deleteKeyValue(AznStrings.toString(new DayPlanDataKey(dpd.getUserId(), dpd.getSetDate())));
                    ;

                    // set dayplan data to valid
                    dpd.setValid(true);

                    optionalDayPlanData = Optional.of(dpd);
                    // if day is in general vacation dayplan data should be ignored and not pushed
                } else if (isGeneralVacationDay(d)) {
                    optionalDayPlanData = Optional.empty();
                } else {
                    // check if we have something in memcached
                    DayPlanDataKey dayPlanDataKey = new DayPlanDataKey();

                    dayPlanDataKey.setSetDate(d);
                    dayPlanDataKey.setUserId(user.get().getUserId());

                    Object obj = memService.getKeyValue(AznStrings.toString(dayPlanDataKey));

                    if (obj != null) {
                        var cachedDayPlanData = (DayPlanData) obj;

                        // check if post data is valid
                        if (validDayPlanData(data)) {
                            // change time
                            Optional<DayTime> dayTime = setDayTimeData(data);

                            cachedDayPlanData.setWorkTime(dayTime.get());
                            cachedDayPlanData.setComment((String) data.get("comment"));

                            // check if soll
                            if ((Boolean) data.get("school")) {
                                cachedDayPlanData.setSchool(true);
                            }

                            // mark dayplan as valid
                            cachedDayPlanData.setValid(true);

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

                            dayTime.setStart(((String) data.get("start_time")).trim().length() == 0 ?
                                    null : new Time(sdf.parse((String) data.get("start_time")).getTime()));

                            dayTime.setEnd(((String) data.get("end_time")).trim().length() == 0 ?
                                    null : new Time(sdf.parse((String) data.get("end_time")).getTime()));

                            dayTime.setPause(((String) data.get("pause")).trim().length() == 0 ?
                                    null : new Time(sdf.parse((String) data.get("pause")).getTime()));


                            dayPlanData.setWorkTime(dayTime);
                            dayPlanData.setComment((String) data.get("comment"));

                            // mark dayplan data as not valid
                            dayPlanData.setValid(false);

                            // save data to memcached
                            memService.storeKeyValue(AznStrings.toString(dayPlanDataKey), dayPlanData);
                            saved = true;
                        } else {
                            // create valid data object

                            dayPlanData.setWorkTime(setDayTimeData(data).get());

                            // mark dayplan data as valid
                            dayPlanData.setValid(true);
                            optionalDayPlanData = Optional.of(dayPlanData);
                        }
                    }

                }


                // check if Dayplan Data is valid
                if ((optionalDayPlanData.isPresent() && optionalDayPlanData.get().isValid()) || isGeneralVacationDay(d)) {

                    optionalDayPlanData.ifPresent(dayPlanData -> dayPlanData.setUuid(UUID.randomUUID()));
                    // check if dayplan is inside a checked month
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);

                    Optional<MonthPlan> optionalMonthPlan = monthPlanRepository.
                            findMonthPlanByMonthAndYear(c.get(Calendar.MONTH) + 1,
                                    c.get(Calendar.YEAR), user.get().getUserId());


                    // set checked values
                    try {
                        optionalDayPlanData.get().setGlaz((Boolean) data.get("glaz"));
                        optionalDayPlanData.get().setSick((Boolean) data.get("sick"));
                        optionalDayPlanData.get().setSchool((Boolean) data.get("school"));
                    } catch (Exception ignore) {
                    }

                    if (optionalMonthPlan.isEmpty()) {
                        // add or remove vacation days
                        boolean currentSickFromUser = false;

                        try {
                            currentSickFromUser = (boolean) data.get("sick");
                        }catch (Exception e){}

                        if (isGeneralVacationDay(d) && !currentSickFromUser)  {
                            dayPlanDataRepository.deleteByUserIdAndSetDate(user.get().getUserId(), d);
                        } else {
                            optionalDayPlanData.ifPresent(dayPlanData -> dayPlanDataRepository.save(dayPlanData));
                        }
                        saved = true;

                    }

                }

                return saved;
            }

        } catch (Exception exception) {
            exception.printStackTrace();

            return false;
        }

        return saved;
    }

    private Optional<DayPlanData> getDayPlanDataByUserAndDate(Optional<Users> user, Date date) throws IOException {
        Optional<DayPlanData> dayPlanData = Optional.empty();
        boolean cached = false;

        if (user.isPresent()) {
            var dayPlanDataKey = new DayPlanDataKey(user.get().getUserId(), date);

            // check general vacation for requested day
            Optional<GeneralVacation> optionalGeneralVacation = generalVacationRepository.getGeneralVacationByDate(date);
            dayPlanData = dayPlanDataRepository.findByDateAndUserId(date, user.get().getUserId());


            if (optionalGeneralVacation.isPresent()) {
                var dpdTemp = new DayPlanData();

                // must be set for later usage
                dpdTemp.setSetDate(date);
                dpdTemp.setUsers(user.get());
                dpdTemp.setUserId(dayPlanDataKey.getUserId());

                switch (optionalGeneralVacation.get().getTag()) {
                    case gUrlaub -> dpdTemp.setVacation(true);
                    case gFeiertag -> dpdTemp.setHoliday(true);
                }

                if (dayPlanData.isEmpty()) {
                    dayPlanData = Optional.of(dpdTemp);
                } else {
                    dayPlanData.get().setHoliday(dpdTemp.getHoliday());
                    dayPlanData.get().setVacation(dpdTemp.getVacation());
                }
            } else {
                // check if day plan data is in memcached
                Object obj = memService.getKeyValue(AznStrings.toString(dayPlanDataKey));

                if (obj != null) {
                    dayPlanData = Optional.of((DayPlanData) obj);
                    cached = true;
                } else {
                    dayPlanData = dayPlanDataRepository.findById(dayPlanDataKey);
                }
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
                          @RequestParam(name = "userid", required = false) String userid, Authentication auth)
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
