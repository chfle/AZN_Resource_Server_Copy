package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import org.springframework.data.repository.CrudRepository;

public interface DayPlanDataRepository extends CrudRepository<DayPlanData, DayPlanDataKey> {
}
