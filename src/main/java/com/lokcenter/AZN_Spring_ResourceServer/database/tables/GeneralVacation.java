package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.GeneralVacationKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Date;

@Entity
@ToString
@IdClass(GeneralVacationKey.class)
public class GeneralVacation {
    @Id
    @Setter
    @Getter
    private int year;

    @Id
    @Setter
    @Getter
    private Date date;

    @Setter
    @Getter
    private String comment;

    @Setter
    @Getter
    @Column(columnDefinition = "uuid")
    private String uuid;
}
