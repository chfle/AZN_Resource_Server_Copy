package com.lokcenter.AZN_Spring_ResourceServer.database.Repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.helper.DayPlanDataId;
import org.springframework.data.repository.CrudRepository;

/**
 * Fetch DayPlanData Objects from DB
 *
 * @version 1.5 2022-19-7
 */
public interface DayPlanDataRepository extends CrudRepository<DayPlanData, DayPlanDataId> {

}
