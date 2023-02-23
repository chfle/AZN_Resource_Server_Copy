package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.MessageTypes;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IUuidable;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IYearCount;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.GeneralVacationKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.MonthPlanKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.*;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.*;
import com.lokcenter.AZN_Spring_ResourceServer.helper.TimeConvert;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.ControllerHelper;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.YearOverViewList;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.Pair;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.tuple.Tuple;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.tuple.TupleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
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

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private ControllerHelper controllerHelper;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private WorkTimeRepository workTimeRepository;


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

                   // current year
                   Calendar calendar = Calendar.getInstance();

                   // get userinfo from each user
                   Optional<UserInfo> userInfo = userInfoRepository.findByUserId(data.getNthValue(0));
                   Long glazCount = dayPlanDataRepository.glazCountByUserAndYear(data.getNthValue(0), calendar.get(Calendar.YEAR));
                   Long sickCount = dayPlanDataRepository.sickCountByUserAndYear(data.getNthValue(0), calendar.get(Calendar.YEAR));

                   // add values
                   currentUserData.put("sick", sickCount);
                   currentUserData.put("glaz", glazCount);

                   if (userInfo.isPresent()) {
                       String availableVacation = userInfo.get().getAvailableVacation()
                               .getOrDefault(String.valueOf(calendar.get(Calendar.YEAR)), "0");

                       currentUserData.put("availableVacation", Integer.parseInt(availableVacation));
                   }

                   currentUserData.put("balance", dayPlanDataRepository.getdpdAndBalaceAsSum(data.getNthValue(0), calendar.get(Calendar.YEAR)));

                   // get total requests
                   Iterable<Requests> requests = requestsRepository.findByUserId(data.getNthValue(0));

                   currentUserData.put("requests", requests.spliterator().getExactSizeIfKnown());

                   currentUserData.put("azn_count", monthPlanRepository.findSubmittedByUser(data.getNthValue(0)).spliterator().getExactSizeIfKnown());

                   listUserData.add(currentUserData);
               }

                return new ResponseEntity<>(new ObjectMapper().writer().
                        withDefaultPrettyPrinter()
                        .writeValueAsString(listUserData), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("", HttpStatus.CONFLICT);
        }
    }

    /* YearPlan Controller */

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

    /* Calendar  Controller */

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
        int vacationDaysUsed = 0;

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

                // query tables by date
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

                    // check if vacation, glaz, urlaub, school is set
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
                        // check if date is a weekend day
                        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            continue;
                        }

                        // add 1 to vacationDaysUsed
                        vacationDaysUsed++;
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

                    // remove used days from userInfo
                    if (requests.get().getType() == RequestTypeEnum.rUrlaub) {
                        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(users.get().getUserId());

                        if (optionalUserInfo.isPresent()) {
                            Map<String, String> availableVacations = optionalUserInfo.get().getAvailableVacation();

                            int currYear = Calendar.getInstance().get(Calendar.YEAR);

                            availableVacations.put(String.valueOf(currYear),
                                    String.valueOf(
                                            (Integer.parseInt(availableVacations.getOrDefault(String.valueOf(currYear),
                                                    "0")) - vacationDaysUsed)));

                            optionalUserInfo.get().setAvailableVacation(availableVacations);

                            userInfoRepository.deleteById(optionalUserInfo.get().getUserinfoId());
                            userInfoRepository.save(optionalUserInfo.get());
                        }
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

    /* Default values  */

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
     * Delete default values
     */
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

    /* AZN Controller */

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

                    // set all dayplan data from this month as checked
                    Calendar c = Calendar.getInstance();

                    c.set(Calendar.YEAR, e.getYear());
                    c.set(Calendar.MONTH, e.getMonth());

                    var lastDayOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                    // find every day and mark as checked
                    for (int i = 1; i <= lastDayOfMonth; i++) {
                        // create date
                        String date = String.format("%s-%s-%d", e.getYear(), e.getMonth(), i);
                        try {
                            Optional<DayPlanData> dpd = dayPlanDataRepository.
                                    getBySetDateAndUserId(new Date(new SimpleDateFormat("yyyy-MM-dd")
                                            .parse(date).getTime()), user.get().getUserId());

                            if (dpd.isPresent()) {
                                DayPlanData dayPlanData = dpd.get();

                                dayPlanData.setChecked(true);

                                // save
                                dayPlanDataRepository.save(dayPlanData);
                            }
                        } catch (ParseException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    // if month is december new userInfo values should be set for the next year
                    if (e.getMonth() == 12) {
                        // Example december 2021 -> create values for 2022
                        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(user.get().getUserId());

                        // update userinfo for new year
                        if (userInfo.isPresent()) {
                            // generate new year
                            int newYear = e.getYear() + 1;

                            var userInfoData = userInfo.get();

                            userInfoRepository.delete(userInfoData);

                            // set vacation days
                            userInfoData.getAvailableVacation().put(String.valueOf(newYear),
                                    userInfoData.getAvailableVacation().
                                            getOrDefault(String.valueOf(e.getYear()), "0"));

                            // sick days
                            userInfoData.getSickDays().put(String.valueOf(newYear), "0");

                            // glaz days
                            userInfoData.getGlazDays().put(String.valueOf(newYear), "0");

                            // school days
                            userInfoData.getSchool().put(String.valueOf(newYear), "0");

                            // vacation sick
                            userInfoData.getVacationSick().put(String.valueOf(newYear), "0");

                            System.out.println(userInfoData.getUsers().getUserId());

                            userInfoRepository.save(userInfoData);
                        }

                        // update balance time for new year
                        Balance balanceNexYear = new Balance();

                        // init base values
                        balanceNexYear.setYear(e.getYear() + 1);
                        balanceNexYear.setUsers(user.get());
                        balanceNexYear.setUserId(user.get().getUserId());
                        balanceNexYear.setBalanceHours(0);
                        balanceNexYear.setBalanceHours(0);
                        balanceNexYear.setBalance(UserInfo.Balance.GUTHABEN);

                        // calculate time from this year put it to the new year
                        String time = dayPlanDataRepository.getdpdAndBalaceAsSum(user.get().getUserId(), e.getYear());

                        var values = time.split(":");

                        balanceNexYear.setBalanceHours(Integer.parseInt(values[0]));
                        balanceNexYear.setBalanceMinutes(Integer.parseInt(values[1]));

                        // set the right balance
                        if (balanceNexYear.getBalanceHours() < 0) {
                            balanceNexYear.setBalance(UserInfo.Balance.SCHULD);
                        }

                        balanceRepository.save(balanceNexYear);

                    }

                }, user.get(), Integer.parseInt((String)payload.get("year")) ,
                        (Integer) payload.get("month")).get();
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }

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

    /**
     * Get all AZN submitted by user by userid
     * @param userId userid from user
     * @return Json String
     * @throws JsonProcessingException
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @GetMapping("/azn/get")
    @ResponseBody
    String getAZNSubmittedByUserId(@RequestParam(name = "userId") String userId) throws JsonProcessingException {
        List<Map<String, Object>> resList = new ArrayList<>();
        Optional<Users> user = userRepository.findById(Long.parseLong(userId));

        if (user.isPresent()) {
            Iterable<MonthPlan> submittedMonthPlans = monthPlanRepository.findSubmittedByUser(user.get().getUserId());

            for (var month : submittedMonthPlans) {
                resList.add(new HashMap<>(Map.of("year", month.getYear(), "month", month.getMonth())));
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(resList);
    }

    /**
     * Save AZN message from admin
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping("/azn/messages")
    @ResponseBody
    Boolean saveMessage(@RequestBody Map<String, Object> payload) {
        try {
            Optional<Users> user = userRepository.findById(Long.parseLong((String)payload.get("userid")));

            if (user.isPresent()) {
                Messages messages = new Messages();

                MessageTypes messageTypes = MessageTypes.valueOf((String) payload.get("type"));
                messages.setMessage((String) payload.get("message"));
                messages.setUser(user.get());
                messages.setDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                messages.setMessageType(messageTypes);

                if (messageTypes == MessageTypes.AZN_MONTH) {
                    var year = Integer.parseInt((String) payload.get("year"));
                    var month = (Integer) payload.get("month");

                    messages.setMessageTypeData(Map.of("year", year, "month", month));
                }

                messagesRepository.save(messages);

                return true;
            }
            return false;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /* User Search Controller */

    /**
     * Get all users and user id's
     * @return list of username and user id
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @ResponseBody
    @GetMapping("/userlist")
    String getUsernamesAndIds() throws JsonProcessingException {
        Iterable<Users> users = userRepository.findAll();
        List<Map<String, Object>> returnData = new ArrayList<>();

        for (Users user : users) {
            if (user.getRoles().containsKey("ROLE_User")) {
                returnData.add(new HashMap<>(Map.of("username", user.getUsername(), "id", user.getUserId())));
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(returnData);
    }

    /* Overview Controller */

    /**
     * Delete overview data
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @DeleteMapping("/overview/delete")
    @ResponseBody
    Boolean deleteOverviewItem(@RequestBody Map<String, Object> payload) {
        try {
            String tag = (String)payload.get("tag");
            String id = (String)payload.get("id");

            Tags tagV = Tags.valueOf(tag);

            if (tagV == Tags.gFeiertag || tagV == Tags.gUrlaub) {
                return generalVacationRepository.deleteByUuid(UUID.fromString(id)) > 0;
            } else {
                // add days back to vacation if not glaz
                if (tagV == Tags.GLAZ) {
                    dayPlanDataRepository.deleteByUuid(UUID.fromString(id));

                   return dayPlanDataRepository.getCountByUUid(UUID.fromString(id)) == 0;
                }
                Iterable<IYearCount>  vacationByYear = dayPlanDataRepository.getDayPlanDataByUuidAndYear(UUID.fromString(id));

                if (vacationByYear.spliterator().getExactSizeIfKnown() > 0) {
                    Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(dayPlanDataRepository.
                            getUserIdByUUid(UUID.fromString(id)));

                    if (optionalUserInfo.isPresent()) {
                        var userInfo = optionalUserInfo.get();

                        for (IYearCount yearCount : vacationByYear) {
                            String current_vacation = userInfo.getAvailableVacation().
                                    getOrDefault(String.valueOf(yearCount.getYear()), "0");

                            userInfo.getAvailableVacation().put(String.valueOf(yearCount.getYear()),
                                    String.valueOf(Integer.parseInt(current_vacation) + yearCount.getCount()));
                        }
                        // delete vacation
                        dayPlanDataRepository.deleteByUuid(UUID.fromString(id));

                        // check if dpd was deleted
                        if (dayPlanDataRepository.getCountByUUid(UUID.fromString(id)) == 0) {
                            // first we need to delete
                            userInfoRepository.deleteById(userInfo.getUserinfoId());
                            // Then save
                            userInfoRepository.save(userInfo);

                            return true;
                        }
                    }
                }

                return false;
            }
        }catch (Exception exception) {
           exception.printStackTrace();
           return false;
        }
    }

    /**
     * Save admin Requested Date to calendar
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping("/overview/request")
    @ResponseBody
    Boolean saveAdminOverviewRequestedDate(@RequestBody Map<String, Object> data) {
        try {
            Optional<Users> user = userRepository.findById(Long.parseLong((String) data.get("id")));

            if (user.isPresent()) {
                Optional<Requests> requests = controllerHelper.getValidNonExistingRequest(data, user.get());

                if (requests.isPresent()) {
                    var request = requests.get();
                    Iterable<DayPlanData> dayPlanData = dayPlanDataRepository.
                            getDayPlanDataBySetDateBetweenAndUserId(
                                    request.getStartDate(),
                                    request.getEndDate(),
                                    user.get().getUserId());

                    Iterable<GeneralVacation> generals = generalVacationRepository.getGeneralVacationByDateBetween(
                            request.getStartDate(),
                            request.getEndDate()
                    );

                    // check if holiday or general holiday is set
                    if (StreamSupport.stream(generals.spliterator(), false).findAny().isEmpty()) {
                        // check if vacation, galz, urlaub, school is set
                        for (DayPlanData dpd : dayPlanData) {
                            if (dpd.getGlaz() || dpd.getSchool() || dpd.getVacation()) {
                                return false;
                            }
                        }

                        var start = TimeConvert.convertToLocalDateViaInstant(new java.util.Date(request.getStartDate().getTime()));
                        var end = TimeConvert.convertToLocalDateViaInstant(new java.util.Date(request.getEndDate().getTime()));

                        // generate uuid
                        UUID uuid = UUID.randomUUID();

                        // go over each day from start to end and set admin request
                        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                            System.out.println(date);
                            // get day
                            DayPlanData dpd;
                            Optional<DayPlanData> day =
                                    dayPlanDataRepository.getBySetDateAndUserId(new Date(TimeConvert.convertToDateViaInstant(date).
                                            getTime()), user.get().getUserId());

                            if (day.isEmpty()) {
                                dpd = new DayPlanData();

                                dpd.setUserId(user.get().getUserId());
                                dpd.setSetDate(new Date(TimeConvert.convertToDateViaInstant(date).getTime()));
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
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /* General Overview Controller */

    /**
     * Get all Data from general_vacation
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Read')")
    @GetMapping("/generalOverview")
    @ResponseBody
    String getGeneralOverviewData(@RequestParam(required = false, name = "firstday") String firstDay,
                                  @RequestParam(required = false, name = "lastday") String lastDay,
                                  @RequestParam(required = false, name = "month") String month,
                                  @RequestParam(required = false, name = "year") String year) throws ParseException, JsonProcessingException {

        var sdf = new SimpleDateFormat("dd-MM-yyyy");

        // remove duplicates
        Set<OverviewController.DateRange> dateRanges = new HashSet<>();

        Pair<String, String> dates = controllerHelper.parseStartEndDate(firstDay, lastDay, year, month);

        String startDate = dates.getKey();
        String endDate = dates.getValue();

        var data = generalVacationRepository.getGeneralVacationByDateBetween(new Date(sdf.parse(startDate).getTime()),
                new Date(sdf.parse(endDate).getTime()));

        // sort by uuid
        Map<UUID, ArrayList<IUuidable>> generalVacationByUUID = controllerHelper.mapByUUID(data);

        // show data in the right representation
        // get min and max date from general vacation
        for (var gv: generalVacationByUUID.entrySet()) {
            // check if general vacation goes over more than one day
            if (gv.getValue().size() > 1) {
                dateRanges.add(new OverviewController.DateRangeComment(
                        //get the right start and end date by using min and max
                        gv.getValue().stream().map(uuiDable ->
                                ((GeneralVacation)uuiDable).getDate()).min(Date::compareTo).get(),
                        gv.getValue().stream().map(uuiDable ->
                                ((GeneralVacation)uuiDable).getDate()).max(Date::compareTo).get(),
                        // set the right tag
                        ((GeneralVacation)gv.getValue().get(0)).getTag() == Tags.gFeiertag ? Tags.gFeiertag: Tags.gUrlaub,
                        gv.getKey(),
                        ((GeneralVacation)gv.getValue().get(0)).getComment()
                ));
            } else {
                GeneralVacation generalVacation = (GeneralVacation)gv.getValue().get(0);
                dateRanges.add(
                        new OverviewController.DateRangeComment(generalVacation.getDate(),
                                generalVacation.getDate(),
                                // set the right tag
                                generalVacation.getTag() == Tags.gFeiertag ? Tags.gFeiertag: Tags.gUrlaub,
                                gv.getKey(),
                                generalVacation.getComment()
                        ));
            }
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(dateRanges);
    }

    /**
     * Post data to general vacation table
     * @param startDate
     * @param endDate
     * @param tag
     * @throws ParseException
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @PostMapping("/generalOverview/request")
    @ResponseBody
    Boolean saveGeneralOverviewRequest(@RequestParam(name = "startDate") String startDate,
                                       @RequestParam(name = "endDate") String endDate,
                                       @RequestParam(name = "tag") String tag,
                                       @RequestParam(name = "comment", required = false) String comment) throws ParseException {

        // get all day plans between start end date
        var formatter = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date startDate_  = new java.util.Date(formatter.parse(startDate).getTime());
        java.util.Date endDate_ = new java.util.Date(formatter.parse(endDate).getTime());

        Iterable<GeneralVacation> generals = generalVacationRepository.getGeneralVacationByDateBetween(
                new Date(formatter.parse(startDate).getTime()),
                new Date(formatter.parse(endDate).getTime())
        );

        // check if requested range is empty
        if (StreamSupport.stream(generals.spliterator(), false).findAny().isEmpty()) {

            var start = TimeConvert.convertToLocalDateViaInstant(startDate_);
            var end = TimeConvert.convertToLocalDateViaInstant(endDate_);

            // generate uuid
            UUID uuid = UUID.randomUUID();

            // go over each day from start to end and set request value
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                // get day
                GeneralVacation generalVacation = new GeneralVacation();
                generalVacation.setDate(new Date(TimeConvert.convertToDateViaInstant(date).getTime()));
                generalVacation.setTag(Tags.valueOf(tag));
                generalVacation.setUuid(uuid);

                String commentG = switch (Tags.valueOf(tag)) {
                    case gUrlaub -> "Urlaub";
                    case gFeiertag -> "Feiertag";
                    default -> "?";
                };

                if (comment != null && !comment.isEmpty()) {
                    commentG = comment;
                }

                generalVacation.setComment(commentG);

                Calendar c = new GregorianCalendar();
                c.setTime(new Date(TimeConvert.convertToDateViaInstant(date).getTime()));

                System.out.println(TimeConvert.convertToDateViaInstant(date));

                generalVacation.setYear(c.get(Calendar.YEAR));

                generalVacationRepository.save(generalVacation);

                // check if value was saved
                GeneralVacationKey generalVacationKey = new GeneralVacationKey();
                generalVacationKey.setDate(generalVacation.getDate());
                generalVacationKey.setYear(generalVacation.getYear());

                if (generalVacationRepository.findById(generalVacationKey).isEmpty()) {
                    return false;
                }



            }

            // get all users
            Iterable<BigInteger> userIds = userRepository.getAllUserIds();

            // remove vacation from all users
            StreamSupport.stream(userIds.spliterator(), true).forEach(userid -> {
                Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(userid.longValue());

                if (optionalUserInfo.isPresent()) {
                    // get count by year
                    Iterable<IYearCount> generalVacationByYearCount = generalVacationRepository.
                            getGeneralVacationByUuidAndYear(uuid);

                    UserInfo userInfo = optionalUserInfo.get();

                    for (IYearCount yearCount: generalVacationByYearCount) {
                        String current_vacation = userInfo.getAvailableVacation().
                                getOrDefault(String.valueOf(yearCount.getYear()), "0");

                        userInfo.getAvailableVacation().put(String.valueOf(yearCount.getYear()),
                                String.valueOf(Integer.parseInt(current_vacation) - yearCount.getCount()));
                    }

                    // remove userinfo to set it back
                    userInfoRepository.delete(userInfo);

                    // save
                    userInfoRepository.save(userInfo);
                }
            });

            return true;
        }

        return false;
    }

    /**
     * delete from general overview calendar
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @DeleteMapping("/generalOverview/delete")
    @ResponseBody
    Boolean deleteGeneralOverViewItem(@RequestBody Map<String, Object> payload) {
        try {
            UUID uuid = UUID.fromString((String) payload.get("id"));

            // get vacation count
            Iterable<IYearCount> generalVacationCount = generalVacationRepository.getGeneralVacationByUuidAndYear(uuid);
            Iterable<BigInteger> userIds = userRepository.getAllUserIds();

            System.out.println("ids:"  + userIds);

            generalVacationRepository.deleteByUuid(uuid);

            if (generalVacationRepository.findByUuid(uuid).spliterator().getExactSizeIfKnown() == 0) {
                // go over all users
                StreamSupport.stream(userIds.spliterator(), true).forEach(userid -> {
                    System.out.println("Userid:" + userid);
                    Optional<UserInfo> optionalUserInfoRepository = userInfoRepository.findByUserId(userid.longValue());

                    if (optionalUserInfoRepository.isPresent()) {
                        UserInfo userInfo = optionalUserInfoRepository.get();

                        for (IYearCount yearCount: generalVacationCount) {
                            String current_vacation = userInfo.getAvailableVacation().
                                    getOrDefault(String.valueOf(yearCount.getYear()), "0");

                            userInfo.getAvailableVacation().put(String.valueOf(yearCount.getYear()),
                                    String.valueOf(Integer.parseInt(current_vacation) + yearCount.getCount()));
                        }

                        // remove userinfo to set it back
                        userInfoRepository.delete(userInfo);

                        // save
                        userInfoRepository.save(userInfo);
                    }
                });

                return true;
            }
        } catch (Exception ignored) {}

        return false;
    }

    /**
     * Worktime list for admin panel
     * @param userId userid
     * @return Worktime list
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @GetMapping("/worktimeList")
    @ResponseBody
    String getWorkTimeListByUser(@RequestParam(name = "userId") String userId) throws JsonProcessingException {
        Optional<Users> optionalUsers = userRepository.findById(Long.parseLong(userId));

        if (optionalUsers.isPresent()) {
            Iterable<WorkTime> workTimeRepositories =
                    workTimeRepository.getWorkTimeByUser(optionalUsers.get().getUserId());

            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(workTimeRepositories);
        }
        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(null);
    }

    /**
     * Get a list of all years and vacation by user
     * @param userId userid
     *
     * @return json string
     */
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    @GetMapping("/yearsList")
    @ResponseBody
    String getVacationByUser(@RequestParam(name = "userId") String userId) throws JsonProcessingException {
        Optional<Users> optionalUsers = userRepository.findById(Long.parseLong(userId));

        if (optionalUsers.isPresent()) {
            return new ObjectMapper().writer().
                    withDefaultPrettyPrinter()
                    .writeValueAsString(userInfoRepository.findByUserId(optionalUsers.get().getUserId()).get().getAvailableVacation());
        }

        return new ObjectMapper().writer().
                withDefaultPrettyPrinter()
                .writeValueAsString(null);
    }

    /**
     * Post admin edit date
     * @param payload user data
     * @param userId id of user
     *
     * @return true or false
     */
    @GetMapping("/edit")
    @CrossOrigin("/admin")
    @ResponseBody
    Boolean postAdminEditData(@RequestBody Map<String, Object> payload, @RequestParam(name = "userId") String userId) {
        // Todo: ...
        return true;
    }
}