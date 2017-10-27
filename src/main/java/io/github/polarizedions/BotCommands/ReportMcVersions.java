package io.github.polarizedions.BotCommands;

import io.github.polarizedions.IRCBot;
import io.github.polarizedions.IrcParser.ParsedMessages.Command;
import io.github.polarizedions.Logger;
import io.github.polarizedions.Utils.McVersions;
import io.github.polarizedions.Utils.PersistentFile;
import io.github.polarizedions.config.ConfigHandler;
import io.github.polarizedions.networking.Network;
import org.json.simple.JSONObject;

import java.util.*;

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
public class ReportMcVersions implements IBotCommandHandler {
    private static final int CHECK_INTERVAL = 120; // seconds
    private static final Logger logger = Logger.getLogger("ReportMcVersions");
    private final String USAGE = String.format("Usage: %s <add/remove/list>", ConfigHandler.getConfig().botPrefix + getCommand());

    private static JSONObject latestMcVersions;
    private static PersistentFile persistantFile = new PersistentFile("reportmcversions");
    private Timer timer;

    public ReportMcVersions() {
        logger.debug("Staring timer");

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new CheckForChanges(), 0L, CHECK_INTERVAL * 1000L);
    }

    private void addChannel(Network network, String channel) {
        String nwName = network.getName();
        HashMap<String, ArrayList<String>> announceChannels = getAnnounceChannels();
        announceChannels.putIfAbsent(nwName, new ArrayList<>());
        ArrayList<String> channels = announceChannels.get(nwName);
        if (!announceChannels.get(nwName).contains(channel)) {
            channels.add(channel);
        }
        announceChannels.put(nwName, channels);

        System.out.println("new is" + Arrays.toString(announceChannels.get(nwName).toArray()));

        persistantFile.put("reportchannels", announceChannels);
        persistantFile.save();
    }

    private void removeChannel(Network network, String channel) {
        String nwName = network.getName();
        HashMap<String, ArrayList<String>> announceChannels = getAnnounceChannels();
        ArrayList<String> channels = announceChannels.get(nwName);
        if (channels != null) {
            channels.remove(channel);

            if (channels.size() == 0) {
                announceChannels.remove(nwName);
            }
            else {
                announceChannels.put(nwName, channels);
            }

            persistantFile.put("reportchannels", announceChannels);
            persistantFile.save();
        }
    }

    private HashMap<String, ArrayList<String>> getAnnounceChannels() {
        Object announceChannels = persistantFile.get("reportchannels");
        if (announceChannels == null) {
            announceChannels = new HashMap<String, ArrayList<String>>();
            persistantFile.put("reportchannels", announceChannels);
            persistantFile.save();
        }

        return (HashMap<String, ArrayList<String>>) announceChannels;
    }

    @Override
    public void handle(Command command) {
        if (command.args.length != 1) {
            command.reply("%s: %s", command.from.nick, USAGE);
            return;
        }

        switch (command.args[0]) {
            case "list":
                HashMap<String, ArrayList<String>> announceChannels = getAnnounceChannels();
                if (announceChannels.size() == 0) {
                    command.reply("%s: I do not announce to any channels!", command.from.nick);
                    return;
                }

                command.reply("%s: I announce to the following channels:", command.from.nick);
                announceChannels.forEach((networkName, channelArr) -> {
                    String channels = String.join(", ", channelArr);
                    String network = IRCBot.bot.networkManager.getNetwork(networkName).toString();
                    command.reply("%s: %s", network, channels);
                });
                break;

            case "add":
                if (!command.toChannel) {
                    command.reply("You must use this command in a channel!");
                    return;
                }

                addChannel(command.originNetwork, command.to);
                command.reply("Added this channel to the announcements list for new Minecraft versions");
                break;

            case "remove":
                if (!command.toChannel) {
                    command.reply("You must use this command in a channel!");
                    return;
                }

                removeChannel(command.originNetwork, command.to);

                command.reply("Removed this channel from the announcements list for new Minecraft versions");
                break;

            default:
                command.reply("%s: Unknown command '%s', %s", command.from.nick, command.args[0], USAGE);
        }
    }

    @Override
    public String getCommand() {
        return "announcemc";
    }

    class CheckForChanges extends TimerTask {

        @Override
        public void run() {
            logger.debug("Checking for new versions");
            JSONObject versionManifest = McVersions.getVersionManifest();

            if (versionManifest == null) {
                return;
            }

            JSONObject latest = (JSONObject) versionManifest.get("latest");

            if (latest == null) {
                return;
            }

            if (latestMcVersions == null) {
                latestMcVersions = latest;
                return;
            }

            JSONObject prevLatestMcVersions = latestMcVersions;
            latestMcVersions = latest;

            for (Object key : latest.keySet()) {
                if (!prevLatestMcVersions.get(key).equals(latestMcVersions.get(key))) {
                    logger.debug("New version found for " + key + " => " + latestMcVersions.get(key) + ", was " + prevLatestMcVersions.get(key));

                    getAnnounceChannels().forEach((networkName, channelArr) -> {
                        Network network = IRCBot.bot.networkManager.getNetwork(networkName);
                        channelArr.forEach(channel -> network.send("PRIVMSG " + channel + " :[Minecraft Version] New " + key + ": " + latestMcVersions.get(key)));
                    });
                }
            }
        }
    }
}
