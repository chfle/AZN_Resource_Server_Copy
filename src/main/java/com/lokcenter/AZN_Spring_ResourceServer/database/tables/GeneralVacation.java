package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IUuidable;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.GeneralVacationKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

@Entity
@ToString
@IdClass(GeneralVacationKey.class)
public class GeneralVacation implements IUuidable {
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
    @Enumerated(EnumType.STRING)
    private Tags tag;

    @Setter
    @Getter
    @Column(columnDefinition = "uuid")
    private UUID uuid;
}
