package io.github.polarizedions.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import io.github.polarizedions.Logger;

import java.io.*;
import java.util.ArrayList;

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
public class ConfigHandler {
    private static Config config;
    private static boolean isDefault = false;
    private static Logger logger = Logger.getLogger("ConfigHandler");

    public static Config loadConfig() {
        File f = new File("botconfig.yaml");

        if (!f.exists()) {
            setDefaultConfig();
            save();

            return getConfig();
        }
        isDefault = false;

        try {
            YamlReader yamlReader = new YamlReader(new FileReader(f));
            config = yamlReader.read(Config.class);
            yamlReader.close();
        } catch (FileNotFoundException e) {
            logger.error("Error while trying to read config file - it's disappeared!");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Error while reading/parsing config file!");
            e.printStackTrace();
        }

        return getConfig();
    }

    public static Config getConfig() {
        if (config == null) {
            loadConfig();
        }

        return config;
    }

    public static boolean isDefault() {
        return isDefault;
    }

    private static void setDefaultConfig() {
        isDefault = true;
        config = new Config();

        config.networkConfigs = new ArrayList();
        NetworkConfig freenode = new NetworkConfig();
        freenode.host = "irc.freenode.net";
        freenode.port = 6667;
        freenode.nick = "PolarizedBot";
        freenode.serverPass = "";
        freenode.gecos = "PolarizedBot";
        freenode.realname = "PolarizedIons's bot";
        freenode.autojoinChannels = "##PolarizedSpam";
        config.networkConfigs.add(freenode);

        config.logLevel = Logger.LOG_LEVEL.INFO;
        config.loginUser = "";
        config.loginPass = "";
        config.nickServAuth = "false";
        config.SASLAuth = "false";
        config.botPrefix = "!";

        logger.debug("Default config set!");
    }

    public static void save() {
        try {
            YamlWriter yamlWriter = new YamlWriter(new FileWriter(new File("botconfig.yaml")));
            yamlWriter.write(config);
            yamlWriter.close();
        } catch (IOException e) {
            logger.error("Error whilst writing the config file!");
            e.printStackTrace();
        }
    }
}
