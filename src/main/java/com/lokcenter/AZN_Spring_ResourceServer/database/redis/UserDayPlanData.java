package com.lokcenter.AZN_Spring_ResourceServer.database.redis;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * Redis UserDayplanData Hash
 *
 * @version 05-07-2022
 */

@Data
@RedisHash
@RequiredArgsConstructor
public class UserDayPlanData {
    /**
     * NOTE: Must be a String
     */
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
