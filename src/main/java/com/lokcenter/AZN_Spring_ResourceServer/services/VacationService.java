package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Get vacation data
 */
@Service
@AllArgsConstructor
public class VacationService {
    @Autowired
    private final UserInfoRepository userInfoRepository;

    @Autowired
    private final DayPlanDataRepository dayPlanDataRepository;

    private CompletableFuture<Long> getSetVacation(String year, Long userId) {
        return CompletableFuture.completedFuture(Long.parseLong(userInfoRepository.getSetVacationByUser(year, userId)));
    }

    private CompletableFuture<Long> getAvVacation(int year, Long userId) {
        return CompletableFuture.completedFuture(dayPlanDataRepository.usedVacationByYearAndUser(userId, year));
    }

    @Async
    public CompletableFuture<Long> getAvailabeVacation(int year, Long userId) throws ExecutionException, InterruptedException {
        var setVacation = getSetVacation(String.valueOf(year), userId);
        var avVacation = getAvVacation(year, userId);

        return CompletableFuture.completedFuture(setVacation.get() - avVacation.get());
    }
}
