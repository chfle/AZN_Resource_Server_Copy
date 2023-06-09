package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IYearCount;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.*;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.*;
import com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes.DayTime;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.Pair;
import com.lokcenter.AZN_Spring_ResourceServer.helper.testing.JunitHelper;
import com.lokcenter.AZN_Spring_ResourceServer.helper.NullType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;

/**
 * LoginController
 *
 * Save first login of a user
 *
 * @version 17-07-22
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserInfoRepository userInfoRepository;

    @Autowired
    private final DefaultsRepository defaultsRepository;

    @Autowired
    private final WorkTimeRepository workTimeRepository;

    @Autowired
    private final BalanceRepository balanceRepository;

    @Autowired
    private final GeneralVacationRepository generalVacationRepository;


    /**
     * First login of user. Save basic data for new user
     * @param payload basic information
     *
     * @return true or false
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    ResponseEntity<Boolean> postLogin(@RequestBody Map<String, Object> payload) throws ParseException {
       Users user = new Users();

        try {
            if (payload.containsKey("username") && payload.containsKey("roles")) {
                user.setUsername((String) payload.get("username"));

                // convert back to a utils.date to use sql.date
                Date currentDate = new Date();

                user.setFirstLogin(new java.sql.Date(currentDate.getTime()));

                // end date generated with 3 years
                Calendar c = Calendar.getInstance();

                c.setTime(currentDate);
                c.add(Calendar.YEAR, 3);
                c.set(Calendar.MONTH, Calendar.AUGUST);
                c.set(Calendar.DAY_OF_MONTH , 31);

                user.setEndDate(new java.sql.Date(c.getTime().getTime()));

                // Add roles
                var roles = (ArrayList<LinkedHashMap<String, String>>) payload.get("roles");

                Map<String, NullType> rolesMap = new HashMap<>();

                for (var role: roles) {
                    rolesMap.put(role.get("authority"), new NullType());
                }

                user.setRoles(rolesMap);

                // try to insert user but check if not a junit test
                if (!JunitHelper.isJUnitTest()) {
                   try {
                       // check if user exists
                       if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
                           userRepository.save(user);
                       }
                   }catch (Exception exception) {
                       //exception.printStackTrace();
                   }

                    // get saved user
                    Optional<Users> userf = userRepository.findByUsername(user.getUsername());


                    if (userf.isPresent()) {
                        // set user data
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUsers(userf.get());

                        // get current date
                        java.sql.Date currDate = new java.sql.Date(new Date().getTime());

                        // set default values
                        DayTime workTime = new DayTime();

                        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                        // default work time

                        workTime.setStart(new Time(format.parse("00:00 am").getTime()));
                        workTime.setEnd(new Time(format.parse("00:00 pm").getTime()));
                        workTime.setPause(new Time(format.parse("00:00 am").getTime()));

                        // default vacation
                        userInfo.setSetVacation(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()), "0")));

                        // balance time
                        Balance balance = new Balance();

                        // create
                        balance.setBalanceHours(0);
                        balance.setBalanceMinutes(0);
                        balance.setBalance(UserInfo.Balance.GUTHABEN);
                        balance.setYear(Calendar.getInstance().get(Calendar.YEAR));
                        balance.setUsers(userf.get());
                        balance.setUserId(userf.get().getUserId());

                        // only create if there is no balance time
                        if (balanceRepository.findByUsers(userf.get().getUserId()).spliterator().getExactSizeIfKnown() == 0) {
                            balanceRepository.save(balance);
                        }

                        // set defaults without default values set from admin
                        if (defaultsRepository.count() != 0) {
                            // use values from default
                            Optional<Defaults> defaults =
                                    defaultsRepository.findClosedDefaultValue(new java.sql.Date(new Date().getTime()));

                            if (defaults.isPresent()) {
                                workTime.setStart(defaults.get().getDefaultStartTime());
                                workTime.setEnd(defaults.get().getDefaultEndTime());
                                workTime.setPause(defaults.get().getDefaultPause());

                                userInfo.setSetVacation(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()),
                                        String.valueOf(defaults.get().getDefaultVacationDays()))));
                            }
                        }

                          WorkTime workTimeObj = new WorkTime();
                          workTimeObj.setWorkTime(workTime);
                          workTimeObj.setUsers(userInfo.getUsers());
                          workTimeObj.setDate(currDate);

                        // save default values
                        try {
                            if (userInfoRepository.findByUserId(user.getUserId()).isEmpty()) {
                                userInfoRepository.save(userInfo);
                            }
                        }catch (Exception ignore){}

                        // save workTime
                        try {
                            if (workTimeRepository.findWorkTimeByUsers(userf.get()).spliterator().getExactSizeIfKnown() == 0) {
                                workTimeRepository.save(workTimeObj);
                            }
                        }catch (Exception exception){
                            exception.printStackTrace();
                        }
                    }
                }
            } else {
               throw new Exception("Bad request");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
       return new ResponseEntity<>(true, HttpStatus.OK);
    }
}