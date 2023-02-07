package com.lokcenter.AZN_Spring_ResourceServer.helper.components;

import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IYearCount;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.BalanceRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
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
        // Note: Only checked Month plan data will be set here.

        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(user.getUserId());
        Map<String, Map<String, Object>> yearDataMap = new HashMap<>();

        if (optionalUserInfo.isPresent()) {
            var userinfo = optionalUserInfo.get();

            yearDataMap = userinfo.yearToMap();

            for (var year: yearDataMap.entrySet()) {
                yearDataMap.get(year.getKey()).put("balance", dayPlanDataRepository.getdpdAndBalaceAsSum(user.getUserId(),
                        Integer.parseInt(year.getKey())));
            }

            // work days grouped by year
            Iterable<IYearCount> workYearCountIterable = dayPlanDataRepository.getWorkDayCountGrouped(user.getUserId());

            for (var workYearCount: workYearCountIterable) {
                if (yearDataMap.containsKey(String.valueOf(workYearCount.getYear()))) {
                    yearDataMap.get(String.valueOf(workYearCount.getYear())).put("workDay", workYearCount.getCount());
                } else {
                    yearDataMap.put(String.valueOf(workYearCount.getYear()),
                            new HashMap<>(Map.of("workDay", workYearCount.getCount())));
                }
            }

            // sick days
            Iterable<IYearCount> sickYearCountIterable = dayPlanDataRepository.getSickDayCountGrouped(user.getUserId());

            for (var sickYearCount: sickYearCountIterable) {
                if (yearDataMap.containsKey(String.valueOf(sickYearCount.getYear()))) {
                    yearDataMap.get(String.valueOf(sickYearCount.getYear())).put("sickDay", sickYearCount.getCount());
                } else {
                    yearDataMap.put(String.valueOf(sickYearCount.getYear()),
                            new HashMap<>(Map.of("sickDay", sickYearCount.getCount())));
                }
            }

        }
        return yearDataMap;
    }
}
