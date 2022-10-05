package com.lokcenter.AZN_Spring_ResourceServer.database.valueTypes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.sql.Timestamp;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class DayTime {
    @NotNull
    @Column(nullable = false)
    @Setter
    @Getter
    private Timestamp end;

    @Setter
    @Getter
    @NotNull
    @Column(nullable = false)
    private Time pause;

    @NotNull
    @Column(nullable = false)
    @Setter
    @Getter
    private Timestamp start;
}
