package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.MonthPlanKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.MonthPlan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthPlanRepository extends CrudRepository<MonthPlan, MonthPlanKey> {
    @Query(value = "select * from month_plan where user_id = ?1 and accepted = false and submitted = true", nativeQuery = true)
    Iterable<MonthPlan> findSubmittedByUser(Long userId);

    @Query(value = "select * from month_plan where month = ?1 and year = ?2 and user_id = ?3 and (accepted or submitted);", nativeQuery = true)
    Optional<MonthPlan> findMonthPlanByMonthAndYear(int month, int year, long user_id);
}
