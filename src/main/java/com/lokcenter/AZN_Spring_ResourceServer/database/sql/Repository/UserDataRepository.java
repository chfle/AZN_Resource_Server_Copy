package com.lokcenter.AZN_Spring_ResourceServer.database.sql.Repository;


import com.lokcenter.AZN_Spring_ResourceServer.database.sql.UserData;
import org.springframework.data.repository.CrudRepository;

/**
 * @version 1.0 2022-06-07
 */
public interface UserDataRepository extends CrudRepository<UserData, Long> {
}
