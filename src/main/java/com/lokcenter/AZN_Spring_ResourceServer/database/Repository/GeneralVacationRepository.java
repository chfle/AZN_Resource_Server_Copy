package com.lokcenter.AZN_Spring_ResourceServer.database.Repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.GeneralVacation;
import org.springframework.data.repository.CrudRepository;

/**
 * Fetch GeneralVacation Objects from DB
 *
 * @version 1.5 2022-19-7
 */
public interface GeneralVacationRepository extends CrudRepository<GeneralVacation, Long> {
}
