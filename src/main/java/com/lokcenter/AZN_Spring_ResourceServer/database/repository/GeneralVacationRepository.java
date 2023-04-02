package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IYearCount;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.GeneralVacationKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

public interface GeneralVacationRepository extends CrudRepository<GeneralVacation, GeneralVacationKey> {
    // select * from general_vacation where date between '09-26-2022' and '11-6-2022'
    @Query()
    Iterable<GeneralVacation> getGeneralVacationByDateBetween(Date start, Date end);

    @Query
    Optional<GeneralVacation> getGeneralVacationByDate(Date date);

    @Transactional
    long deleteByUuid(UUID uuid);

    @Query
    Iterable<GeneralVacation> findByUuid(UUID uuid);

    @Query(value = "select count(extract(year from date)), extract(year from date) as year from general_vacation where uuid = ?1 and tag = 'gUrlaub' and extract(ISODOW  from date) not in (6, 7) group by extract(year from date)", nativeQuery = true)
    Iterable<IYearCount> getGeneralVacationByUuidAndYear(UUID uuid);

    @Query(value = "select count(extract(year from date)), extract(year from date) as year from general_vacation where date >= ?1 and tag = 'gUrlaub' and extract(ISODOW  from date) not in (6, 7) group by extract(year from date)", nativeQuery = true)
    Iterable<IYearCount> getGeneralVacationFromDate(Date date);

    @Query(value = "select count(*) from general_vacation where date >= ?1 and extract(year from date) = ?2 ", nativeQuery = true)
    Long getVacationCountByYear(Date date, int year);

    @Query(value = "select exists(select * from general_vacation where date = ?1 and tag = 'gUrlaub')", nativeQuery = true)
    Boolean hasVacation(Date date);

    /**
     * Count vacation days by year (without holidays)
     * @param year
     * @return
     */
    @Query(value = "select count(*) from general_vacation where extract(year from date) = ?1 and tag = 'gUrlaub'", nativeQuery = true)
    Long getGeneralVacationCountByYear(long year);
}
