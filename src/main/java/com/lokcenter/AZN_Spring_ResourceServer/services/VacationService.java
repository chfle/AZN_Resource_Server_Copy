package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Everything to get the vacation stuff
 */
@Service
@AllArgsConstructor
public class VacationService {
    @Autowired
    private final DayPlanDataRepository dayPlanDataRepository;

}
