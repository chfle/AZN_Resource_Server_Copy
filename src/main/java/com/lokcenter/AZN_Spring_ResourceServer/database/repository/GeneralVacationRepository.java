package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.GeneralVacationKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import org.springframework.data.repository.CrudRepository;

public interface GeneralVacationRepository extends CrudRepository<GeneralVacation, GeneralVacationKey> {
}
