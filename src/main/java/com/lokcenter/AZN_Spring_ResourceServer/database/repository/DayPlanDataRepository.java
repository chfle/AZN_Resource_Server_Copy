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
    @Query(value = "select * from day_plan_data where user_id=?1 and (glaz or sick or vacation) and (set_date between ?2 and ?3)", nativeQuery = true)
    Iterable<DayPlanData> getAllByUserWhereTrue(Users user, Date startDate, Date endDate);

    @Query(value = "select * from day_plan_data where user_id=?1 and " +
            "checked and ((not glaz or glaz is null) and " +
            "(not school or school is null) and (not vacation or vacation is null) and " +
            "(not sick or sick is null)) ", nativeQuery = true)
    Iterable<DayPlanData> getAllByUserIdAndAndChecked(Users user);
}
