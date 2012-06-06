package org.motechproject.care.service.router;

import org.motechproject.care.service.router.action.Action;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

public class Route {
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
