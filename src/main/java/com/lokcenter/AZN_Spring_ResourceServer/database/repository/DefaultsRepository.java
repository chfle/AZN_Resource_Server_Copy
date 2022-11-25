package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Defaults;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;

public interface DefaultsRepository extends CrudRepository<Defaults, Date> {
}
