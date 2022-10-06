package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.keys.UserInfoKey;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IdClass(UserInfoKey.class)
@Entity
@TypeDefs({
        @TypeDef(name = "list-array", typeClass = ListArrayType.class),
        @TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
})
public class UserInfo {
    public enum WORKIME {
        END, START, PAUSE
    }


    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    @Getter
    private Users users;

    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userinfoId;

    @Setter
    @Getter
    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    private Map<Date, Map<WORKIME, Object>> workTime = new HashMap<>();

    @Type(type = "list-array")
    @Column(
            name = "available_vacation" ,
            columnDefinition = "integer[]"
    )
    @Setter
    @Getter
    private List<Integer> availableVacation = new ArrayList<>();

    @Type(type = "list-array")
    @Column(
            name = "balance_time",
            columnDefinition = "time[]"
    )
    @Setter
    @Getter
    private List<Time> balanceTime = new ArrayList<>();

    @Type(type = "list-array")
    @Column(
            name = "glaz_days" ,
            columnDefinition = "integer[]"
    )
    @Setter
    @Getter
    private List<Integer> glazDays = new ArrayList<>();

    @Type(type = "list-array")
    @Column(
            name = "sick_days" ,
            columnDefinition = "integer[]"
    )
    @Setter
    @Getter
    private List<Integer> SickDays = new ArrayList<>();

    @Type(type = "list-array")
    @Column(
            name = "vacation_sick" ,
            columnDefinition = "integer[]"
    )
    @Setter
    @Getter
    private List<Integer> vacationSick = new ArrayList<>();

    @Type(type = "list-array")
    @Column(
            name = "school",
            columnDefinition = "integer[]"
    )
    @Setter
    @Getter
    private List<Integer> school = new ArrayList<>();
}
