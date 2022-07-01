package com.lokcenter.AZN_Spring_ResourceServer.database;

import com.lokcenter.AZN_Spring_ResourceServer.helper.UserDepending;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

/**
 * Table for dayPlan data from every user
 *
 * @version 1.0 2022-06-07
 */
@UserDepending
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_dayPlanData")
public class UserDayPlanData {
    @Id
    @Setter @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * id from User table
     */

    @Column(nullable = false)
    private Date date;
    @Setter @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user_id;

    /**
     * Workday start time
     */
    @Column(nullable = true, name = "start_time")
    @Setter @Getter
    private Time startTime;

    /**
     * Workday end time
     */
    @Column(nullable = true, name = "end_time")
    @Setter @Getter
    private Time endTime;

    /**
     * Pause time
     */
    @Column(nullable = true)
    private short pause;

    /**
     * Mark if day is a school day
     */
    @Column(nullable = false, name = "school_day")
    @Setter @Getter
    private int schoolDay;

    /**
     * Mark if day is a glaz Day
     */
    @Column(nullable = false, name = "glaz_day")
    @Setter @Getter
    private int glazDay;

    /**
     * Mark if day is a vacation day
     */
    @Column(nullable = false, name = "vacation_day")
    @Setter @Getter
    private int vacationDay;

    /**
     * Mark if day is a sick day
     */
    @Column(nullable = false, name = "sick_day")
    @Setter @Getter
    private int sickDay;

    /**
     * Dayplan comment
     */
    @Column(nullable = true)
    @Setter @Getter
    private String comment;
}