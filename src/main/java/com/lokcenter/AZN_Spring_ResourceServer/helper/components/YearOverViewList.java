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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

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

    private void groupIYearCount(Iterable<IYearCount> iYearCounts, String key, Map<String, Map<String, Object>> yearDataMap) {
        StreamSupport.stream(iYearCounts.spliterator(), true).forEach(sickYearCount -> {
            if (yearDataMap.containsKey(String.valueOf(sickYearCount.getYear()))) {
                yearDataMap.get(String.valueOf(sickYearCount.getYear())).put(key, sickYearCount.getCount());
            } else {
                yearDataMap.put(String.valueOf(sickYearCount.getYear()),
                        new HashMap<>(Map.of(key, sickYearCount.getCount())));
            }
        });
    }

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
        Map<String, Map<String, Object>> yearDataMap;

        if (optionalUserInfo.isPresent()) {
            var userinfo = optionalUserInfo.get();

            yearDataMap = userinfo.yearToMap();

            yearDataMap.entrySet().parallelStream().forEach((year) -> {
                yearDataMap.get(year.getKey()).put("balance",
                    dayPlanDataRepository.getdpdAndBalaceAsSum(user.getUserId(),
                    Integer.parseInt(year.getKey())));

                // get available vacation
                Long av = Long.parseLong((String)yearDataMap.get(
                        year.getKey()).getOrDefault("setVacation", "0"))
                        - dayPlanDataRepository.usedVacationByYearAndUser(user.getUserId(), Integer.parseInt(year.getKey()));

                System.out.println("av: " + av );


                yearDataMap.get(year.getKey()).put("availableVacation", av);
            });

            List<Thread> threads = new ArrayList<>();

            threads.add(new Thread(() -> {
                Iterable<IYearCount> sickYearCountIterable = dayPlanDataRepository.getSickDayCountGrouped(user.getUserId());
                groupIYearCount(sickYearCountIterable, "sickDay", yearDataMap);
            }));

            threads.add(new Thread(() -> {
                Iterable<IYearCount> glazYearCountIterable = dayPlanDataRepository.getGlazDayCountGrouped(user.getUserId());
                groupIYearCount(glazYearCountIterable, "glazDay", yearDataMap);
            }));

            threads.add(new Thread(() -> {
                Iterable<IYearCount> workYearCountIterable = dayPlanDataRepository.getWorkDayCountGrouped(user.getUserId());
                groupIYearCount(workYearCountIterable, "workDay", yearDataMap);
            }));

            threads.parallelStream().forEach(Thread::start);
            threads.parallelStream().forEach(thread -> {
                if (thread.isAlive()) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } else {
            yearDataMap = new HashMap<>();
        }
        return yearDataMap;
    }
}
