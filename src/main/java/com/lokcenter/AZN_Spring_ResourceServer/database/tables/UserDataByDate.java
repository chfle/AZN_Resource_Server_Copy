package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.lokcenter.AZN_Spring_ResourceServer.database.primary_keys.UserDataByDateKey;
import com.lokcenter.AZN_Spring_ResourceServer.database.udt.Daytime;
import com.lokcenter.AZN_Spring_ResourceServer.helper.UserDepending;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.sql.Date;

/**
 * Query user data by date
 */

@UserDepending
@Table("user_data_by_date")
public class UserDataByDate {
    /**
     * Composite Key
     */
    @PrimaryKey
    @Setter
    @Getter
    private UserDataByDateKey userDataByDateKey;

    @Setter
    @Getter
    private String comment;

    /**
     * Will be set on the first login
     *
     * @implNote Should not be changed later!
     */
    @Setter
    @Getter
    @CassandraType(type = CassandraType.Name.DATE)
    private Date firstLogin;

    @Getter
    @Setter
    private boolean school;

    @Setter
    @Getter
    private boolean vacation;

    @Setter
    @Getter
    private boolean sick;

    @Setter
    @Getter
    private Daytime workTime;
}
