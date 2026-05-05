package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    Properties prop;

    public ConfigReader() {
        try {
            FileInputStream fis = new FileInputStream(
                System.getProperty("user.dir") + "/src/main/resources/config.properties"
            );
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBrowser() {
        return prop.getProperty("browser");
    }

    public String getUrl() {
        return prop.getProperty("url");
    }

    public String getOS() {
        return prop.getProperty("os");
    }
}