package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.controller.OverviewController;
import com.lokcenter.AZN_Spring_ResourceServer.database.enums.Tags;
import com.lokcenter.AZN_Spring_ResourceServer.database.interfaces.UUIDable;
import com.lokcenter.AZN_Spring_ResourceServer.database.repository.GeneralVacationRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.GeneralVacation;
import com.lokcenter.AZN_Spring_ResourceServer.helper.components.ControllerHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class GeneralVacationService {
    @Autowired
    private final GeneralVacationRepository generalVacationRepository;

    @Autowired
    private final ControllerHelper controllerHelper;


    /**
     * get the right min and max range from general vacation
     */
    @Async
    public CompletableFuture<Set<OverviewController.DateRange>> MinMaxGeneralVacation(Date start, Date end) {
         Set<OverviewController.DateRange> dateRanges = new HashSet<>();

        var generalVacations =
                generalVacationRepository.
                        getGeneralVacationByDateBetween(start, end);

        // map all general vacation with the same comment
        Map<UUID, ArrayList<UUIDable>> generalVacationByUUID = controllerHelper.mapByUUID(generalVacations);


        // get min and max date from general vacation
        for (var gv: generalVacationByUUID.entrySet()) {
            if (gv.getValue().size() > 1) {
                dateRanges.add(new OverviewController.DateRangeComment(
                        gv.getValue().stream().map(uuiDable ->
                                ((GeneralVacation)uuiDable).getDate()).min(Date::compareTo).get(),
                        gv.getValue().stream().map(uuiDable ->
                                ((GeneralVacation)uuiDable).getDate()).max(Date::compareTo).get(),
                        ((GeneralVacation)gv.getValue().get(0)).getTag() == Tags.gFeiertag ? Tags.gFeiertag: Tags.gUrlaub,
                        gv.getKey(),
                        ((GeneralVacation)gv.getValue().get(0)).getComment()
                ));
            } else {
                GeneralVacation generalVacation = (GeneralVacation)gv.getValue().get(0);
                dateRanges.add(
                        new OverviewController.DateRangeComment(generalVacation.getDate(),
                                generalVacation.getDate(),
                                generalVacation.getTag() == Tags.gFeiertag ? Tags.gFeiertag: Tags.gUrlaub,
                                gv.getKey(),
                                generalVacation.getComment()
                        ));
            }
        }

        return CompletableFuture.completedFuture(dateRanges);
    }
}
