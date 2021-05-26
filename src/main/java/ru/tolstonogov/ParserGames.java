package ru.tolstonogov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
    private static final String NAME_FILE_PROPERTIES = "src/main/resources/app.properties";
    private static final String NAME_FILE_WASTED = "src/main/resources/wasted_games";
    private static final String NAME_FILE_SAVED = "src/main/resources/saved_games";
    private static final String NAME_FILE_DOCUMENTED = "src/main/resources/documented_games";

    private void execute(DataBaseGames dbV,
                         String rangeBegin,
                         String rangeEnd,
                         File parentDirectory) {
        LOG.info(new StringBuilder("Parser: start at ")
                .append(new GregorianCalendar(TimeZone.getTimeZone("GMT+3:00")).getTime())
                .append('.'));
        Parser parser = new Parser(BASE_URL, dbV);
        parser.parse(Integer.parseInt(rangeBegin), Integer.parseInt(rangeEnd), parentDirectory, NAME_OG, NAME_OG_WASTED);
        LOG.info(new StringBuilder("Parser: finish at ")
                .append(new GregorianCalendar(TimeZone.getTimeZone("GMT+3:00")).getTime())
                .append('.'));
    }

    public static void main(String[] args) {
        //TODO: add sessions.
//        TODO: check: args[].
//        TODO: args[0] contains only name of properties file, and when jar is running,
//         the program finds properties file with name in args[0] inside jar, but not in working directory.
//        TODO: add link to same games.
        DataBaseGames dbV = new DataBaseGames(NAME_FILE_PROPERTIES, NAME_FILE_WASTED, NAME_FILE_SAVED, NAME_FILE_DOCUMENTED);
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
        new ParserGames().execute(dbV,
                rangeBegin,
                rangeEnd,
                parentDirectory);
    }
}
