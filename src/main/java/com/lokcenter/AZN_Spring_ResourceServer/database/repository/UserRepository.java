package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IStartEnd;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    @Query(value = "select user_id from users", nativeQuery = true)
    Iterable<BigInteger> getAllUserIds();

    @Query(value = "select first_login as start, case when end_date is null " +
            "then '1970-01-01' else end_date end as last from users where user_id = ?1", nativeQuery = true)
    IStartEnd getStartEndDateByUser(Long userId);
}
