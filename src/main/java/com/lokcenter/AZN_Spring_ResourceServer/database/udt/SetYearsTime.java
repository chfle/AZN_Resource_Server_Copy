package com.lokcenter.AZN_Spring_ResourceServer.database.udt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.sql.Time;

@UserDefinedType("set_years_time")
public class SetYearsTime {
    @Setter
    @Getter
    private Time year1;

    @Setter
    @Getter
    private Time year2;

    @Setter
    @Getter
    private Time year3;

    @Setter
    @Getter
    private Time year4;

    @Setter
    @Getter
    private Time year5;
}
