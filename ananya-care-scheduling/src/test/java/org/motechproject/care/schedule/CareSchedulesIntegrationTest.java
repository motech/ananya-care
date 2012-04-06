package org.motechproject.care.schedule;

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
/*
    @Test
    public void shouldProvideAlertsFordABC2After4WeeksOfTT1Taken() throws Exception {
        schedule.enrollFor("TT Vaccination1", DateUtil.today(), new Time(14, 0));
        schedule.assertAlerts("TT Y1", due, DateUtil.today().plusWeeks(2).toDate());
        visualization.outputTo("mother-tt.html", 3);

    }

    @Test
    public void shouldProvideAlertsForXXAfter4WeeksOfStart() throws Exception {
        schedule.enrollFor("TT Vaccination", newDate(2012, 1, 1), new Time(14, 0));
        schedule.assertAlerts("TT 2", earliest, date(15 , JANUARY));
        visualization.outputTo("mother-tt.html", 3);

    }

    @Test
    public void shouldProvideAlertsForANCAtTheRightTimes() throws Exception {
        schedule.enrollFor("Ante Natal Care - Normal", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("ANC 1", earliest);
        schedule.assertAlerts("ANC 1", due, date(11, MARCH), date(18, MARCH), date(25, MARCH), date(1, APRIL));
        schedule.assertAlerts("ANC 1", late, date(8, APRIL), date(11, APRIL), date(15, APRIL), date(18, APRIL), date(22, APRIL));
        schedule.assertAlerts("ANC 1", max, date(24, APRIL), date(25, APRIL), date(26, APRIL));

        schedule.assertNoAlerts("ANC 2", earliest);
        schedule.assertAlerts("ANC 2", due, date(3, JUNE), date(10, JUNE), date(17, JUNE), date(24, JUNE));
        schedule.assertAlerts("ANC 2", late, date(1, JULY), date(4, JULY), date(8, JULY), date(11, JULY), date(15, JULY));
        schedule.assertAlerts("ANC 2", max, date(17, JULY), date(18, JULY), date(19, JULY));

        schedule.assertNoAlerts("ANC 3", earliest);
        schedule.assertAlerts("ANC 3", due, date(29, JULY), date(5, AUGUST), date(12, AUGUST), date(19, AUGUST));
        schedule.assertAlerts("ANC 3", late, date(26, AUGUST), date(29, AUGUST));
        schedule.assertAlerts("ANC 3", max, date(30, AUGUST), date(31, AUGUST), date(1, SEPTEMBER));

        schedule.assertNoAlerts("ANC 4", earliest);
        schedule.assertAlerts("ANC 4", due, date(2, SEPTEMBER), date(9, SEPTEMBER), date(16, SEPTEMBER));
        schedule.assertAlerts("ANC 4", late, date(23, SEPTEMBER), date(26, SEPTEMBER), date(30, SEPTEMBER), date(3, OCTOBER), date(7, OCTOBER));
        schedule.assertAlerts("ANC 4", max, date(9, OCTOBER), date(10, OCTOBER), date(11, OCTOBER));

        visualization.outputTo("mother-tt.html", 3);
    }

*/

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
