package io.github.polarizedions;

import io.github.polarizedions.config.Config;
import io.github.polarizedions.config.ConfigHandler;
import io.github.polarizedions.config.NetworkConfig;
import io.github.polarizedions.networking.NetworkManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public static final Path STORAGE_DIR = Paths.get(".", "storage");

    public static final IRCBot bot = new IRCBot();
    public final NetworkManager networkManager;
    public final Logger logger;
    public final Config config;

    private IRCBot() {
        logger = Logger.getLogger("Bot");
        logger.info("Starting irc bot.");
        System.out.println(STORAGE_DIR);
        if (!Files.exists(STORAGE_DIR)) {
            try {
                Files.createDirectory(STORAGE_DIR);
            } catch (IOException e) {
                logger.error("Error creating storage directory! Your config will *not* be able to be saved!");
                e.printStackTrace();
            }
        }

        networkManager = NetworkManager.instance;
        config = ConfigHandler.getConfig();

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

    public void shutdown() {
        networkManager.shutdown();
    }
}
