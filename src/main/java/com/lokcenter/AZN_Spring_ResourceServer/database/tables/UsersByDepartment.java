package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.primary_keys.UserByDepartmentKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Q6 -> users_by_department
 */
@RequiredArgsConstructor
@Table("users_by_department")
public class UsersByDepartment {
    @Setter
    @Getter
    private String username;

    @PrimaryKey
    @Setter
    @Getter
    private UserByDepartmentKey userByDepartmentKey;
}