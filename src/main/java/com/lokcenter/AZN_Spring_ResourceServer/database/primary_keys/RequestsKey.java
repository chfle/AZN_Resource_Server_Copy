package com.lokcenter.AZN_Spring_ResourceServer.database.primary_keys;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@PrimaryKeyClass
public class RequestsKey implements Serializable {
    /**
     * user id
     *
     * @implNote partition key
     */
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @Setter
    @Getter
    private UUID userId;

    /**
     * request start date
     *
     * @implNote Cluster Key
     */
    @PrimaryKeyColumn(name = "start_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @Setter
    @Getter
    @CassandraType(type = CassandraType.Name.DATE)
    private Date start_date;
}
