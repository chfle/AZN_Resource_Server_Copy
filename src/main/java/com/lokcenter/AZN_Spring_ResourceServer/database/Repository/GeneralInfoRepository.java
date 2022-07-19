package com.lokcenter.AZN_Spring_ResourceServer.database.Repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.GeneralInfo;
import org.springframework.data.repository.CrudRepository;


/**
 * Fetch GeneralInfoRepository Objects from DB
 *
 * @version 1.5 2022-19-7
 */
public interface GeneralInfoRepository extends CrudRepository<GeneralInfo, Long> {
}
