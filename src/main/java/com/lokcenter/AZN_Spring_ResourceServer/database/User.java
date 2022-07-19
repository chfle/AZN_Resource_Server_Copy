package com.lokcenter.AZN_Spring_ResourceServer.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

/**
 * User table
 *
 * @version 19-06-2022
 */
@Entity
@Table(name = "user")
@ToString
@NoArgsConstructor
public class User implements Serializable {
    /**
     * @implNote serialVersionUID should be updated if each version
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
     * username
     *
     * @implNote NOT NULL
     */
    @Column(nullable = false, unique = true)
    @Setter
    @Getter
    private String username;

    /**
     * Save first login
     */
    @Column(nullable = false, name = "first_login")
    @Setter
    @Getter
    private Date firstLogin;
}
