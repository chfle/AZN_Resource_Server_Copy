package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Defaults;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.Optional;

public interface DefaultsRepository extends CrudRepository<Defaults, Date> {
 @Query(value = "select * from defaults where default_start_date < ?1 " +
         "ORDER BY default_start_date DESC LIMIT 1", nativeQuery = true)
    Optional<Defaults> findClosedDefaultValue(Date date);
}
