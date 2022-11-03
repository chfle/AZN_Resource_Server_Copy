package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.UserInfoKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.UserInfo;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, UserInfoKey> {
    @Query(value = "select * from user_info where user_id = ?1", nativeQuery = true)
    Optional<UserInfo> findByUserId(Long userid);
}
