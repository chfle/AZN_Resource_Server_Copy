package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Time;
import java.util.Optional;
import java.util.UUID;

/**
 * DayPlanData queries
 */
public interface DayPlanDataRepository extends CrudRepository<DayPlanData, DayPlanDataKey> {
    /**
     * get all day plans where one or more fields are true
     */
    @Query(value = "select * from day_plan_data where user_id=?1 and (glaz or sick or vacation) and (set_date between ?2 and ?3)", nativeQuery = true)
    Iterable<DayPlanData> getAllByUserWhereTrue(Users user, Date startDate, Date endDate);

    /**
     * get dayplan where one or more fields are true
     */
    @Query(value = "select * from day_plan_data where user_id=?1 and (glaz or sick or vacation or school) and set_date = ?2", nativeQuery = true)
    Optional<DayPlanData> getDayPlanDataWhereTrue(Users user, Date startDate);

    /**
     * get all checked day plans where checked by user without any selected boolean
     */
    @Query(value = "select * from day_plan_data where user_id=?1 and " +
            "checked and ((not glaz or glaz is null) and " +
            "(not school or school is null) and (not vacation or vacation is null) and " +
            "(not sick or sick is null)) ", nativeQuery = true)
    Iterable<DayPlanData> getAllByUserIdAndAndChecked(Users user);

    /**
     * Get Dayplans between start and end date by user
     */
    @Query(value = "select * from day_plan_data where user_id=?3 and (set_date between ?1 and ?2) " +
            "and worktime_start is not null and worktime_end is not null", nativeQuery = true)
    Iterable<DayPlanData> getDayPlanDataBySetDateBetweenAndUserId(Date startDate, Date endDate, Long userId);

    /**
     * Get Dayplan by user and set date
     */
    @Query(value = "select * from day_plan_data where set_date = ?1 and user_id = ?2", nativeQuery = true)
    Optional<DayPlanData> getBySetDateAndUserId(Date setDate, Long userId);

    @Transactional
    Long deleteByUuid(UUID uuid);

    @Query(value = "select count(*) from day_plan_data where user_id = ?1 and vacation = true", nativeQuery = true)
    long countByUserIdAndVacationTrue(Long userId);

    @Query(value = "select sum(dpd_soll - soll)\\:\\:varchar as final_soll from " +
            "(select  (worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause) as dpd_soll from day_plan_data where user_id=?1 and not (glaz or sick or vacation or school) and extract(year from set_date) = ?2) as dpd cross join " +
            "(select (worktime_end - worktime_start - worktime_pause) as soll from work_time where user_id = ?1 ORDER BY work_time.date DESC  limit 1) work_time;", nativeQuery = true)
    String getSumOfSollTime(Long userid, int year);

    @Query(value = "select sum (final_soll + timeV)\\:\\:varchar from (select sum(dpd_soll - soll) as final_soll from\n" +
            "            ((select  (worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause) as dpd_soll from day_plan_data where user_id=?1 and not (glaz or sick or vacation or school) and extract(year from set_date) = ?2) as dpd cross join\n" +
            "            (select (worktime_end - worktime_start - worktime_pause) as soll from work_time where user_id = ?1 ORDER BY work_time.date DESC  limit 1) work_time)) as dwtfs cross join" +
            " (select to_timestamp(balance_hours\\:\\:varchar || ':'  || balance_minutes\\:\\:varchar, 'hh24:MI')\\:\\:Time as timeV from balance where user_id = ?1 and year = ?2) as b", nativeQuery = true)
    String getdpdAndBalaceAsSum(Long userId, int year);
}
