package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.RequestsKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@IdClass(RequestsKey.class)
public class Requests implements Serializable {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    @Getter
    private Users users;

    @Id
    @Setter
    @Getter
    private Date startDate;

    @Id
    @Setter
    @Getter
    private Date endDate;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private RequestTypeEnum type;

    @Setter
    @Getter
    @Column(columnDefinition = "uuid")
    private String uuid;
}