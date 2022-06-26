package com.lokcenter.AZN_Spring_ResourceServer.database;

import javax.persistence.*;
import java.sql.Date;

/**
 * User table
 *
 * @version 1.05 2022-06-04
 */
@Entity
public class User {
    /**
     * Table id
     *
     * @implNote Auto generated in mariadb
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * username
     *
     * @implNote NOT NULL
     */
    @Column(nullable = false, unique = true)
    private String username;
    /**
     * password
     *
     * @implNote NOT NULL
     */

    /**
     * Save first login
     */
    private Date firstLogin;

    public User(Long id, String username, Date firstLogin) {
        this.id = id;
        this.username = username;
        this.firstLogin = firstLogin;
    }

    // must be set
    public User(){}

    public Date getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Date firstLogin) {
        this.firstLogin = firstLogin;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
 }
