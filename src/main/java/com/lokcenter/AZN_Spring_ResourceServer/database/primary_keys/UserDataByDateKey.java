package com.lokcenter.AZN_Spring_ResourceServer.database.primary_keys;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

/**
 * Composite Key for user_data_by_date query
 */
@PrimaryKeyClass
public class UserDataByDateKey implements Serializable {
    /**
     * Partition Key
     */
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @Setter
    @Getter
    private UUID user_id;

    /**
     * Cluster Key
     */
    @PrimaryKeyColumn(name = "set_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @Setter
    @Getter
    private Date set_date;
}
