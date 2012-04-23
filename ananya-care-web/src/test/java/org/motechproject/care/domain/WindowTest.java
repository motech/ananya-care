package org.motechproject.care.domain;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class WindowTest {

    @Test
    public void shouldBeValidIfStartFallsBeforeBeforeEnd() {
        DateTime start = DateTime.now();
        DateTime end = start.plusDays(10);

        Window window = new Window(start, end);
        Assert.assertTrue(window.isValid());
    }

    @Test
    public void shouldBeValidIfStartISameAsEnd() {
        DateTime start = DateTime.now();
        DateTime end = start;

        Window window = new Window(start, end);
        Assert.assertTrue(window.isValid());
    }

    @Test
    public void shouldBeInValidIfStartFallsAfterEnd() {
        DateTime start = DateTime.now();
        DateTime end = start.minusDays(1);

        Window window = new Window(start, end);
        Assert.assertFalse(window.isValid());
    }

    @Test
    public void shouldNotTruncateDuringResizeIfTheWindowFallsCompletelyWithinLimits() {
        DateTime start = DateTime.now();
        DateTime end = start.plusDays(10);

        Window window = new Window(start, end);
        Window truncatedWindow = window.resize(new Window(start, end));
        Assert.assertEquals(start, truncatedWindow.getStart());
        Assert.assertEquals(end, truncatedWindow.getEnd());
        Assert.assertTrue(truncatedWindow.isValid());
    }

    @Test
    public void shouldTruncateFromTheStartDuringResizeIfTheWindowStartsBeforeLimitStart() {
        DateTime start = DateTime.now();
        DateTime end = start.plusDays(10);

        Window window = new Window(start, end);
        Window truncatedWindow = window.resize(new Window(start.plusDays(1), end));
        Assert.assertEquals(start.plusDays(1), truncatedWindow.getStart());
        Assert.assertEquals(end, truncatedWindow.getEnd());
        Assert.assertTrue(truncatedWindow.isValid());
    }

    @Test
    public void shouldNotTruncateFromTheStartDuringResizeIfTheWindowStartsAfterLimitStart() {
        DateTime start = DateTime.now();
        DateTime end = start.plusDays(10);

        Window window = new Window(start, end);
        Window truncatedWindow = window.resize(new Window(start.minusDays(1), end));
        Assert.assertEquals(start, truncatedWindow.getStart());
        Assert.assertEquals(end, truncatedWindow.getEnd());
        Assert.assertTrue(truncatedWindow.isValid());
    }

    @Test
    public void shouldTruncateFromTheEndDuringResizeIfTheWindowEndsAfterLimitEnd() {
        DateTime start = DateTime.now();
        DateTime end = start.plusDays(10);

        Window window = new Window(start, end);
        Window truncatedWindow = window.resize(new Window(start, end.minusDays(1)));
        Assert.assertEquals(start, truncatedWindow.getStart());
        Assert.assertEquals(end.minusDays(1), truncatedWindow.getEnd());
        Assert.assertTrue(truncatedWindow.isValid());
    }

    @Test
    public void shouldNotTruncateFromTheEndDuringResizeIfTheWindowEndsBeforeLimitEnd() {
        DateTime start = DateTime.now();
        DateTime end = start.plusDays(10);

        Window window = new Window(start, end);
        Window truncatedWindow = window.resize(new Window(start, end.plusDays(1)));
        Assert.assertEquals(start, truncatedWindow.getStart());
        Assert.assertEquals(end, truncatedWindow.getEnd());
        Assert.assertTrue(truncatedWindow.isValid());
    }

    @Test
    public void shouldTruncateToInvalidWindowDuringResizeIfTheWindowFallsOutsideTheLimit() {
        DateTime start = DateTime.now();
        DateTime end = start.plusDays(10);

        Window window = new Window(start, end);
        Window truncatedWindow = window.resize(new Window(start.plusDays(11), start.plusDays(12)));
        Assert.assertFalse(truncatedWindow.isValid());

        truncatedWindow = window.resize(new Window(start.minusDays(11), start.minusDays(10)));
        Assert.assertFalse(truncatedWindow.isValid());
    }

}
