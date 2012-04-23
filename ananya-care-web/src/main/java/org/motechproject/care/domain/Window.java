package org.motechproject.care.domain;

import org.joda.time.DateTime;

public class Window {

    private DateTime start;
    private DateTime end;

    public Window(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    public Window resize(Window limit) {
        DateTime start = this.start.isBefore(limit.start) ? limit.start : this.start;
        DateTime end = this.end.isAfter(limit.end) ? limit.end : this.end;
        return new Window(start, end);
    }

    public boolean isValid() {
        return !start.isAfter(end);
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }
}
