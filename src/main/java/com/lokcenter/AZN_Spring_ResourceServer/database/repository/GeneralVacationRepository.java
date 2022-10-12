package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.GeneralVacationKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;

public interface GeneralVacationRepository extends CrudRepository<GeneralVacation, GeneralVacationKey> {
    // select * from general_vacation where date between '09-26-2022' and '11-6-2022'
    @Query()
    Iterable<GeneralVacation> getGeneralVacationByDateBetween(Date start, Date end);
}
