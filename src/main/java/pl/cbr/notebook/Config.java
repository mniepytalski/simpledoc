package pl.cbr.notebook;

import java.io.*;
import java.util.Properties;

public class Config {

    private static final String FILE_NAME = "config.properties";
    private static final Properties props = new Properties();

    // Wczytanie przy starcie
    static {
        load();
    }

    public static void load() {
        File file = new File(FILE_NAME);

        try {
            if (file.exists()) {
                try (FileInputStream in = new FileInputStream(file)) {
                    props.load(in);
                }
            } else {
                // Domyślne wartości
                setDefaults();
                save(); // zapis domyślnego pliku
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd wczytywania konfiguracji", e);
        }
    }

    private static void setDefaults() {
        props.setProperty("window.title", "Sklep Swing");
        props.setProperty("window.width", "800");
        props.setProperty("window.height", "600");
        props.setProperty("window.resizable", "true");

        props.setProperty("store.name", "Domyślny Sklep");
        props.setProperty("store.tax", "0.23");
    }

    // ===== GETTERY =====

    public static String getString(String key) {
        return props.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }

    public static double getDouble(String key) {
        return Double.parseDouble(props.getProperty(key));
    }

    // ===== SETTERY =====

    public static void set(String key, String value) {
        props.setProperty(key, value);
    }

    public static void set(String key, int value) {
        props.setProperty(key, String.valueOf(value));
    }

    public static void set(String key, boolean value) {
        props.setProperty(key, String.valueOf(value));
    }

    public static void set(String key, double value) {
        props.setProperty(key, String.valueOf(value));
    }

    // ===== ZAPIS =====

    public static void save() {
        try (FileOutputStream out = new FileOutputStream(FILE_NAME)) {
            props.store(out, "Konfiguracja aplikacji Swing");
        } catch (IOException e) {
            throw new RuntimeException("Błąd zapisu konfiguracji", e);
        }
    }
}
