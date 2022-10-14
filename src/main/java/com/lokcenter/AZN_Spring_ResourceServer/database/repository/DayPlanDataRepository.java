package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.Optional;

public interface DayPlanDataRepository extends CrudRepository<DayPlanData, DayPlanDataKey> {
    @Query(value = "select * from day_plan_data where user_id=?1 and (glaz or sick or vacation)", nativeQuery = true)
    Iterable<DayPlanData> getAllByUserWhereTrue(Users user);
}
