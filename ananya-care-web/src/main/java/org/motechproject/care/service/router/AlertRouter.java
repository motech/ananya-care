package org.motechproject.care.service.router;

import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.schedule.vaccinations.ExpirySchedule;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.router.action.Action;
import org.motechproject.care.service.router.action.AlertChildAction;
import org.motechproject.care.service.router.action.AlertMotherAction;
import org.motechproject.care.service.router.action.ClientExpiryAction;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.care.service.router.Matcher.any;
import static org.motechproject.care.service.router.Matcher.anyOf;

@Component
public class AlertRouter {
    private List<Route> routes;

    @Autowired
    public AlertRouter(AlertChildAction alertChildAction
            , AlertMotherAction alertMotherAction
            , ClientExpiryAction expiryAction) {
        routes = new ArrayList<Route>();
        routes.add(new Route(childSchedules(), any(), any(), alertChildAction));
        routes.add(new Route(motherSchedules(), any(), any(), alertMotherAction));
        routes.add(new Route(expirySchedules(), any(), any(), expiryAction));
    }

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handle(MotechEvent realEvent) {
        MilestoneEvent event = new MilestoneEvent(realEvent);
        MilestoneAlert milestoneAlert = event.getMilestoneAlert();

        for (Route route : routes) {
            if (route.isSatisfiedBy(event.getScheduleName(), milestoneAlert.getMilestoneName(), event.getWindowName())) {
                route.invokeAction(event);
                return;
            }
        }
        throw new NoRoutesMatchException();
    }

    private Matcher childSchedules() {
        return anyOf(ChildVaccinationSchedule.allVaccineNames());
    }

    private Matcher motherSchedules() {
        return anyOf(MotherVaccinationSchedule.allVaccineNames());
    }

    private Matcher expirySchedules() {
        return anyOf(ExpirySchedule.allVaccineNames());
    }

    private class Route {
        private final Matcher scheduleMatcher;
        private final Matcher milestoneMatcher;
        private final Matcher windowMatcher;
        private final Action action;

        public Route(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, Action action) {
            this.scheduleMatcher = scheduleMatcher;
            this.milestoneMatcher = milestoneMatcher;
            this.windowMatcher = windowMatcher;
            this.action = action;
        }

        public boolean isSatisfiedBy(String scheduleName, String milestoneName, String windowName) {
            return scheduleMatcher.matches(scheduleName) && milestoneMatcher.matches(milestoneName) && windowMatcher.matches(windowName);
        }

        public void invokeAction(MilestoneEvent event) {
            action.invoke(event);
        }
    }
}
