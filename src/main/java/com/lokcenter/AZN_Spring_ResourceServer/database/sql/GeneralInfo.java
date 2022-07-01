package com.lokcenter.AZN_Spring_ResourceServer.database.sql;

import com.lokcenter.AZN_Spring_ResourceServer.helper.UserDepending;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.sql.Time;

/**
 * General Calendar info
 *
 * @version 1.0 2022-06-07
 */

@UserDepending
@Entity
@NoArgsConstructor
@Table(name = "general_info")
public class GeneralInfo {
    /**
     * Primary Key
     */
    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter @Getter
    private Long a_id;

    /**
     *  Default vacation days for all users
     */
    @Column(nullable = false, name = "vacation_normal")
    @Setter @Getter
    private Long b_vacationNormal;

    /**
     *  Vacation for handicapped users
     */
    @Column(nullable = false, name = "vacation_handicap")
    @Setter @Getter
    private Long c_vacationHandicap;

    /**
     * weekend factor
     */
    @Column(nullable = false, name = "weekend_factor")
    @Setter @Getter
    private double d_weekendFactor;

    /**
     * Default work time like 8:00 Hours or 7:42
     */
    @Column(nullable = false, name = "daily_worktime")
    @Setter @Getter
    private Time e_dailyWorktime;

    /**
     * Default pause value
     */
    @Column(nullable = false, name = "daily_pause")
    @Setter @Getter
    private short f_dailyPause;
}
