package io.github.polarizedions;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Copyright 2017 PolarizedIons
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
public class Logger {
    private static String FORMAT = "%s [%s] [%s] [%s] %s";
    private static int level = LOG_LEVEL.INFO;
    private static HashMap<String, Logger> loggerMap = new HashMap<>();

    private String name;

    private Logger(String name) {
        this.name = name;
    }

    public static Logger getLogger(String name) {
        loggerMap.putIfAbsent(name, new Logger(name));
        return loggerMap.get(name);
    }

    public static int getLogLevel() {
        return Logger.level;
    }

    public static void setLogLevel(int level) {
        Logger.level = level;
    }

    private String getDayTime() {
        Calendar now = Calendar.getInstance();
        return String.format("%02d/%02d/%02d %02d:%02d:%02d",
                now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.MONTH) + 1,
                now.get(Calendar.YEAR),
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND)
        );
    }

    public void info(Object... msgs) {
        if (Logger.level < LOG_LEVEL.INFO) {
            return;
        }

        for (Object o : msgs) {
            System.out.println(String.format(FORMAT, getDayTime(), "INFO", Thread.currentThread().getName(), name, o));
        }
    }

    public void debug(Object... msgs) {
        if (Logger.level < LOG_LEVEL.DEBUG) {
            return;
        }

        for (Object o : msgs) {
            System.out.println(String.format(FORMAT, getDayTime(), "DEBUG", Thread.currentThread().getName(), name, o));
        }
    }

    public void error(Object... msgs) {
        if (Logger.level < LOG_LEVEL.ERROR) {
            return;
        }

        for (Object o : msgs) {
            System.err.println(String.format(FORMAT, getDayTime(), "ERROR", Thread.currentThread().getName(), name, o));
        }
    }

    public static class LOG_LEVEL {
        public static int ERROR = 0,
                INFO = 1,
                DEBUG = 2;
    }
}
