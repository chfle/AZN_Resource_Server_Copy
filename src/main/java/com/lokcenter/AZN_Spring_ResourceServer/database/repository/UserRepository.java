package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    @Query(value = "select user_id from users", nativeQuery = true)
    Iterable<BigInteger> getAllUserIds();
}
