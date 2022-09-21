package com.lokcenter.AZN_Spring_ResourceServer.database.udt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Frozen;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.Date;

@UserDefinedType("daily_worktime")
@RequiredArgsConstructor
public class DailyWorktime {
    @Setter
    @Getter
    @CassandraType(type = CassandraType.Name.DATE)
    private Date setDate;

    @Setter
    @Getter
    @Frozen
    Daytime workTime;
}
