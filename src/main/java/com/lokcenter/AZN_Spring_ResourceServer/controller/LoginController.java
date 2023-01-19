package com.lokcenter.AZN_Spring_ResourceServer.controller;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DefaultsRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.WorkTimeRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Defaults;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.WorkTime;
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


    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_UserApi.Write')")
    ResponseEntity<Boolean> postLogin(@RequestBody Map<String, Object> payload) {
       Users user = new Users();

        try {
            if (payload.containsKey("username") && payload.containsKey("roles")) {
                user.setUsername((String) payload.get("username"));

                // convert back to an utils.date to use sql.date
                Date currentDate = new Date();

                user.setFirstLogin(new java.sql.Date(currentDate.getTime()));

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
                       userRepository.save(user);
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

                        workTime.setStart(new Time(format.parse("07:15 am").getTime()));
                        workTime.setEnd(new Time(format.parse("16:00 pm").getTime()));
                        workTime.setPause(new Time(format.parse("01:00 am").getTime()));

                        // default vacation
                        userInfo.setAvailableVacation(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()), "30")));
                        userInfo.setSickDays(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()), "0")));
                        userInfo.setGlazDays(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()), "0")));
                        userInfo.setSchool(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()), "0")));
                        userInfo.setVacationSick(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()), "0")));
                        userInfo.setBalanceTime(new Time(format.parse("00:00 am").getTime()));
                        userInfo.setBalance(UserInfo.Balance.GUTHABEN);

                        // set defaults without default values set from admin
                        if (defaultsRepository.count() != 0) {
                            // use values from default
                            Optional<Defaults> defaults =
                                    defaultsRepository.findClosedDefaultValue(new java.sql.Date(new Date().getTime()));

                            if (defaults.isPresent()) {
                                workTime.setStart(defaults.get().getDefaultStartTime());
                                workTime.setEnd(defaults.get().getDefaultEndTime());
                                workTime.setPause(defaults.get().getDefaultPause());

                                userInfo.setAvailableVacation(new HashMap<>(Map.of(String.valueOf(Year.now().getValue()),
                                        String.valueOf(defaults.get().getDefaultVacationDays()))));
                            }
                        }

                          WorkTime workTimeObj = new WorkTime();
                          workTimeObj.setWorkTime(workTime);
                          workTimeObj.setDate(currDate);
                          workTimeObj.setUsers(userInfo.getUsers());

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