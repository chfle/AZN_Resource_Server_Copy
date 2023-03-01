package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IYearCount;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Transactional
    @Query(value = "delete from day_plan_data where uuid = ?1", nativeQuery = true)
    void deleteByUuid(UUID uuid);

    @Query(value = "select count(*) from day_plan_data where uuid = ?1", nativeQuery = true)
    Long getCountByUUid(UUID uuid);

    @Query(value = "select distinct user_id from day_plan_data where uuid = ?1", nativeQuery = true)
    Long getUserIdByUUid(UUID uuid);

    @Query(value = "select count(extract(year from set_date)), extract(year from set_date) as year from day_plan_data where uuid = ?1 group by extract(year from set_date)", nativeQuery = true)
    Iterable<IYearCount> getDayPlanDataByUuidAndYear(UUID uuid);

    /**
     * Get vacation by user + general vacation
     * @implNote if something is in general vacation do not count dayplan data
     * @param userId userid
     *
     * @return count
     */
    @Query(value = "select (count(*) + (select count(*) from general_vacation where tag = 'gUrlaub')) as vacation from day_plan_data where user_id = ?1 and vacation = true and set_date not in (select date from general_vacation where tag = 'gUrlaub')", nativeQuery = true)
    long countByUserIdAndVacationTrue(Long userId);

    @Query(value = "select (count(*) + (select count(*) from general_vacation where tag = 'gUrlaub')) as vacation from day_plan_data where user_id = ?1 and vacation = true and extract(year from set_date) = ?2 and set_date not in (select date from general_vacation where tag = 'gUrlaub')", nativeQuery = true)
    long countByUserIdAndVacationTrueByYear(Long userId, int year);

    @Query(value = "select sum(final_soll + timeV)\\:\\:varchar from (select (weekend_soll + weekdays) as final_soll from (select case when sum(day_plan_data.worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause -\n" +
            "       (select (w.worktime_end - w.worktime_start - w.worktime_pause) from work_time as w where w.date <= set_date and user_id = ?1 order by w.date desc limit 1 )) is null then INTERVAL '0 days' else sum(day_plan_data.worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause -\n" +
            "       (select (w.worktime_end - w.worktime_start - w.worktime_pause) from work_time as w where w.date <= set_date and user_id = ?1 order by w.date desc limit 1 )) end as weekdays from day_plan_data where user_id=?1 and not (glaz or sick or vacation or school) and EXTRACT(ISODOW FROM set_date) not IN (6, 7)\n" +
            "and extract(year from set_date) = ?2) as weekdays cross join\n" +
            "(select  case when (sum(worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause)) IS NULL then INTERVAL '0 days'\n" +
            "    else (sum(worktime_end - day_plan_data.worktime_start - day_plan_data.worktime_pause)) end\n" +
            "weekend_soll from day_plan_data where user_id=?1 and not (glaz or sick or vacation or school) and EXTRACT(ISODOW FROM set_date)\n" +
            "IN (6, 7)) as weekend) as soll cross join (select make_interval(0, 0, 0, 0, balance_hours\\:\\:integer, balance_minutes\\:\\:integer, 0)\n" +
            "as timeV from balance where user_id = ?1 and year = ?2) as b;", nativeQuery = true)
    String getdpdAndBalaceAsSum(Long userId, int year);

    @Query(value = "select exists(select * from day_plan_data where set_date = ?1 and user_id = ?2 and sick)", nativeQuery = true)
    Boolean isSickByUserAndDate(Date date, Long userId);
}
