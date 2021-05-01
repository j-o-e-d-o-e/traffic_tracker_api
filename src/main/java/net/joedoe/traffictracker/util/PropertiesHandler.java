package net.joedoe.traffictracker.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {

    public static Properties getProperties(String path) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(path));
        return prop;
    }
}
