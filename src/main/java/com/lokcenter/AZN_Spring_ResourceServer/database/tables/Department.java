package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.DepartmentEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Department {
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Enumerated(EnumType.STRING)
    @Setter
    @Getter
    private DepartmentEnum department;

    /* Department belongs to multiple users */
    @Setter
    @Getter
    @OneToMany(mappedBy = "department")
    private Set<Users> users;
}
