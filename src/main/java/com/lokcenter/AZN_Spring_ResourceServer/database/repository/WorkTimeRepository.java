package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.WorkTime;
import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Date;
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

    @Query(value = "select (worktime_end - worktime_start - worktime_pause)\\:\\:varchar as soll from work_time where date <= ?2 and user_id = ?1 ORDER BY date DESC  limit 1", nativeQuery = true)
    Optional<String> getMostRecentSollByUserAndDate(Users users, Date date);

    @Query(value = "select * from work_time where user_id = ?1 order by date desc", nativeQuery = true)
    Iterable<WorkTime> getWorkTimeByUser(Long userId);

    @Query(value = "select * from work_time where user_id = ?1 and date = ?2", nativeQuery = true)
    Optional<WorkTime> getWorkTimeByUserIdAndDate(long Userid, Date date);
}
