package my.projects.learning.jpa.util.logging;

import my.projects.learning.jpa.util.text.AnsiColor;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static my.projects.learning.jpa.util.text.AnsiColor.*;

public class CustomConsoleHandler extends ConsoleHandler {
    private final StreamHandler stdOutHandler = new StreamHandler(System.out, new SimpleFormatter());
    private final StreamHandler stdErrHandler = new StreamHandler(System.err, new SimpleFormatter());

    public CustomConsoleHandler() {
        final LogManager logManager = LogManager.getLogManager();
        setLevelFromProperties(logManager);
        setFormatterFromProperties(logManager);
    }

    @Override
    public synchronized void publish(final LogRecord record) {
        final LogRecord colorized = colorizeHibernateSQLRecord(record);

        if (record.getLevel().intValue() <= Level.INFO.intValue()) {
            stdOutHandler.publish(colorized);
            stdOutHandler.flush();
        } else {
            stdErrHandler.publish(record);
            stdErrHandler.flush();
        }
    }

    private LogRecord colorizeHibernateSQLRecord(final LogRecord record) {
        final String loggerName = record.getLoggerName();

        if (Objects.equals("org.hibernate.SQL", loggerName)) {
            colorizeString(record, PURPLE);
        }

        if (Objects.equals("org.hibernate.type.descriptor.sql.BasicBinder", loggerName)) {
            colorizeString(record, BLUE);
        }

        if (Objects.equals("org.hibernate.type.descriptor.sql.BasicExtractor", loggerName)) {
            colorizeString(record, CYAN);
        }

        return record;
    }

    private void colorizeString(final LogRecord record, final String color) {
        final String message = record.getMessage();

        final String colorizedMessage = Stream.of(message.split("\\n"))
                .map(line -> color + line + RESET)
                .collect(Collectors.joining(System.lineSeparator()));

        record.setMessage(colorizedMessage);
    }

    private void setFormatterFromProperties(final LogManager logManager) {
        final String className = getClass().getName();
        final String formatterClassStr = logManager.getProperty(className + ".formatter");
        try {
            final Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(formatterClassStr);
            final Formatter formatter = (Formatter) clz.getConstructor().newInstance();
            stdOutHandler.setFormatter(formatter);
        } catch (final ClassNotFoundException | IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            final String message = e.getMessage();
            System.err.println(message);
        }
    }

    private void setLevelFromProperties(final LogManager logManager) {
        final String className = getClass().getName();
        final String levelStr = logManager.getProperty(className + ".level");
        final Level level = Level.parse(levelStr);
        stdOutHandler.setLevel(level);
    }
}
