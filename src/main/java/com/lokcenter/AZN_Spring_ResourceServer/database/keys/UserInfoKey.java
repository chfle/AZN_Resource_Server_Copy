package com.lokcenter.AZN_Spring_ResourceServer.database.keys;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class UserInfoKey implements Serializable {
    @Setter
    @Getter
    @Column(name = "user_id")
    private Long userId;

    @Setter
    @Getter
    private Long userinfoId;
}
