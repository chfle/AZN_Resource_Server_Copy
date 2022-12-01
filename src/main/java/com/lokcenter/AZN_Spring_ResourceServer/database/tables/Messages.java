package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

/**
 * Save messages between users
 */

@Entity
public class Messages {
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String messageId;

    @Setter
    @Getter
    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private Users user;

    @Setter
    @Getter
    @Column(nullable = false)
    private Date date;

    /**
     * Is message unread?
     */
    @Setter
    @Getter
    private Boolean read;
}
