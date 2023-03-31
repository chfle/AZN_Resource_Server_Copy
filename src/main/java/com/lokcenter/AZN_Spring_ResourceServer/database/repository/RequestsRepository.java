package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.RequestsKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.MonthPlan;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.util.Optional;

public interface RequestsRepository extends CrudRepository<Requests, RequestsKey> {
    @Query(value = "select * from requests where user_id = ?3 and (start_date Between ?1 and ?2 or end_date between ?1 and ?2)", nativeQuery = true)
    Iterable<Requests> getRequestsByRange(Date startDate, Date endDate, Users user);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "insert into requests(end_date, start_date, uuid, user_id, type) " +
            "values(:#{#requests.endDate}, :#{#requests.startDate}, :#{#requests.uuid }, :#{#requests.users.userId}," +
            ":#{#requests.type.name()})", nativeQuery = true)
    void insertSave(@Param("requests") Requests requests);

    @Query(value = "select * from requests where user_id = ?1", nativeQuery = true)
    Iterable<Requests> findByUserId(Long userId);

    @Query(value = "select * from requests where start_date = ?1 and end_date = ?2 and user_id = ?3", nativeQuery = true)
    Optional<Requests> findRequestsByStartDateAndEndDateAndUsers(Date startDate, Date endDate, Long userid);

    @Transactional
    @Modifying
    @Query(value = "delete from requests where start_date = ?1 and end_date = ?2 and user_id = ?3", nativeQuery = true)
    void deleteRequestsByStartDateAndEndDateAndUsers(Date startDate, Date endDate, Long userid);

    @Query(value = "select * from requests where (extract(year from  start_date) = ?1 or extract(year from  end_date) = ?1) and type = 'rUrlaub' and user_id = ?2 ", nativeQuery = true)
    Iterable<Requests> getVacationRequestsByYear(int year, long userId);
}
