package my.projects.learning.jpa.util.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class CustomJavaLogger {
    private static final Logger MAIN_LOGGER = Logger.getLogger(CustomJavaLogger.class.getName());

    private CustomJavaLogger() {
    }

    public static void readLoggerConfigurationPropertiesFromResource(final String resourceName) {
        final LogManager logManager = LogManager.getLogManager();

        final ClassLoader classLoader = CustomConsoleHandler.class.getClassLoader();
        try (final InputStream loggerProperties = classLoader.getResourceAsStream(resourceName)) {
            logManager.readConfiguration(loggerProperties);
        } catch (final IOException e) {
            final String message = e.getMessage();
            MAIN_LOGGER.warning(message);
        }
    }
}
