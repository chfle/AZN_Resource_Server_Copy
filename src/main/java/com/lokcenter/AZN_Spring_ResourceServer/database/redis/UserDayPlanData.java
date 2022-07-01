package com.lokcenter.AZN_Spring_ResourceServer.database.redis;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash
@RequiredArgsConstructor
public class UserDayPlanData {
    @Id
    @Setter @Getter
    String id;

    @Setter @Getter
    String userId;

    @Setter @Getter
    String start_time;

    @Setter @Getter
    String end_time;

    @Setter @Getter
    String pause;

    /**
     * Name of the checked value
     */
    @Setter @Getter
    String checked_day_name;

    @Setter @Getter
    String comment;
}
