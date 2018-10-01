package ru.bigcheese.flscanner.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.bigcheese.flscanner.model.AppProps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Settings {

    private static final Settings instance = new Settings();
    private static final ObjectMapper mapper = new ObjectMapper();

    private List<SiteConfig> configs = new ArrayList<>();
    private AppProps appProps = new AppProps();

    static {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    private Settings() {
    }

    public static Settings getInstance() {
        return instance;
    }

    public synchronized void initSettings() {
        readSiteConfigs("sites.json");
        readAppProps("settings.json");
    }

    public synchronized void saveSettings(AppProps props) {
        appProps = new AppProps(props);
        try {
            mapper.writeValue(new File("settings.json"), appProps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AppProps getAppProps() {
        return new AppProps(appProps);
    }

    public List<SiteConfig> getSiteConfigs() {
        final Set<String> ignored = appProps.getIgnored();
        return Collections.unmodifiableList(configs
                .stream()
                .filter(e -> !ignored.contains(e.getName()))
                .collect(Collectors.toList()));
    }

    public List<SiteConfig> getAllSiteConfigs() {
        return Collections.unmodifiableList(configs);
    }

    private void readSiteConfigs(String fileName) {
        Path sitesPath = Paths.get(fileName);
        if (Files.exists(sitesPath)) {
            try {
                byte[] jsonData = Files.readAllBytes(sitesPath);
                configs = mapper.readValue(jsonData, new TypeReference<List<SiteConfig>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readAppProps(String fileName) {
        Path settingsPath = Paths.get(fileName);
        try {
            if (Files.exists(settingsPath)) {
                byte[] jsonData = Files.readAllBytes(settingsPath);
                appProps = mapper.readValue(jsonData, AppProps.class);
            } else {
                mapper.writeValue(new File(fileName), appProps);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
