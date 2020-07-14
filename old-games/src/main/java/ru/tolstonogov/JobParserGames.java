package ru.tolstonogov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class JobParserGames implements Job {
    private static final Logger LOG = LogManager.getLogger(JobParserGames.class.getName());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        LOG.info(new StringBuilder("Parser: start at ")
                .append(new GregorianCalendar(TimeZone.getTimeZone("GMT+3:00")).getTime())
                .append('.'));
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        String baseUrl = data.getString("baseUrl");
        DataBaseGames dbV = (DataBaseGames) data.get("dbV");
        int rangeBegin = data.getInt("rangeBegin");
        int rangeEnd = data.getInt("rangeEnd");
        File parentDirectory = (File) data.get("parentDirectory");
        String nameOg = (String) data.get("nameOg");
        String nameOgWasted = (String) data.get("nameOgWasted");
        Parser parser = new Parser(baseUrl, dbV);
        parser.parse(rangeBegin, rangeEnd, parentDirectory, nameOg, nameOgWasted);
        LOG.info(new StringBuilder("Parser: finish at ")
                .append(new GregorianCalendar(TimeZone.getTimeZone("GMT+3:00")).getTime())
                .append('.'));
    }
}
