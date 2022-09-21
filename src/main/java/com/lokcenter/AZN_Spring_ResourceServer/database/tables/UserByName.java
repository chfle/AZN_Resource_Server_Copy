package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.helper.UserDepending;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Q1 -> user_by_name
 */

@Table("user_by_name")
@UserDepending
@RequiredArgsConstructor
public class UserByName {
    /**
     * Partition Key
     */
    @PrimaryKey
    @Setter
    @Getter
    private UUID userid;

    /**
     * Query by username
     */
    @Setter
    @Getter
    private String username;
}
