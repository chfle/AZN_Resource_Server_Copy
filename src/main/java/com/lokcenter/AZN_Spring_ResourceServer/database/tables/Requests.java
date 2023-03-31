package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IUuidable;
import com.lokcenter.AZN_Spring_ResourceServer.database.keys.RequestsKey;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.Mergeable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Entity
@IdClass(RequestsKey.class)
public class Requests implements Serializable, IUuidable {
    @Id
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
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