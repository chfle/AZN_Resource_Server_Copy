package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes.DayTime;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@NoArgsConstructor
public class WorkTime implements Serializable {
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long WorkTimeId;

    @Setter
    @Getter
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @Setter
    @Getter
    @JsonBackReference
    private Users users;


    @Setter
    @Getter
    @Embedded
    @AttributeOverride(name = "end", column = @Column(name = "worktime_end"))
    @AttributeOverride(name = "pause", column = @Column(name = "worktime_pause"))
    @AttributeOverride(name = "start", column = @Column(name = "worktime_start"))
    private DayTime WorkTime;
}
