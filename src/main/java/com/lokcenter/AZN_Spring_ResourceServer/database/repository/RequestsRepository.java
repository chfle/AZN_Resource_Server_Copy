package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.RequestsKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
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
}
