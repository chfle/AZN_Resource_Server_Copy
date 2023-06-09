package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.MonthPlan;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    @Query(value = "select * from user_info where user_id = ?1", nativeQuery = true)
    Optional<UserInfo> findByUserId(Long userid);

    @Query(value = "select case when set_vacation->?1 is null then ('0')\\:\\:BIGINT else (set_vacation->?1)\\:\\:BIGINT end as set_vacation from user_info " +
            "where user_id = ?2", nativeQuery = true)
    Long getSetVacationByUser(String year, Long userId);

}
