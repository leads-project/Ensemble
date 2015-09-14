package org.infinispan.ensemble.rest;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Marcelo Pasin (pasin)
 */

public class PropertyLoader {

    public static final String DEFAULT_CONFIG_PROPERTIES = "config.properties";

    // add all default properties here
    public static final String DEFAULT_PROPERTIES =
            "\n";

    private static Set<String> loadedFiles = null;

    /**
     *
     * Loads default properties, from DEFAULT_PROPERTIES string.
     * Loaded properties _do_not_override_ the existing ones
     *
     * @param prop: Properties to fill with default values
     */

    private static void loadDefault(Properties prop) {
        if (hasLoaded("/..DEFAULT" + prop.hashCode()))
            return;
        Properties loadedProps = new Properties();
        ByteArrayInputStream initial = new ByteArrayInputStream(DEFAULT_PROPERTIES.getBytes());
        try {
            loadedProps.load(initial);
            loadedProps.putAll(prop);
            prop.putAll(loadedProps);
//            prop.storeToXML(System.out, "text");
        } catch (IOException e) {}
    }


    /**
     *
     * Loads a file into existing properties.
     * Loaded properties _do_not_override_ the existing ones
     * First, look for fileName in the classpath, then in the current directory.
     *
     * The property priority is:
     * 1) Properties passed in the command-line (-Dproperty=value)
     * 2) Default Java properties
     * 3) Default properties listed in DEFAULT_PROPERTIES
     * 4) Properties loaded with load()
     *
     * @param prop: Properties to fill with loaded values
     * @param fileName: File to load.
     * @return the -1 if the input file is corrupted, 0 if file was not found, 1 if file was loaded, 2 if it was already loaded before.
     */

    public static int load(Properties prop, String fileName) {

        loadDefault(prop);
        if (hasLoaded(fileName + prop.hashCode()))
            return 2;

        int ret = 0;
        Properties loadedProps = new Properties();

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            try { is = new FileInputStream(fileName); }
            catch (FileNotFoundException e) { }
        }
        if (is != null) {
            try {
                loadedProps.load(is);
                ret = 1;
            } catch (IOException e) {
                e.printStackTrace();
                ret = -1;
            }
        }
        // overrides the loaded properties with the ones previously set (prop argument)
        loadedProps.putAll(prop);
        prop.putAll(loadedProps);
        return ret;
    }

    private static boolean hasLoaded(String fileName) {
        if (loadedFiles == null)
            loadedFiles = new HashSet<String>();

        if (loadedFiles.contains(fileName))
            return true;
        loadedFiles.add(fileName);
        return false;
    }

    public static int load(String fileName) {
        return load(System.getProperties(), fileName);
    }

    public static void load(Properties prop) {
        load(prop, DEFAULT_CONFIG_PROPERTIES);
    }

    public static void load() {
        load(System.getProperties(), DEFAULT_CONFIG_PROPERTIES);
    }
}
