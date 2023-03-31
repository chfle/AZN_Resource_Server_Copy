package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.MonthPlanKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.Mergeable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * MonthPlan Table
 */
@IdClass(MonthPlanKey.class)
@NoArgsConstructor
@Entity
public class MonthPlan implements Serializable {
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @Setter
    @Getter
    @JsonBackReference
    private Users users;

    @Id
    @Setter
    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Setter
    @Getter
    private int year;

    @Id
    @Setter
    @Getter
    private int month;

    @Setter
    @Getter
    @Column(name = "submitted", nullable = false)
    private Boolean submitted = false;

    @Setter
    @Getter
    @Column(name = "accepted", nullable = false)
    private Boolean accepted = false;
}