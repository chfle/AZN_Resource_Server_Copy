package com.lokcenter.AZN_Spring_ResourceServer.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Q6 -> users_by_department
 */
@RequiredArgsConstructor
@Table("users_by_department")
public class UsersByDepartment {
    @Setter
    @Getter
    private String username;

}
