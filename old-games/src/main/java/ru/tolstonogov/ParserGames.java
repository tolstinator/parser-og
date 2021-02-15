package ru.tolstonogov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class ParserGames {
    private static final Logger LOG = LogManager.getLogger(ParserGames.class.getName());
    private static final String KEY_LOAD = "l=";
    private static final String KEY_RANGE_BEGIN = "rb=";
    private static final String KEY_RANGE_END = "re=";
    private static final String DEFAULT_RANGE_BEGIN = "0";
    private static final String DEFAULT_RANGE_END = "10900";
    private static final String BASE_URL = "https://www.old-games.ru/";
    private static final String NAME_OG = "Old-Games.RU";
    private static final String NAME_OG_WASTED = "Old-Games.RU_wasted";
    private static final String NAME_FILE_PROPERTIES = "app.properties";
    private static final String NAME_FILE_WASTED = "wasted_games";
    private static final String NAME_FILE_SAVED = "saved_games";
    private static final String NAME_FILE_DOCUMENTED = "documented_games";

    private static String getCron(String prop) {
        String result = null;
        try (InputStream in = ParserGames.class.getClassLoader().getResourceAsStream(prop)) {
            Properties config = new Properties();
            if (in != null) {
                config.load(in);
            }
            result = config.getProperty("cron.time");
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return result;
    }

    private static void putJobData(JobDetail job, String[] args, DataBaseGames dbV) {
//        TODO: check: args[].
//        TODO: args[0] contains only name of properties file, and when jar is running,
//         the program finds properties file with name in args[0] inside jar, but not in working directory.
//        TODO: add link to same games.
        job.getJobDataMap().put("dbV", dbV);
        job.getJobDataMap().put("baseUrl", BASE_URL);
        job.getJobDataMap().put("nameOg", NAME_OG);
        job.getJobDataMap().put("nameOgWasted", NAME_OG_WASTED);
        String rangeBegin = DEFAULT_RANGE_BEGIN;
        String rangeEnd = DEFAULT_RANGE_END;
        File parentDirectory = null;
        for (String arg : args) {
            if (arg.startsWith(KEY_RANGE_BEGIN)) {
                rangeBegin = arg.substring(KEY_RANGE_BEGIN.length());
            } else if (arg.startsWith(KEY_RANGE_END)) {
                rangeEnd = arg.substring(KEY_RANGE_END.length());
            } else if (arg.startsWith(KEY_LOAD)) {
                parentDirectory = new File(arg.substring(KEY_LOAD.length()));
                try {
                    if (!parentDirectory.isDirectory()) {
                        LOG.error(new StringBuilder("Directory ").append(parentDirectory.getCanonicalPath()).append(" is not directory."));
                    }
                    if (!parentDirectory.exists()) {
                        LOG.error(new StringBuilder("Directory ").append(parentDirectory.getCanonicalPath()).append(" is not exist."));
                    }
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
        }
        job.getJobDataMap().put("rangeBegin", rangeBegin);
        job.getJobDataMap().put("rangeEnd", rangeEnd);
        job.getJobDataMap().put("parentDirectory", parentDirectory);
    }

    public static void main(String[] args) throws SchedulerException {
        //TODO: add sessions.
        Scheduler sched = new StdSchedulerFactory().getScheduler();
        sched.start();
        JobDetail job = newJob(JobParserGames.class)
                .withIdentity("jobParserGames")
                .build();
        DataBaseGames dbV = new DataBaseGames(NAME_FILE_PROPERTIES, NAME_FILE_WASTED, NAME_FILE_SAVED, NAME_FILE_DOCUMENTED);
        putJobData(job, args, dbV);
        Trigger trigger = newTrigger()
                .withIdentity("triggerParserGames")
                .withSchedule(simpleSchedule().withIntervalInSeconds(300))
                .build();
//        Trigger trigger = newTrigger()
//                .withIdentity("triggerParserGames")
//                .withSchedule(cronSchedule(getCron(args[0])))
//                .build();
        sched.scheduleJob(job, trigger);
    }
}
