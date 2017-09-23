package io.github.stepie22;

import io.github.stepie22.config.Config;
import io.github.stepie22.config.ConfigHandler;
import io.github.stepie22.config.NetworkConfig;
import io.github.stepie22.networking.NetworkManager;

import java.io.IOException;

public class IRCBot {
    public static final IRCBot bot = new IRCBot();
    public final NetworkManager networkManager = NetworkManager.instance;
    public final Logger logger = Logger.getLogger("Bot");
    public final Config config = ConfigHandler.getConfig();

    private IRCBot() {
        logger.info("Starting irc bot.");

        for (Object nwConfig : config.networkConfigs) {
            networkManager.addNetwork((NetworkConfig)nwConfig);
        }
    }

    public void addNetwork(NetworkConfig nwConfig) {
        config.networkConfigs.add(nwConfig);
        networkManager.addNetwork(nwConfig);
    }
}
