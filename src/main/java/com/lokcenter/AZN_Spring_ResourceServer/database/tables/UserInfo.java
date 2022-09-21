package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.udt.DailyWorktime;
import com.lokcenter.AZN_Spring_ResourceServer.database.udt.SetYearsInt;
import com.lokcenter.AZN_Spring_ResourceServer.database.udt.SetYearsTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Q3
 */

@Table("user_info")
public class UserInfo {
    @PrimaryKey
    @Setter
    @Getter
    private UUID userId;

    @Setter
    @Getter
    private SetYearsInt availableVacation;

    @Setter
    @Getter
    private SetYearsTime balanceTime;

    @Setter
    @Getter
    private SetYearsInt glazDays;

    @Setter
    @Getter
    private SetYearsInt sickDays;

    @Setter
    @Getter
    private SetYearsInt usedVacation;

    @Setter
    @Getter
    private SetYearsInt vacationSick;

    @Setter
    @Getter
    private DailyWorktime worktime;
}
