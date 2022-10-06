package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Users{
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
    @OneToMany(mappedBy = "users")
    private Set<Requests> requests;
}
