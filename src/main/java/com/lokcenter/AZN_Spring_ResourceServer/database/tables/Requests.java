package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.primary_keys.RequestsKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.sql.Date;

/**
 * Q5 requests
 */

@Table("requests")
public class Requests {
    /**
     * Partition Key
     */
    @Setter
    @Getter
    @PrimaryKey
    private RequestsKey requestsKey;

    @Setter
    @Getter
    @CassandraType(type = CassandraType.Name.DATE)
    private Date endDate;

    @Setter
    @Getter
    private String type;
}
