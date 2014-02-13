package org.motechproject.care.service.util;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class PeriodUtil {
    public static final int DAYS_IN_9_MONTHS = 274;
    public static final int DAYS_IN_3RD_TRIMESTER = 91;
    private Properties ananyaCareProperties;
    private final PeriodFormatter periodFormatter;

    @Autowired
    public PeriodUtil(@Qualifier("ananyaCareProperties")Properties ananyaCareProperties){
        this.ananyaCareProperties = ananyaCareProperties;
        periodFormatter = new PeriodFormatterBuilder()
                .appendMonths()
                .appendSuffix(" month"," months")
                .appendWeeks().
                 appendSuffix(" week"," weeks")
                .appendDays()
                .appendSuffix(" day"," days").toFormatter();
    }

    public Period getScheduleOffset(){
        String scheduleOffset = ananyaCareProperties.getProperty("schedule.offset");
        return Period.parse(scheduleOffset, periodFormatter);
    }
}
