package com.lokcenter.AZN_Spring_ResourceServer.database.interfaces;

import java.sql.Date;

/**
 * Extract Start date End date from User
 */
public interface IStartEnd {
    Date getStart();
    Date getLast();
}
