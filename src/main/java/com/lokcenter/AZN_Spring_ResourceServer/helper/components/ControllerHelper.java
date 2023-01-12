package com.lokcenter.AZN_Spring_ResourceServer.helper.components;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.UUIDable;
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
        String format = "dd-MM-yyyy";

        int yearParsed = Integer.parseInt(year);
        var sdf = new SimpleDateFormat(format);

        // we should use last year for start year
        if (month.equals("01")) {
            yearParsed -= 1;
        }
        String startDate = String.format("%s-%s-%s", firstDay,
                month.equals("01")? 12 : Integer.parseInt(month) - 1,
                yearParsed);

        yearParsed = Integer.parseInt(year);

        // should use new year as start year
        if (month.equals("12")) {
            yearParsed += 1;
        }

        String endDate = String.format("%s-%s-%s", lastDay,
                month.equals("12") ? 1 : Integer.parseInt(month) + 1, yearParsed);

        return new Pair<>(startDate, endDate);
    }

    public Map<UUID, ArrayList<UUIDable>> mapByUUID(Iterable<? extends UUIDable> uuiDableCollection) {
        Map<UUID, ArrayList<UUIDable>> map = new HashMap<>();

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
