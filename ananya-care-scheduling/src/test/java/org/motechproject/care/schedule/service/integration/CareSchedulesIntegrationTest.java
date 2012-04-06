package org.motechproject.care.schedule.service.integration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.delivery.schedule.util.FakeSchedule;
import org.motechproject.delivery.schedule.util.ScheduleVisualization;
import org.motechproject.delivery.schedule.util.ScheduleWithCapture;
import org.motechproject.delivery.schedule.util.SetDateAction;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Date;

import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-Care.xml")
public class CareSchedulesIntegrationTest extends BaseUnitTest {
    private static final int JANUARY = 1;
    private static final int FEBRUARY = 2;
    private static final int MARCH = 3;
    private static final int APRIL = 4;
    private static final int MAY = 5;
    private static final int JUNE = 6;
    private static final int JULY = 7;
    private static final int AUGUST = 8;
    private static final int SEPTEMBER = 9;
    private static final int OCTOBER = 10;
    private static final int NOVEMBER = 11;

    @Autowired
    private ScheduleTrackingService trackingService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private ScheduleWithCapture schedule;
    private ScheduleVisualization visualization;

    @Test
    public void shouldProvideAlertsForTT1() throws Exception {
        schedule.enrollFor("TT Vaccination", newDate(2012, 1, 1), new Time(14, 0));
        schedule.assertAlerts("TT 1", due, date(1, JANUARY));
        visualization.outputTo("mother-tt.html", 3);

    }

    @Test
    public void shouldProvideAlertsForTT2After4WeeksOfTT1Taken() throws Exception {
        schedule.enrollFor("TT Vaccination", newDate(2012, 1, 1), new Time(14, 0));
        schedule.assertAlerts("TT 2", due, date(15 , JANUARY));
        visualization.outputTo("mother-tt.html", 3);

    }

    @Before
    public void setUp() throws Exception {
        FakeSchedule fakeSchedule = new FakeSchedule(trackingService, schedulerFactoryBean, new SetDateAction() {
            @Override
            public void setTheDateTo(LocalDate date) {
                mockCurrentDate(date);
            }
        });

        String outputDir = null;
        if (new File("ananya-care-scheduling").exists()) {
            outputDir = "ananya-care-scheduling/doc/schedules/";
        }
        else if (new File("doc").exists()) {
            outputDir = "doc/schedules/";
        }
        visualization = new ScheduleVisualization(fakeSchedule, outputDir);

        schedule = new ScheduleWithCapture(fakeSchedule, visualization);
    }

    @BeforeClass
    public static void turnOffSpringLogging() {
        Logger logger = Logger.getLogger("org.springframework");
        logger.setLevel(Level.FATAL);
    }

    private Date date(int day, int month) {
        return dateWithYear(day, month, 2012);
    }

    private Date dateWithYear(int day, int month, int year) {
        return new DateTime(year, month, day, 14, 0).toDate();
    }
}
