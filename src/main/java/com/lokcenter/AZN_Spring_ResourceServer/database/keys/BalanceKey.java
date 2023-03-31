package com.lokcenter.AZN_Spring_ResourceServer.database.keys;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.sql.Date;

/**
 * Composite Key for Balance Table
 */
public class BalanceKey implements Serializable {
    @Setter
    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Setter
    @Getter
    @Column(name = "year")
    private int year;
}
