package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.RequestsKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import org.springframework.data.repository.CrudRepository;

public interface RequestsRepository extends CrudRepository<Requests, RequestsKey> {
}
