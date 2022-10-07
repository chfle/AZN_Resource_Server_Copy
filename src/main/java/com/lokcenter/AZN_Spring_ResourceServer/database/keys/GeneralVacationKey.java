package com.lokcenter.AZN_Spring_ResourceServer.database.keys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

@AllArgsConstructor
public class GeneralVacationKey implements Serializable {
    @Setter
    @Getter
    private int year;

    @Setter
    @Getter
    private Date date;
}