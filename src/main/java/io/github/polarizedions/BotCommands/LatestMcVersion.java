package io.github.polarizedions.BotCommands;

import io.github.polarizedions.IrcParser.ParsedMessages.Command;
import io.github.polarizedions.Logger;
import io.github.polarizedions.Utils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
public class LatestMcVersion implements IBotCommandHandler {
    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final Logger logger = Logger.getLogger("LatestMcVersionCommand");

    private static JSONObject versionManifestCache;
    private static long cacheTime = 0;
    private static final int CACHE_TIMEOUT = 60; // Seconds

    @Override
    public void handle(Command command) {
        switch (command.args.length > 0 ? command.args[0] : "release") {
            case "release":
                lookupLatest("release", resultObj -> {
                    if (resultObj == null) {
                        command.reply("%s: Error looking up latest release", command.from.nick);
                        return;
                    }
                    String result = (String)resultObj[0];
                    command.reply("%s: Latest release is %s", command.from.nick, result);
                });

                break;
            case "snapshot":
                lookupLatest("snapshot", resultObj -> {
                    if (resultObj == null) {
                        command.reply("%s: Error looking up latest snapshot", command.from.nick);
                        return;
                    }
                    String result = (String)resultObj[0];
                    command.reply("%s: Latest snapshot is %s", command.from.nick, result);
                });

                break;
            default:
                command.reply("%s: Unknown release type '%s'. Valid options: 'release' (default), 'snapshot'", command.from.nick, String.join(" ", command.args));
        }
    }

    private JSONObject getVersionManifest() {
        try {
            logger.debug("Current time: %s%n expire time: %s, %s", System.currentTimeMillis(), cacheTime + (CACHE_TIMEOUT * 1000), (cacheTime + (CACHE_TIMEOUT * 1000)) > System.currentTimeMillis());
            if (versionManifestCache != null && (cacheTime + (CACHE_TIMEOUT * 1000)) > System.currentTimeMillis()) {
                return versionManifestCache;
            }
            JSONObject versionManifest = (JSONObject) JSONValue.parseWithException(IOUtils.toString(new URL(VERSION_MANIFEST_URL).openStream()));
            cacheTime = System.currentTimeMillis();
            versionManifestCache = versionManifest;
            return versionManifest;
        } catch (ParseException | IOException e) {
            logger.error("Error looking up the version manifest!");
            logger.error(e);
            return null;
        }
    }

    private void lookupLatest(String releaseType, Utils.Callback cb) {
        JSONObject versionManifest = getVersionManifest();
        if (versionManifest == null) {
            cb.reply(null);
            return;
        }

        JSONObject latestVersions = (JSONObject)versionManifest.get("latest");
        if (latestVersions == null) {
            cb.reply(null);
            return;
        }

        cb.reply(latestVersions.get(releaseType));
    }

    @Override
    public String getCommand() {
        return "mc";
    }
}
