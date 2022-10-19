package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.UUIDable;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes.DayTime;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@Entity
@IdClass(DayPlanDataKey.class)
public class DayPlanData implements Serializable, UUIDable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "user_id")
    @Setter
    @Getter
    private Users users;

    @Id
    @Setter
    @Getter
    private Date setDate;

    @Setter
    @Getter
    private String comment;

    @Setter
    @Getter
    private Boolean sick;

    @Setter
    @Getter
    private Boolean school;

    @Setter
    @Getter
    private Boolean vacation;

    @Setter
    @Getter
    private Boolean glaz;

    @Setter
    @Getter
    @Embedded
    @AttributeOverride(name = "end", column = @Column(name = "worktime_end"))
    @AttributeOverride(name = "pause", column = @Column(name = "worktime_pause"))
    @AttributeOverride(name = "start", column = @Column(name = "worktime_start"))
    private DayTime WorkTime;

    @Setter
    @Getter
    @Column(columnDefinition = "uuid")
    private String uuid;

    @Setter
    @Getter
    private Boolean checked;

    public static Tags getTag(DayPlanData dayPlanData) {
        // NOTE: Priority: 1. Krank 2. Urlaub 3.
        if (dayPlanData.getSick() != null && dayPlanData.getSick()) {
            return Tags.Krank;
        } else if (dayPlanData.getVacation() != null && dayPlanData.getVacation()) {
            return Tags.Urlaub;
        } else if (dayPlanData.getGlaz() != null && dayPlanData.getGlaz()) {
            return Tags.GLAZ;
        } else {
            return Tags.Urlaub;
        }
    }
}
