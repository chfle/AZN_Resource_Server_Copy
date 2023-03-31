package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IUuidable;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.DayPlanDataKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes.DayTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

@Entity
@IdClass(DayPlanDataKey.class)
public class DayPlanData implements Serializable, IUuidable {
    @Serial
    private static final long serialVersionUID = 1L;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyToOne(LazyToOneOption.NO_PROXY)
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
    @Column(name = "set_date")
    private Date setDate;


    @Setter
    private String comment;

    public String getComment() {
        if (Optional.ofNullable(comment).isPresent()) {
            return comment;
        } else {
            return "";
        }
    }

    @Setter
    @Column(columnDefinition = "boolean default false")
    private Boolean sick;

    public Boolean getSick() {
       if (Optional.ofNullable(sick).isPresent()) {
           return sick;
       } else {
           return false;
       }
    }

    @Setter
    @Column(columnDefinition = "boolean default false")
    private Boolean school;

    public Boolean getSchool() {
        if (Optional.ofNullable(school).isPresent()) {
            return school;
        } else {
            return false;
        }
    }

    @Setter
    @Column(columnDefinition = "boolean default false")
    private Boolean vacation;

    public Boolean getVacation() {
        if (Optional.ofNullable(vacation).isPresent()) {
            return vacation;
        } else {
            return false;
        }
    }

    @Setter
    @Column(columnDefinition = "boolean default false")
    private Boolean glaz;

    public Boolean getGlaz() {
        if (Optional.ofNullable(glaz).isPresent()) {
            return glaz;
        } else {
            return false;
        }
    }

    /**
     * Holiday should be queried by general vacation
     *
     * @implNote holiday should not be pushed to the database
     */
    @Setter
    @Transient
    @Column(columnDefinition = "boolean default false")
    private Boolean holiday;

    public Boolean getHoliday() {
        if (Optional.ofNullable(holiday).isPresent()) {
            return holiday;
        } else {
            return false;
        }
    }

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
