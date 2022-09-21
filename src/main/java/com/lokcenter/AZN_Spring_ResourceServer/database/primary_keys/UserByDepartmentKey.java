package com.lokcenter.AZN_Spring_ResourceServer.database.primary_keys;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.DepartmentEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for user_by_department query
 */

@PrimaryKeyClass
public class UserByDepartmentKey implements Serializable {
    /**
     * Department as String -> Enum
     *
     * @implNote Partition Key
     */
    @PrimaryKeyColumn(name = " department", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = CassandraType.Name.TEXT)
    @Setter
    @Getter
    private DepartmentEnum department;

    /**
     * user id
     *
     * @implNote Cluster Key
     */
    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @Setter
    @Getter
    private UUID userId;
}
