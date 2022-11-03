package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.UserInfoKey;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@IdClass(UserInfoKey.class)
@Entity
@TypeDefs({
        @TypeDef(name = "list-array", typeClass = ListArrayType.class),
        @TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
})
public class UserInfo {

    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkTime {
        @Setter
        @Getter
        private Timestamp start;

        @Setter
        @Getter
        private Timestamp end;

        @Setter
        @Getter
        private Time pause;
    }

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userinfoId;

    /**
     * Work time -> Start Date and the Worktime
     */
    @Setter
    @Getter
    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    private Map<Date, WorkTime> workTime = new HashMap<>();

    /**
     * A Java Map with Year and available Vacation for each year
     */
    @Type(type = "hstore")
    @Column(
            name = "available_vacation" ,
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<Year, Integer> availableVacation = new HashMap<>();

    /**
     * A Java Map with Year and balance time for each year
     */
    @Type(type = "hstore")
    @Column(
            name = "balance_time",
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<Year, Time> balanceTime = new HashMap<>();

    /**
     * A Java Map with Year and glaz days for each year
     */
    @Type(type = "hstore")
    @Column(
            name = "glaz_days" ,
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<Year, Integer> glazDays = new HashMap<>();

    /**
     * A Java Map with Year and sick days for each year
     */
    @Type(type = "hstore")
    @Column(
            name = "sick_days" ,
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<Year, Integer> SickDays = new HashMap<>();

    /**
     * A Java Map with Year and vacation sick days for each year
     */
    @Type(type = "hstore")
    @Column(
            name = "vacation_sick" ,
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<Year, Integer> vacationSick = new HashMap<>();

    /**
     * A Java Map with Year and school days for each year
     */
    @Type(type = "hstore")
    @Column(
            name = "school",
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<Year, Integer> school = new HashMap<>();
}
