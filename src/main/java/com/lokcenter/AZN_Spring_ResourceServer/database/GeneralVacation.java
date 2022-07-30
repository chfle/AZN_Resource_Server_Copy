package com.lokcenter.AZN_Spring_ResourceServer.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

/**
 * GeneralVacation table
 *
 * @version 19-06-2022
 */

@Entity
@Table(name = "general_vacation")
public class GeneralVacation implements Serializable {
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
     * Comment or note
     */
    @Column(nullable = true)
    @Setter
    @Getter
    private String comment;

    /**
     * ...
     */
    @Column(nullable = false)
    @Setter
    @Getter
    private Date date;
}