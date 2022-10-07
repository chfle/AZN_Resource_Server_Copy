package com.lokcenter.AZN_Spring_ResourceServer.database.keys;

import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
public class UserInfoKey implements Serializable {
    @Setter
    @Getter
    private Users users;

    @Setter
    @Getter
    private Long userinfoId;
}