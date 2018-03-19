package com.ems.it;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

final class ConnectionManager {

    private static final Properties properties = getProperties();

    private static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
    private static final String CONNECTION_URI = "jdbc:oracle:thin:@" +
            properties.getProperty("ems.database.host") + ":" +
            properties.getProperty("ems.database.port") + ":" +
            properties.getProperty("ems.database.sid");

    private static final String DATABASE_USERNAME = properties.getProperty("ems.database.username");
    private static final String DATABASE_PASSWORD = properties.getProperty("ems.database.password");

    private ConnectionManager() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    static Optional<Connection> getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER_NAME);
            connection = DriverManager.getConnection(CONNECTION_URI, DATABASE_USERNAME, DATABASE_PASSWORD);

        } catch (final ClassNotFoundException driverNotFound) {
            System.err.println("Oracle driver not found in classpath");
            return Optional.empty();
        } catch (final SQLException badConnectionAttempt) {
            System.err.println("Failed to create the database connection");
            return Optional.empty();
        }

        return Optional.of(connection);
    }

    private static Properties getProperties() {
        final Properties properties = new Properties();

        try {
            properties.load(ConnectionManager.class.getResourceAsStream("/application.properties"));
        } catch (final IOException couldNotLoadProperties) {
            System.err.println("Could not load properties from file");
            return null;
        }

        return properties;
    }

}
