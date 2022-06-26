package com.lokcenter.AZN_Spring_ResourceServer.database.Repository;


import com.lokcenter.AZN_Spring_ResourceServer.database.UserData;
import org.springframework.data.repository.CrudRepository;

/**
 * @version 1.0 2022-06-07
 */
public interface UserDataRepository extends CrudRepository<UserData, Long> {
}
