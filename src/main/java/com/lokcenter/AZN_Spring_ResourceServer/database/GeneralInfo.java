package com.lokcenter.AZN_Spring_ResourceServer.database;

import com.lokcenter.AZN_Spring_ResourceServer.helper.UserDepending;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.sql.Time;

/**
 * General Calendar info
 *
 * @version 1.0 2022-06-07
 */

@UserDepending
@Entity
@Table(name = "general_info")
public class GeneralInfo {
    /**
     * Primary Key
     */
    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long a_id;

    /**
     *  Default vacation days for all users
     */
    @Column(nullable = false, name = "vacation_normal")
    private Long b_vacationNormal;

    /**
     *  Vacation for handicapped users
     */
    @Column(nullable = false, name = "vacation_handicap")
    private Long c_vacationHandicap;

    /**
     * weekend factor
     */
    @Column(nullable = false, name = "weekend_factor")
    private double d_weekendFactor;

    /**
     * Default work time like 8:00 Hours or 7:42
     */
    @Column(nullable = false, name = "daily_worktime")
    private Time e_dailyWorktime;

    /**
     * Default pause value
     */
    @Column(nullable = false, name = "daily_pause")
    private short f_dailyPause;

    public GeneralInfo() {}

    public Long getId() {
        return a_id;
    }

    public void setId(Long id) {
        this.a_id = id;
    }

    public Long getVacationNormal() {
        return b_vacationNormal;
    }

    public void setVacationNormal(Long vacationNormal) {
        this.b_vacationNormal = vacationNormal;
    }

    public Long getVacationHandicap() {
        return c_vacationHandicap;
    }

    public void setVacationHandicap(Long vacationHandicap) {
        this.c_vacationHandicap = vacationHandicap;
    }

    public double getWeekendFactor() {
        return d_weekendFactor;
    }

    public void setWeekendFactor(double weekendFactor) {
        this.d_weekendFactor = weekendFactor;
    }

    public Time getDailyWorktime() {
        return e_dailyWorktime;
    }

    public void setDailyWorktime(Time dailyWorktime) {
        this.e_dailyWorktime = dailyWorktime;
    }

    public short getDailyPause() {
        return f_dailyPause;
    }

    public void setDailyPause(short dailyPause) {
        this.f_dailyPause = dailyPause;
    }
}
