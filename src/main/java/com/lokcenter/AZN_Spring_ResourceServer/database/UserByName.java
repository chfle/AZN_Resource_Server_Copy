package com.lokcenter.AZN_Spring_ResourceServer.database;

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
@RequiredArgsConstructor
public class UserByName {
    @PrimaryKey
    @Setter
    @Getter
    private UUID userid;

    @Setter
    @Getter
    private String username;
}
