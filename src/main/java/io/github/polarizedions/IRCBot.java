package io.github.polarizedions;

import io.github.polarizedions.config.Config;
import io.github.polarizedions.config.ConfigHandler;
import io.github.polarizedions.config.NetworkConfig;
import io.github.polarizedions.networking.NetworkManager;

/**
 * Copyright 2017 PolarizedIons
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
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
