package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lokcenter.AZN_Spring_ResourceServer.helper.NullType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.time.Year;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@ToString
@NoArgsConstructor
public class Users implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private Long UserId;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String username;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date firstLogin;

    @Getter
    @Setter
    @Column(nullable = false)
    private Date endDate;

    /**
     * All App user roles
     *
     * @implNote Value should not be set, postgres does not support sets
     */
    @Type(type = "hstore")
    @Column(
            name = "roles",
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<String, NullType> roles = new HashMap<>();

    @Setter
    @Getter
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Requests> requests = new HashSet<>();

    @Setter
    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Messages> messages = new HashSet<>();

    @Setter
    @Getter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    @JsonManagedReference
    private Set<DayPlanData> dayPlanData = new HashSet<>();

    @Setter
    @Getter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    @JsonManagedReference
    private Set<MonthPlan> monthplans = new HashSet<>();

    @Setter
    @Getter
    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private UserInfo userInfo;

    @Setter
    @Getter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    @JsonManagedReference
    private Set<WorkTime> worktimes = new HashSet<>();

    @Setter
    @Getter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    @JsonManagedReference
    private Set<WorkTime> balanceTimes = new HashSet<>();
}
