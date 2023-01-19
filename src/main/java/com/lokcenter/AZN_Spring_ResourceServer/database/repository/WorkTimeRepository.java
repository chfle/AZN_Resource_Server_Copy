package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.WorkTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkTimeRepository extends CrudRepository<WorkTime, Long> {
    @Query
    Iterable<WorkTime> findWorkTimeByUsers(Users users);

    @Query(value = "select * from work_time where user_id = ?1 ORDER BY date DESC  limit 1", nativeQuery = true)
    Optional<WorkTime> getMostRecentWorkTimeByUser(Users users);
}
