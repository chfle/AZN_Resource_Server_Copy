package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Time;

/**
 * Default values for all users
 */
@Entity
@NoArgsConstructor
public class Defaults {
    /**
     * Period from when the defaults apply
     */
    @Setter
    @Getter
    @Id
    private Date defaultStartDate;

    /**
     * default work time start time
     */
    @Setter
    @Getter
    private Time defaultStartTime;

    /**
     * default work time end time
     */
    @Setter
    @Getter
    private Time defaultEndTime;

    /**
     * default pause
     */
    @Setter
    @Getter
    private Time defaultPause;

    @Setter
    @Getter
    private int defaultVacationDays;
}
