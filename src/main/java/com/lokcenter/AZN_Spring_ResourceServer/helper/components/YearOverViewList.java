package com.lokcenter.AZN_Spring_ResourceServer.helper.components;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.BalanceRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Balance;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class to get Year Overviews from users
 */
@Component
public class YearOverViewList {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    /**
     * Get All Years and Data as list by user
     *
     * @param user user to get data from
     *
     * @return Years of Year data
     */
    public Map<String, Map<String, Object>> getYearsListByUser(Users user) {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(user.getUserId());
        Map<String, Map<String, Object>> yearDataMap;

        var userinfo = optionalUserInfo.get();

        yearDataMap = userinfo.yearToMap();

        if (optionalUserInfo.isPresent()) {
            // get balance time data from user
            Iterable<Balance> balances = balanceRepository.findByUsers(user.getUserId());

            for (var year: yearDataMap.entrySet()) {
                yearDataMap.get(year.getKey()).put("balance", dayPlanDataRepository.getdpdAndBalaceAsSum(user.getUserId(),
                        Integer.parseInt(year.getKey())));
            }

            // get every day plan from user with checked
            // Only Valid and checked Data will be used
            Iterable<DayPlanData> dayPlanDataIterable = dayPlanDataRepository.getAllByUserIdAndAndChecked(user);

            for (var dpd : dayPlanDataIterable) {
                var calender = Calendar.getInstance();
                calender.setTime(dpd.getSetDate());

                var year = calender.get(Calendar.YEAR);

                if (yearDataMap.containsKey(String.valueOf(year))) {
                    yearDataMap.get(String.valueOf(year))
                            .put("workDay",
                                    ((Integer) yearDataMap.get(String.valueOf(year))
                                            .getOrDefault("workDay", 0)) + 1);
                } else {
                    yearDataMap.put(String.valueOf(year), new HashMap<>(Map.of("workDay", 1)));
                }
            }
        }
        return yearDataMap;
    }
}
