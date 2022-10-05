package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.repository.CrudRepository;

public interface UserInfoRepository extends CrudRepository<Users, Long> {
}
