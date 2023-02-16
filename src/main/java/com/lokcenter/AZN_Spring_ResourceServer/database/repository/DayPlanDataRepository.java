package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IYearCount;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.sql.Date;
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

    /**
     * get work days grouped by year
     * @param userId userId
     *
     * @return IYearCount object
     */
    @Query(value = "select extract(year from set_date) as year, count(*) from day_plan_data where user_id=?1 and" +
            "            checked and ((not glaz or glaz is null) and" +
            "            (not school or school is null) and (not vacation or vacation is null) and" +
            "            (not sick or sick is null)) group by extract(year from set_date);", nativeQuery = true)
    Iterable<IYearCount> getWorkDayCountGrouped(Long userId);

    /**
     * Checked Sick days grouped
     * @param userId userId
     *
     * @return IYearCount object
     */
    @Query(value = "select extract(year from set_date) as year, count(*) from day_plan_data where user_id = ?1 and sick" +
            " and checked group by extract(year from set_date);", nativeQuery = true)
    Iterable<IYearCount> getSickDayCountGrouped(Long userId);

    @Query(value = "select count(*) from day_plan_data where user_id = ?1 and sick and extract(year from set_date) = ?2", nativeQuery = true)
    long sickCountByUserAndYear(Long userid, int year);

    @Query(value = "select count(*) from day_plan_data where user_id = ?1 and glaz and extract(year from set_date) = ?2", nativeQuery = true)
    long glazCountByUserAndYear(Long userid, int year);

    @Query(value = "select extract(year from set_date) as year, count(*) from day_plan_data where user_id = ?1 and glaz" +
            "             and checked group by extract(year from set_date);", nativeQuery = true)
    Iterable<IYearCount> getGlazDayCountGrouped(Long userId);

    @Transactional
    Long deleteByUuid(UUID uuid);

    @Query(value = "select count(*) from day_plan_data where user_id = ?1 and vacation = true", nativeQuery = true)
    long countByUserIdAndVacationTrue(Long userId);

    @Query(value = "select (final_soll + timeV)\\:\\:varchar from (select (weekend_soll + weekday_soll) as final_soll from (select sum(weekdays_soll - soll) as weekday_soll from\n" +
            "(select  (worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause) as\n" +
            "weekdays_soll from day_plan_data where user_id=?1 and not (glaz or sick or vacation or school) and EXTRACT(ISODOW FROM set_date) not IN (6, 7)\n" +
            "and extract(year from set_date) = ?2) as dpd cross join (select (worktime_end - worktime_start - worktime_pause)\n" +
            "as soll from work_time where user_id = ?1 ORDER BY work_time.date DESC  limit 1) work_time) as weekdays cross join\n" +
            "(select  sum(worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause) as\n" +
            "weekend_soll from day_plan_data where user_id=?1 and not (glaz or sick or vacation or school) and EXTRACT(ISODOW FROM set_date)\n" +
            "IN (6, 7)) as weekend) as soll cross join (select make_interval(0, 0, 0, 0, balance_hours\\:\\:integer, balance_minutes\\:\\:integer, 0)\n" +
            "as timeV from balance where user_id = ?1 and year = ?2) as b", nativeQuery = true)
    String getdpdAndBalaceAsSum(Long userId, int year);
}
