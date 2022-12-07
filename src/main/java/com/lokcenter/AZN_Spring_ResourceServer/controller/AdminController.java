package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.MonthPlanKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.*;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.*;
import com.lokcenter.AZN_Spring_ResourceServer.helper.TimeConvert;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.YearOverViewList;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.tuple.Tuple;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.tuple.TupleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * Admin Controller
 * @version 3.0 1-12-2022
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

    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private GeneralVacationRepository generalVacationRepository;

    @Autowired
    private DefaultsRepository defaultsRepository;

    @Autowired
    private MonthPlanRepository monthPlanRepository;


    public static TupleType TupleThreeType = TupleType.DefaultFactory.create(
            Long.class,
            String.class,
            String.class);

    /**
     * Get all user data needed for the admin panel
     * @return json reprenstation of data
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    ResponseEntity<String> getUserData(@RequestBody Map<String, Object> payload) throws Exception {
        // extract role and group e.g. KBM / IT (role)
        if (payload.containsKey("role")) {
            String role = (String) payload.get("role");
            if (!role.equals("ROLE_Admin")) {
                // user should not be allowed!
                return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
            } else {
                Iterable<Users> users = userRepository.findAll();

                // list of ROLE_User and username users
                List<Tuple> userUsers = new ArrayList<>();


                for (Users user: users) {
                    // user must include ROLE_User
                    Optional<String> department = Optional.empty();

                    if (user.getRoles().containsKey("ROLE_KBM")) {
                        department = Optional.of("KBM");
                    } else if (user.getRoles().containsKey("ROLE_IT")) {
                        department = Optional.of("IT");
                    }

                    if (user.getRoles().containsKey("ROLE_User") && department.isPresent()) {
                        userUsers.add(TupleThreeType.createTuple(user.getUserId(), user.getUsername(), department.get()));
                    }

                    // check if user as any department

                }

                // return data
                List<Map<String, Object>> listUserData = new ArrayList<>();

               // go over each valid user
               for (Tuple data: userUsers) {
                   Map<String, Object> currentUserData = new HashMap<>();

                   currentUserData.put("name", data.getNthValue(1));
                   currentUserData.put("userId", data.getNthValue(0));
                   currentUserData.put("department", data.getNthValue(2));

                   // get userinfo from each user
                   Optional<UserInfo> userInfo = userInfoRepository.findByUserId(data.getNthValue(0));

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
                   Iterable<Requests> requests = requestsRepository.findByUserId(data.getNthValue(0));

                   currentUserData.put("requests", requests.spliterator().getExactSizeIfKnown());

                   listUserData.add(currentUserData);
               }

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

    /**
     * Accept request
     */
    @PutMapping("/requests/accept")
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    Boolean acceptRequestByUser(@RequestParam(name = "startDate", required = true) String startDate,
                                @RequestParam(name = "endDate", required = true) String endDate,
                                @RequestParam(name = "userid", required = true) String userId) throws ParseException {

        Optional<Users> users = userRepository.findById(Long.parseLong(userId));

        if (users.isPresent()) {
            // get all day plans between start end date
            var formatter = new SimpleDateFormat("dd-MM-yyyy");

             // check if request exists
            Optional<Requests> requests = requestsRepository.findRequestsByStartDateAndEndDateAndUsers(
                    new Date(formatter.parse(startDate).getTime()),
                    new Date(formatter.parse(endDate).getTime()),
                    users.get().getUserId());

            if (requests.isPresent()) {
                java.util.Date startDateDate = new java.util.Date(formatter.parse(startDate).getTime());
                java.util.Date endDateDate = new java.util.Date(formatter.parse(endDate).getTime());

                Iterable<DayPlanData> dayPlanData = dayPlanDataRepository.
                        getDayPlanDataBySetDateBetweenAndUserId(
                                new Date(formatter.parse(startDate).getTime()),
                                new Date(formatter.parse(endDate).getTime()),
                                users.get().getUserId());

                Iterable<GeneralVacation> generals = generalVacationRepository.getGeneralVacationByDateBetween(
                        new Date(formatter.parse(startDate).getTime()),
                        new Date(formatter.parse(endDate).getTime())
                );

                // check if holiday or general holiday is set
                if (StreamSupport.stream(generals.spliterator(), false).findAny().isEmpty()) {

                    // check if vacation, galz, urlaub, school is set
                    for (DayPlanData dpd : dayPlanData) {
                        if (dpd.getGlaz() || dpd.getSchool() || dpd.getVacation()) {
                            return false;
                        }
                    }

                    var start = TimeConvert.convertToLocalDateViaInstant(startDateDate);
                    var end = TimeConvert.convertToLocalDateViaInstant(endDateDate);

                    // generate uuid
                    UUID uuid = UUID.randomUUID();

                    // go over each day from start to end and set request value
                    for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                        System.out.println(date);
                        // get day
                        DayPlanData dpd;
                        Optional<DayPlanData> day =
                                dayPlanDataRepository.getBySetDateAndUserId(new Date(TimeConvert.convertToDateViaInstant(date).
                                        getTime()), users.get().getUserId());

                        if (day.isEmpty()) {
                            dpd = new DayPlanData();

                            dpd.setUserId(users.get().getUserId());
                            dpd.setSetDate(new Date(TimeConvert.convertToDateViaInstant(date).getTime()) );
                        } else {
                            dpd = day.get();
                        }

                        // set request value
                        switch (requests.get().getType()) {
                            case rGLAZ -> dpd.setGlaz(true);
                            case rUrlaub -> dpd.setVacation(true);
                        }

                        // set uuid
                        dpd.setUuid(uuid);

                        // save dpd
                        dayPlanDataRepository.save(dpd);
                    }

                    // delete requests
                    requestsRepository.deleteRequestsByStartDateAndEndDateAndUsers(
                            requests.get().getStartDate(),
                            requests.get().getEndDate(),
                            users.get().getUserId());

                    return requestsRepository.findRequestsByStartDateAndEndDateAndUsers( new Date(formatter.parse(startDate).getTime()),
                            new Date(formatter.parse(endDate).getTime()),
                            users.get().getUserId()).isEmpty();
                }
            }
        }

        return false;
    }

    /**
     * Delete request from user
     * @param startDate request startdate
     * @param endDate request enddate
     * @param userId user
     */
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

            // check if request was deleted
            return requestsRepository.findRequestsByStartDateAndEndDateAndUsers( new Date(formatter.parse(startDate).getTime()),
                    new Date(formatter.parse(endDate).getTime()),
                    users.get().getUserId()).isEmpty();
        }

        return false;
    }

    /**
     * Add User default values
     * @param payload default data
     * @return request success value
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping("defaults/add")
    @ResponseBody
    Boolean addDefaultValues(@RequestBody Map<String, Object> payload) {
       try {
          Defaults defaults = new Defaults();

          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
          SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

          defaults.setDefaultStartDate(new Date(simpleDateFormat.parse((String)payload.get("start_date")).getTime()));
          defaults.setDefaultStartTime(new Time(timeFormat.parse((String)payload.get("start_time")).getTime()));
          defaults.setDefaultEndTime(new Time(timeFormat.parse((String)payload.get("end_time")).getTime()));
          defaults.setDefaultPause(new Time(timeFormat.parse((String)payload.get("pause")).getTime()));

          defaults.setDefaultVacationDays(Integer.parseInt((String)payload.get("vacation")));

          defaultsRepository.save(defaults);

          // check if data was saved
          return  defaultsRepository.findById(defaults.getDefaultStartDate()).isPresent();
       }catch (Exception ignore) {
           return false;
       }
    }

    /**
     * Get all defaults
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping("defaults/get")
    @ResponseBody
    String getDefaults() throws JsonProcessingException {
        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(defaultsRepository.findAll());
    }

    /**
     * Consume monthplan if pressent and user has monthplan submitted
     */
    AtomicBoolean monthPlanIfPresentAndSubmitted(Consumer<? super MonthPlan> consumer, Users user, int year, int month) {
      AtomicBoolean changed = new AtomicBoolean(false);
       var MK = new MonthPlanKey(user.getUserId(), year , month);

       monthPlanRepository.findById(MK).ifPresent((e) -> {
           if (e.getSubmitted()) {
               consumer.accept(e);
               changed.set(true);
           }
       });

       return changed;
    }

    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @DeleteMapping("defaults/delete")
    @ResponseBody
    Boolean deleteDefaults(@RequestBody Map<String, Object> payload) {
        try {
            var spl = new SimpleDateFormat("dd.MM.yyyy");

            defaultsRepository.deleteById(new Date(spl.parse((String) payload.get("start_date")).getTime()));

            return defaultsRepository.findById(new Date(spl.parse((String) payload.get("start_date")).getTime())).isEmpty();
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * Accept month plan
     * @param payload month plan data
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PutMapping("azn/accept")
    @ResponseBody
    Boolean aznAccept(@RequestBody Map<String, Object> payload) {
        try {
            Optional<Users> user  = userRepository.findById(Long.parseLong((String)payload.get("userid")));

            if (user.isPresent()) {
                return monthPlanIfPresentAndSubmitted((e) -> {
                    e.setAccepted(true);
                    monthPlanRepository.save(e);
                }, user.get(), Integer.parseInt((String)payload.get("year")) , (Integer) payload.get("month")).get();
            }
        }catch (Exception ignore) {}

        return false;
    }

    /**
     * Deny month plan
     * @param payload month plan data
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PutMapping("azn/deny")
    @ResponseBody
    Boolean aznDeny(@RequestBody Map<String, Object> payload) {
        try {
            Optional<Users> user  = userRepository.findById(Long.parseLong((String)payload.get("userid")));

            if (user.isPresent()) {
                return monthPlanIfPresentAndSubmitted((e) -> {
                    e.setAccepted(false);
                    e.setSubmitted(false);
                    monthPlanRepository.save(e);
                }, user.get(), Integer.parseInt((String)payload.get("year")) , (Integer) payload.get("month")).get();
            }
        }catch (Exception ignore) {}

        return false;
    }
}