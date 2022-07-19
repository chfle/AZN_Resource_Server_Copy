package com.lokcenter.AZN_Spring_ResourceServer.database.Repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.UserData;
import org.springframework.data.repository.CrudRepository;

/**
 * Fetch UserData from DB
 *
 * @version 1.06 2022-06-04
 */
public interface UserDataRepository extends CrudRepository<UserData, Long> {

}
