package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Users implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Long UserId;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String username;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date firstLogin;

    /* User has many requests */
    @Setter
    @Getter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "users")
    private Set<Requests> requests;
}
