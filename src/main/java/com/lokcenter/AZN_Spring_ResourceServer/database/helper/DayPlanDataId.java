package com.lokcenter.AZN_Spring_ResourceServer.database.helper;


import com.lokcenter.AZN_Spring_ResourceServer.database.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * DayPlanData composite key
 *
 * @version 19-06-2022
 */
@AllArgsConstructor
public class DayPlanDataId implements Serializable {
    /**
     * @implNote serialVersionUID should be updated if each version
     */
    @Serial
    private static final long serialVersionUID = 1L;

    private User user_id;
    private Long date;
}
