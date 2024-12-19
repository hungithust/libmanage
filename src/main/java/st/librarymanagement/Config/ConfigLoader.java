package st.librarymanagement.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties config = new Properties();
    private static final Properties dbConfig = new Properties();

    static {
        try {
            // Load config.properties
            FileInputStream configFile = new FileInputStream("resources/config.properties");
            config.load(configFile);

            // Load db.properties
            FileInputStream dbConfigFile = new FileInputStream("resources/database.properties");
            dbConfig.load(dbConfigFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters for config properties
    public static String getAppName() {
        return config.getProperty("app.name");
    }

    public static int getWindowWidth() {
        return Integer.parseInt(config.getProperty("window.width"));
    }

    public static int getWindowHeight() {
        return Integer.parseInt(config.getProperty("window.height"));
    }

    public static int getMaxLoanDays() {
        return Integer.parseInt(config.getProperty("book.max_loan_days"));
    }

    // Getters for database properties
    public static String getDbDriver(){
        return  dbConfig.getProperty("db.driver");
    }

    public static String getDbUrl() {
        return dbConfig.getProperty("db.url");
    }

    public static String getDbUsername() {
        return dbConfig.getProperty("db.username");
    }

    public static String getDbPassword() {
        return dbConfig.getProperty("db.password");
    }
}