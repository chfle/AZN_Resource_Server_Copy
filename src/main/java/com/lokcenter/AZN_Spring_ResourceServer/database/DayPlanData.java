package com.lokcenter.AZN_Spring_ResourceServer.database;

import com.lokcenter.AZN_Spring_ResourceServer.database.helper.DayPlanDataId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

/**
 * DayPlanData table
 *
 * @version 19-06-2022
 */

@Entity
@Table(name = "dayplan_data")
@ToString
@NoArgsConstructor
@IdClass(DayPlanDataId.class)
public class DayPlanData implements Serializable {
    /**
     * @implNote serialVersionUID should be updated after each version
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @implNote User can be deleted only when all references are removed.
     */
    @Id
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    @Setter
    @Getter
    private User user_id;

    @Id
    @Setter
    @Getter
    Date date;

    @Column(nullable = true, name = "start_time")
    @Setter @Getter
    private Time startTime;

    /**
     * Workday end time
     */
    @Column(nullable = true, name = "end_time")
    @Setter @Getter
    private Time endTime;

    /**
     * Pause time
     */
    @Column(nullable = true)
    @Setter
    @Getter
    private short pause;

    /**
     * Mark if day is a school day
     */
    @Column(nullable = false, name = "school")
    @Setter @Getter
    private int schoolDay;

    /**
     * Mark if day is a glaz Day
     */
    @Column(nullable = false, name = "glaz")
    @Setter @Getter
    private int glazDay;

    /**
     * Mark if day is a vacation day
     */
    @Column(nullable = false, name = "vacation")
    @Setter @Getter
    private int vacationDay;

    /**
     * Mark if day is a sick day
     */
    @Column(nullable = false, name = "sick")
    @Setter @Getter
    private int sickDay;

    /**
     * Dayplan comment
     */
    @Column(nullable = true)
    @Setter @Getter
    private String comment;
}
