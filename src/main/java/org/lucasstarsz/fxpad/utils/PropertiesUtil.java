package org.lucasstarsz.fxpad.utils;

import java.io.*;
import java.util.Properties;

import static org.lucasstarsz.fxpad.RTFXMain.lightStylePath;
import static org.lucasstarsz.fxpad.RTFXMain.userPropertiesPath;

public class PropertiesUtil {

    public static Properties get(String location) throws IOException {
        Properties result = null;

        try (FileInputStream input = new FileInputStream(location)) {
            result = new Properties();
            result.load(input);
        } catch (FileNotFoundException e) {
            if (location.equals(userPropertiesPath)) {
                File prefsFile = new File(location);
                File prefsDir = new File(prefsFile.getAbsoluteFile().getParent());

                if (prefsDir.mkdirs() || prefsFile.createNewFile()) {
                    FileWriter fw = new FileWriter(prefsFile);
                    fw.write("theme=" + lightStylePath);
                    fw.close();
                }

                return get(location);
            } else {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void set(String userPropertiesPath, String key, String value) throws IOException {
        Properties properties = get(userPropertiesPath);
        try (FileOutputStream output = new FileOutputStream(userPropertiesPath)) {
            properties.setProperty(key, value);
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
