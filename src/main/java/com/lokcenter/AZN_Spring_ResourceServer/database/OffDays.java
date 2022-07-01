package com.lokcenter.AZN_Spring_ResourceServer.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Table for holidays and vacation
 *
 * @version 1.01 2022-06-07
 */
@Entity
@NoArgsConstructor
@Table(name = "off_days")
public class OffDays {
    /**
     * Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter
    @Column(name = "id")
    private Long a_id;

    @Column(nullable = false, name = "date")
    @Temporal(TemporalType.DATE)
    @Setter @Getter
    private Date b_date;

    @Column(nullable = false, name = "general_vacation")
    @Setter @Getter
    private int c_generalVacation;

    @Column(nullable = false, name = "official_holiday")
    @Setter @Getter
    private int d_officialHoliday;

    @Column(nullable = true, name = "comment")
    @Setter @Getter
    private String e_comment;
}
