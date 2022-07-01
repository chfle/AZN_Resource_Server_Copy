package com.lokcenter.AZN_Spring_ResourceServer.database.sql;


import com.lokcenter.AZN_Spring_ResourceServer.helper.UserDepending;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Stores user data
 */
@UserDepending
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_data")
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter
    private Long id;

    /**
     * id from User table
     */
    @ManyToOne
    @Setter @Getter
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user_id;

    /**
     * Select if handicapped
     */
    @Setter @Getter
    private int handicap;

    /**
     * Job length
     */
    @Setter @Getter
    private int job_length;

    /**
     * Positive or negative credit based on enum value
     *
     * @implNote columnDefinition = "ENUM('NEGATIVE', 'POSITIVE') must be set /
     * for mysql
     */
    @Column(nullable = false, columnDefinition = "ENUM('NEGATIVE', 'POSITIVE')", name = "balance_time")
    @Setter @Getter
    private Balance a_balanceTime;

    /**
     * All vacation days used so far
     */
    @Column(nullable = false, name = "used_vacationDays")
    @Setter @Getter
    private Long b_usedVacationDays;

    /**
     * All sick days
     */
    @Column(nullable = false, name = "sick_days")
    @Setter @Getter
    private int c_sickDays;

    /**
     * All GLAZ days
     */
    @Column(nullable = false, name = "glaz_days")
    @Setter @Getter
    private int d_glazDays;

    /**
     * Count all vacation days while sick to get them back
     */
    @Column(nullable = false, name = "vacation_while_sick")
    @Setter @Getter
    private Long e_vacationWhileSick;
}
