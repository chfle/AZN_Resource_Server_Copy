package com.lokcenter.AZN_Spring_ResourceServer.database.udt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType("set_years_int")
public class SetYearsInt {
    @Setter
    @Getter
    private int year1;

    @Setter
    @Getter
    private int year2;

    @Setter
    @Getter
    private int year3;

    @Setter
    @Getter
    private int year4;

    @Setter
    @Getter
    private int year5;
}
