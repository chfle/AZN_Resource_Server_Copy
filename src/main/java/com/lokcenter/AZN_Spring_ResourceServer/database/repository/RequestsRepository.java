package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.RequestsKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.Optional;

public interface RequestsRepository extends CrudRepository<Requests, RequestsKey> {
    @Query(value = "select * from requests where user_id = ?3 and (start_date Between ?1 and ?2 or end_date between ?1 and ?2)", nativeQuery = true)
    Iterable<Requests> getRequestsByRange(Date startDate, Date endDate, Users user);

    @Query(value = "insert into requests(end_date, start_date, type, uuid, user_id) " +
            "values(:#{#requests.endDate}, :#{#requests.startDate}, :#{#requests.type}, :#{#requests.uuid}, :#{#requests.users.userId})", nativeQuery = true)
    void insertSave(@Param("requests") Requests requests);
}
