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

@PrimaryKeyClass
public class GeneralVacationKey implements Serializable {
    /**
     * Vacation Year
     *
     * @implNote Partition Key
     */
    @PrimaryKeyColumn(name = "year", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @Setter
    @Getter
    private int year;

    /**
     * Vacation day
     *
     * @implNote Cluster Key
     */
    @PrimaryKeyColumn(name = "set_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @CassandraType(type = CassandraType.Name.DATE)
    @Setter
    @Getter
    private Date setDate;
}
