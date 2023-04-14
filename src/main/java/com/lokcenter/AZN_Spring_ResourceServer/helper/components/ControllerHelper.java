package com.lokcenter.AZN_Spring_ResourceServer.helper.components;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.IUuidable;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import com.lokcenter.AZN_Spring_ResourceServer.helper.ds.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to reduce duplicate code
 */
@Component
public class ControllerHelper {
    @Autowired
    private DayPlanDataRepository dayPlanDataRepository;

    @Autowired
    private GeneralVacationRepository generalVacationRepository;
    public Optional<Requests> getValidNonExistingRequest(Map<String, Object> data, Users user) throws ParseException {
        var request = new Requests();

        request.setUsers(user);

        var format = new SimpleDateFormat("yyyy-MM-dd");

        // check if endDate is valid
        var startDate = new Date(format.parse((String) data.get("startDate")).getTime());
        var endDate = new Date(format.parse((String) data.get("endDate")).getTime());

        if (startDate.compareTo(endDate) <= 0) {
            request.setStartDate(startDate);
            request.setEndDate(endDate);

            switch ((String) data.get("tag")) {
                case "rUrlaub" -> request.setType(RequestTypeEnum.rUrlaub);
                case "rGLAZ" -> request.setType(RequestTypeEnum.rGLAZ);
            }

            request.setUuid(UUID.randomUUID());

            Optional<DayPlanData> dayPlanData = dayPlanDataRepository.
                    getDayPlanDataWhereTrue(user, startDate);

            Optional<GeneralVacation> generalVacation =
                    generalVacationRepository.getGeneralVacationByDate(startDate);

            // check if request exists
            if (dayPlanData.isEmpty() && generalVacation.isEmpty()) {
                // save
                return Optional.of(request);
            }}
        return Optional.empty();
    }

    /**
     * parse Start and end date
     * @return Pair of first and last date
     */
    public Pair<String, String> parseStartEndDate(String firstDay, String lastDay, String year, String month) {
        int yearParsed = Integer.parseInt(year);
        int monthParsed = Integer.parseInt(month);

        // we should use last year for start year
        if (monthParsed == 1) {
            yearParsed -= 1;
            monthParsed = 12;
        } else {
            // if firs day starts on a monday
            if (Integer.parseInt(firstDay) != 1) {
                monthParsed -= 1;
            }
        }

        StringBuilder startDateBuilder = new StringBuilder();
        startDateBuilder.append(firstDay).append("-").append(String.format("%02d", monthParsed)).append("-").append(yearParsed);
        String startDate = startDateBuilder.toString();

        yearParsed = Integer.parseInt(year);
        monthParsed = Integer.parseInt(month);

        // should use new year as start year
        if (monthParsed == 12) {
            yearParsed += 1;
            monthParsed = 1;
        } else {
            if (Integer.parseInt(lastDay) < 7) {
                monthParsed += 1;
            }
        }

        StringBuilder endDateBuilder = new StringBuilder();
        endDateBuilder.append(lastDay).append("-").append(String.format("%02d", monthParsed)).append("-").append(yearParsed);
        String endDate = endDateBuilder.toString();

        return new Pair<>(startDate, endDate);
    }


    /**
     * Map uuid
     */
    public Map<UUID, ArrayList<IUuidable>> mapByUUID(Iterable<? extends IUuidable> uuiDableCollection) {
        Map<UUID, ArrayList<IUuidable>> map = new HashMap<>();

        for (var uuidable : uuiDableCollection) {
            if (map.containsKey(uuidable.getUuid())) {
                map.get(uuidable.getUuid()).add(uuidable);
            } else {
                map.put(uuidable.getUuid(), new ArrayList<>(List.of(uuidable)));
            }
        }

        return map;
    }
}
