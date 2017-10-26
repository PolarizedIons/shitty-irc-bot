package io.github.polarizedions.Utils;

import io.github.polarizedions.Logger;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
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
public class McVersions {
    private static final Logger logger = Logger.getLogger("McVersions");
    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final int CACHE_TIMEOUT = 60; // Seconds
    private static JSONObject versionManifestCache;
    private static long cacheTime = 0;

    public static JSONObject getVersionManifest() {
        try {
            if (versionManifestCache != null && (cacheTime + (CACHE_TIMEOUT * 1000)) > System.currentTimeMillis()) {
                return versionManifestCache;
            }
            logger.debug("Fetching latest mc versions from Mojang");

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

    public static void lookupLatest(String releaseType, Callback cb) {
        JSONObject versionManifest = getVersionManifest();
        if (versionManifest == null) {
            cb.reply(null);
            return;
        }

        JSONObject latestVersions = (JSONObject) versionManifest.get("latest");
        if (latestVersions == null) {
            cb.reply(null);
            return;
        }

        logger.debug("Latest " + releaseType + " is " + latestVersions.get(releaseType));

        cb.reply(latestVersions.get(releaseType));
    }
}
