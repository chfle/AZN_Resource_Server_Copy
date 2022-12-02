package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.MonthPlanKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.MonthPlanRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.MonthPlan;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.TimeConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/monthplan")
public class MonthPlanController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneralVacationRepository generalVacationRepository;

    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private MonthPlanRepository monthPlanRepository;

    private Map<String, Object> getDayPlanDataByUserAndDate(Optional<Users> user, java.sql.Date date) throws IOException, ParseException {
        Optional<DayPlanData> dayPlanData = Optional.empty();

        if (user.isPresent()) {
            var dayPlanDataKey = new DayPlanDataKey(user.get().getUserId(), date);

            // check general vacation for requested day
            Optional<GeneralVacation> optionalGeneralVacation = generalVacationRepository.getGeneralVacationByDate(date);

            if (optionalGeneralVacation.isPresent()) {
                var dpdTemp = new DayPlanData();

                // must be set for later usage
                dpdTemp.setSetDate(date);
                dpdTemp.setUsers(user.get());

                switch (optionalGeneralVacation.get().getTag()) {
                    case gUrlaub -> dpdTemp.setVacation(true);
                    case gFeiertag -> dpdTemp.setHoliday(true);
                }
                dayPlanData = Optional.of(dpdTemp);
            } else {
                    dayPlanData = dayPlanDataRepository.findById(dayPlanDataKey);
            }
        }

       if (dayPlanData.isPresent()) {
           var dpd = dayPlanData.get();

           return new HashMap<>(Map.of(
                   "start", dpd.getWorkTime() != null ? dpd.getWorkTime().getStart(): "",
                   "end", dpd.getWorkTime() != null ? dpd.getWorkTime().getEnd(): "",
                   "pause", dpd.getWorkTime() != null ? dpd.getWorkTime().getPause(): "",
                   "glaz", dpd.getGlaz(),
                   "sick", dpd.getSick(),
                   "vacation", dpd.getVacation(),
                   "holiday", dpd.getHoliday(),
                   "school", dpd.getSchool(),
                   "comment", dpd.getComment()));
       }

       return new HashMap<>();
    }

    // go over each day one by one
    private List<Map<String, Object>> getDayPlansOfMonth(String month, String year, Optional<Users> user) throws ParseException, IOException {
        List<Map<String, Object>> dpd = new ArrayList<>();

        if (user.isPresent()) {
            // convert to date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = sdf.parse(String.format("1/%s/%s", month, year));

            System.out.println(year);
            System.out.println(month);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            // get the last day of month
            int lastDayMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            // go over month
            for (LocalDate i = TimeConvert.convertToLocalDateViaInstant(date);
                 !i.isAfter(TimeConvert.
                         convertToLocalDateViaInstant(sdf.parse(String.format("%s/%s/%s",lastDayMonth, month, year)))); i = i.plusDays(1))
            {
                Map<String, Object> dayPlanDataOptional =
                        getDayPlanDataByUserAndDate(user,

                                new java.sql.Date(TimeConvert.convertToDateViaInstant(i).getTime()));

               dpd.add(dayPlanDataOptional);
            }
        }

        return dpd;
    }

    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping
    String getMonthPlan( @RequestParam(name = "month", required = true) String month,
                         @RequestParam(name = "year", required = true) String year,
                         @RequestParam(name = "role", required = true) String role,
                         @RequestParam(name = "userid", required = false) String userid,  Authentication auth) throws IOException, ParseException {

        List<Map<String, Object>> monthData = new ArrayList<>();

        if (userid == null) {
            Jwt jwt = (Jwt) auth.getPrincipal();

            String name = jwt.getClaim("unique_name");

            // get userId;
            Optional<Users> user = userRepository.findByUsername(name);
            monthData = getDayPlansOfMonth(month, year, user);
        } else {
            // user must be admin to use userid
            if (role.equals("ROLE_Admin")) {
                Optional<Users> user = userRepository.findById(Long.valueOf(userid));
                monthData = getDayPlansOfMonth(month, year, user);
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(monthData);
    }

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
                MonthPlan monthPlan = new MonthPlan();
                monthPlan.setUsers(user.get());
                monthPlan.setUserId(user.get().getUserId());
                monthPlan.setSubmitted(true);
                monthPlan.setAccepted(false);
                monthPlan.setYear((Integer) payload.get("year"));
                monthPlan.setMonth(Integer.parseInt((String)payload.get("month")));

                monthPlanRepository.save(monthPlan);

               return monthPlanRepository.findById(new MonthPlanKey(
                       user.get().getUserId(),
                       (Integer) payload.get("year") ,
                       Integer.parseInt((String)payload.get("month")))).isPresent();
            } else {
                return false;
            }
        }catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
