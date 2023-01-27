package com.lokcenter.AZN_Spring_ResourceServer.database.repository;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Balance;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends CrudRepository<Balance, BalanceRepository> {
    @Query(value = "select * from balance where user_id = ?1 and year = ?2" , nativeQuery = true)
    Optional<Balance> findBalanceByUsersAndYear(Long userId, int year);

    @Query(value = "select * from balance where user_id = ?1" , nativeQuery = true)
    Iterable<Balance> findByUsers(Long userId);
}
