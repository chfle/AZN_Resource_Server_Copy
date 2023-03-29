package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.BalanceRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

    @Async
    public CompletableFuture<String> getBalanceTime(Optional<Users> user, int year) throws ParseException {
        if (user.isPresent()) {
            String time = dayPlanDataRepository.getdpdAndBalaceAsSum(user.get().getUserId(), year);

            if (time == null) {
                time = "00:00";
            }

            return CompletableFuture.completedFuture(time);
        }
        return CompletableFuture.completedFuture("");
    }
}
