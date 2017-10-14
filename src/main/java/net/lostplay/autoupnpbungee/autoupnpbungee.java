package net.lostplay.autoupnpbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class autoupnpbungee
        extends Plugin
{
    public void onEnable()
    {
        String protocol;
        String name;
        String ip;
        int port;
        Configuration config = null;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assert config != null;
        protocol = config.getString("settings.protocol");
        name = config.getString("settings.name");
        ip = config.getString("settings.ipaddress");
        port = config.getInt("settings.port");
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        openPort(ip, port, name, protocol);
    }

    private void openPort(String ip, int port, String name, String protocol)
    {
        getLogger().info("[AutoUpnp] Attempting to forward port.");
        PortMapping mapping;
        UpnpService upnpService;
        if (protocol.equals("TCP")) {
            mapping = new PortMapping(port, ip, PortMapping.Protocol.TCP, name);

        } else if (protocol.equals("UDP")) {
            mapping = new PortMapping(port, ip, PortMapping.Protocol.UDP, name);

        } else {
            mapping = new PortMapping(port, ip, PortMapping.Protocol.TCP, name);

        }
        upnpService = new UpnpServiceImpl(new PortMappingListener(mapping));
        upnpService.getControlPoint().search();
    }


    public void onDisable()
    {
        getLogger().info("[AutoUpnp] Removing port mapping.");
        new UpnpService.Shutdown();
    }

}
