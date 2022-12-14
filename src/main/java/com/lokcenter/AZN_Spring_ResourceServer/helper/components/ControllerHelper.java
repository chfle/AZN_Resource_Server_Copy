package com.lokcenter.AZN_Spring_ResourceServer.helper.components;

import com.lokcenter.AZN_Spring_ResourceServer.database.enums.RequestTypeEnum;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.DayPlanDataRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.DayPlanData;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Requests;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
}
