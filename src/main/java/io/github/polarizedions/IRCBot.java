package io.github.polarizedions;

import io.github.polarizedions.config.Config;
import io.github.polarizedions.config.ConfigHandler;
import io.github.polarizedions.config.NetworkConfig;
import io.github.polarizedions.networking.NetworkManager;

public class IRCBot {
    public static final IRCBot bot = new IRCBot();
    public final NetworkManager networkManager = NetworkManager.instance;
    public final Logger logger = Logger.getLogger("Bot");
    public final Config config = ConfigHandler.getConfig();

    private IRCBot() {
        logger.info("Starting irc bot.");

        for (Object nwConfig : config.networkConfigs) {
            networkManager.addNetwork((NetworkConfig) nwConfig);
        }
    }

    public void addNetwork(NetworkConfig nwConfig) {
        config.networkConfigs.add(nwConfig);
        networkManager.addNetwork(nwConfig);
    }

    public void start() {
        networkManager.connectAll();
    }
}
