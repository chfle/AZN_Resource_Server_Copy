package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.UUIDable;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.RequestsKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Entity
@IdClass(RequestsKey.class)
public class Requests implements Serializable, UUIDable {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    @Getter
    @JsonBackReference
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
    private UUID uuid;
}