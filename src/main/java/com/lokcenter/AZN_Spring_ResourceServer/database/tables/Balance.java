package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.BalanceKey;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * Balance
 *
 * @implNote Save total balance time to the database after all 12 months are submitted. Create new Balance Time for the
 * next year with data of the last year
 */
@Entity
@IdClass(BalanceKey.class)
public class Balance {
    @Setter
    @Getter
    @Column(nullable = false)
    private int balanceHours;

    @Setter
    @Getter
    @Column(nullable = false)
    private int balanceMinutes;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserInfo.Balance balance;

    @Setter
    @Getter
    @Id
    @Column(nullable = false)
    private int year;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = false, insertable = false, updatable = false)
    @Setter
    @Getter
    @JsonBackReference
    private Users users;

    @Id
    @Setter
    @Getter
    @Column(name = "user_id")
    private Long userId;
}
