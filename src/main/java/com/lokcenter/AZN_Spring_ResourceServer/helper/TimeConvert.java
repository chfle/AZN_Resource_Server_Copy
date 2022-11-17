package com.lokcenter.AZN_Spring_ResourceServer.helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Helper functions to convert time and date
 */
public class TimeConvert {
    /**
     * java.util.Date -> LocalDate
     */
    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * LocalDate -> java.util.Date
     */
    public static Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
