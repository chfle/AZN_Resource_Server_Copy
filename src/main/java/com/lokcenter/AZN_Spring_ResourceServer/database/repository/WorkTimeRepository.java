package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.WorkTime;
import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Optional;

@Repository
public interface WorkTimeRepository extends JpaRepository<WorkTime, Long> {
    @Query
    Iterable<WorkTime> findWorkTimeByUsers(Users users);

    @Query(value = "select * from work_time where user_id = ?1 ORDER BY date DESC  limit 1", nativeQuery = true)
    Optional<WorkTime> getMostRecentWorkTimeByUser(Users users);

    @Query(value = "select (worktime_end - worktime_start - worktime_pause)\\:\\:varchar as soll from work_time where user_id = ?1 ORDER BY date DESC  limit 1", nativeQuery = true)
    Optional<String> getMostRecentSollByUser(Users users);
}
