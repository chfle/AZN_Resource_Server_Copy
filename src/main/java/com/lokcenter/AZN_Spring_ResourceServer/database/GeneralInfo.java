package com.lokcenter.AZN_Spring_ResourceServer.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;

/**
 * GeneralInfo table
 *
 * @version 19-06-2022
 */
@Entity
@Table(name = "general_info")
@ToString
@NoArgsConstructor
public class GeneralInfo implements Serializable {
    /**
     * @implNote serialVersionUID should be updated after each version
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Table id
     *
     * @implNote Auto generated in mariadb
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Long id;

    @Column(nullable = false, name = "weekend_factor")
    @Setter
    @Getter
    private double weekendFactor;

    @Column(nullable = false, name = "daily_worktime")
    @Setter
    @Getter
    private Time dailyWorkTime;

    @Column(nullable = false, name = "daily_pause")
    @Setter
    @Getter
    private Time dailyPause;
}
