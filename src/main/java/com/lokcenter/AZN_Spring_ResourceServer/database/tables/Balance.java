package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.BalanceKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

import javax.persistence.*;
import java.sql.Time;

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

    @ManyToOne(fetch = FetchType.EAGER)
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
