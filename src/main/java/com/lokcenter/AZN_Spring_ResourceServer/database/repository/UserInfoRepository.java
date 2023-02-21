package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    @Query(value = "select * from user_info where user_id = ?1", nativeQuery = true)
    Optional<UserInfo> findByUserId(Long userid);

}
