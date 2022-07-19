package com.lokcenter.AZN_Spring_ResourceServer.database;

import com.azure.core.annotation.Get;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;

/**
 * UserData table
 *
 * @version 19-06-2022
 */
@Entity
@Table(name = "user_data")
@ToString
@NoArgsConstructor
public class UserData implements Serializable {
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

    /**
     * @implNote User can be deleted only when all references are removed.
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", unique = true)
    @Setter
    @Getter
    private User user_id;

    @Column(nullable = false, name = "vacation_sick")
    @Getter
    @Setter
    private int vacationSick;

    @Column(nullable = false, name = "glaz_days")
    @Getter
    @Setter
    private int glazDays;

    @Column(nullable = false, name = "sick_days")
    @Getter
    @Setter
    private int sickDays;

    @Column(nullable = false, name = "used_vacation")
    @Getter
    @Setter
    private int usedVacation;

    @Column(nullable = false, name = "balance_time")
    @Getter
    @Setter
    private Time balanceTime;

    @Column(nullable = false, name = "available_vacation")
    @Getter
    @Setter
    private int availableVacation;
}
