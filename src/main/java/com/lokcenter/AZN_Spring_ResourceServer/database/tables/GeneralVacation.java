package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.primary_keys.GeneralVacationKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * general_vacation
 */
@Table("general_vacation")
public class GeneralVacation {
    /**
     * primary key
     * @implNote Composite Key
     */
    @PrimaryKey
    @Setter
    @Getter
    private GeneralVacationKey generalVacationKey;

    /**
     * Vacation Comment
     */
    @Setter
    @Getter
    private String comment;
}
