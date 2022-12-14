package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.MessageTypes;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Save messages between users
 */

@Entity
public class Messages implements Serializable {
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Setter
    @Getter
    @Column(nullable = false)
    private String message;

    @ManyToOne
    @Setter
    @Getter
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
    @Column(nullable = false)
    private Boolean read = false;

    @Setter
    @Getter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageTypes messageType;

    /**
     * Data
     *
     * @implSpec Example Monthplan year, month
     */
    @Setter
    @Getter
    @Type(type = "hstore")
    @Column(columnDefinition = "hstore", nullable = false)
    private Map<String, Object> monthTypeData = new HashMap<>();
}
