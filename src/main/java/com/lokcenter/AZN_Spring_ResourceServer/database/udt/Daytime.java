package com.lokcenter.AZN_Spring_ResourceServer.database.udt;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * day_time UDT
 */

@UserDefinedType("day_time")
public class Daytime {
    /**
     * end time
     */
    @Setter
    @Getter
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Timestamp end;

    /**
     * Pause total
     */
    @Setter
    @Getter
    @CassandraType(type = CassandraType.Name.TIME)
    private Time pause;

    /**
     * Start time
     */
    @Setter
    @Getter
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Timestamp start;

}
