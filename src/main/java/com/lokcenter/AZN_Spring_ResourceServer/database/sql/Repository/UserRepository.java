package com.lokcenter.AZN_Spring_ResourceServer.database.sql.Repository;


import com.lokcenter.AZN_Spring_ResourceServer.database.sql.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Fetch Users from DB
 *
 * @version 1.06 2022-06-04
 */
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Find user with userdata and role
     * @param username the username
     * @return A user
     */
    @Query(value = "SELECT user.* from user WHERE username = ?1", nativeQuery = true)
    Optional<User> findByUsername(String username);
}

