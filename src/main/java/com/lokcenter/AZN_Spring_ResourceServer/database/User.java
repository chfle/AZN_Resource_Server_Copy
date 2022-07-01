package com.lokcenter.AZN_Spring_ResourceServer.database;

import com.azure.core.annotation.Get;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

/**
 * User table
 *
 * @version 1.05 2022-06-04
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * Table id
     *
     * @implNote Auto generated in mariadb
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter
    private Long id;
    /**
     * username
     *
     * @implNote NOT NULL
     */
    @Column(nullable = false, unique = true)
    @Setter @Getter
    private String username;
    /**
     * password
     *
     * @implNote NOT NULL
     */

    /**
     * Save first login
     */
    @Column(nullable = false)
    @Setter @Getter
    private Date firstLogin;
 }
