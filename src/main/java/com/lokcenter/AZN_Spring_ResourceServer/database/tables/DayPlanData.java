package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.UUIDable;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes.DayTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Entity
@IdClass(DayPlanDataKey.class)
public class DayPlanData implements Serializable, UUIDable {
    @Serial
    private static final long serialVersionUID = 1L;

    @LazyToOne(LazyToOneOption.NO_PROXY)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @Setter
    @Getter
    @JsonBackReference
    private Users users;

    @Id
    @Setter
    @Getter
    @Column(name = "user_id")
    Long userId;

    @Id
    @Setter
    @Getter
    @Column(name = "set_date")
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

    /**
     * Holiday should be queried by general vacation
     *
     * @implNote holiday should not be pushed to the database
     */
    @Setter
    @Getter
    @Transient
    private Boolean holiday;

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
    private UUID uuid;

    @Setter
    @Getter
    private Boolean checked;

    /**
     * isValid will be used to check if a dayplan is valid or not
     *
     * @implNote: This field should not be included in any database table
     */
    @Setter
    @Getter
    @org.springframework.data.annotation.Transient
    private boolean isValid;

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

    @Override
    public String toString() {
        return "DayPlanData{" +
                "comment='" + comment + '\'' +
                ", sick=" + sick +
                ", school=" + school +
                ", vacation=" + vacation +
                ", glaz=" + glaz +
                ", WorkTime=" + WorkTime +
                '}';
    }
}
