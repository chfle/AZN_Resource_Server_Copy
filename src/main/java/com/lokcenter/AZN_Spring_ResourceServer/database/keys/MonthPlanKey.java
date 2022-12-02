package com.lokcenter.AZN_Spring_ResourceServer.database.keys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class MonthPlanKey implements Serializable {
    @Setter
    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Setter
    @Getter
    @Column(name = "year")
    private int year;

    @Setter
    @Getter
    @Column(name = "month")
    private int month;
}