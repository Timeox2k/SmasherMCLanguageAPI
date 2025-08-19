package me.timeox2k.smasherMCLanguageAPI.velocity;

import com.google.inject.Inject;
import com.mysql.cj.jdbc.Driver;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.timeox2k.smasherMCLanguageAPI.velocity.manager.VelocityDatabaseManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;

@Plugin(id = "smashermclanguageapi", name = "SmasherMCLanguageAPI", version = "0.1.0-SNAPSHOT", description = "LanguageAPI of SmasherMC.net", authors = {"Timeox2k"})
public class SmasherMCLanguageVelocityAPI {

    private final ProxyServer server;
    private final Logger logger;
    private VelocityDatabaseManager databaseManager;
    private YamlDocument config;
    private static SmasherMCLanguageVelocityAPI instance;

    @Inject
    public SmasherMCLanguageVelocityAPI(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        instance = this;

        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance());
        } catch (ReflectiveOperationException | SQLException e) {
           e.printStackTrace();
        }

        try {
            Files.createDirectories(dataDirectory);

            File file = new File(dataDirectory.toFile(), "config.yml");
            InputStream defaults = SmasherMCLanguageVelocityAPI.class.getResourceAsStream("/config.yml");

            if (defaults != null) {
                config = YamlDocument.create(
                        file,
                        defaults,
                        GeneralSettings.DEFAULT,
                        LoaderSettings.builder().setAutoUpdate(true).build(),
                        DumperSettings.DEFAULT,
                        UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build()
                );
                config.update();
            } else {
                config = YamlDocument.create(file);
            }
            config.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @Subscribe
    public void on(ProxyInitializeEvent event) {
        this.databaseManager = new VelocityDatabaseManager();
    }

    public VelocityDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public static SmasherMCLanguageVelocityAPI getInstance() {
        return instance;
    }

    public YamlDocument getConfig() {
        return config;
    }
}
