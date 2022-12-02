package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.MonthPlanKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.MonthPlan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthPlanRepository extends CrudRepository<MonthPlan, MonthPlanKey> {
}
