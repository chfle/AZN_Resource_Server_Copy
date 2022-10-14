package com.lokcenter.AZN_Spring_ResourceServer.database.keys;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
public class DayPlanDataKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @Setter
    @Getter
    private Users users;

    @Setter
    @Getter
    private Date setDate;
}