package io.github.polarizedions.IrcParser;

import io.github.polarizedions.networking.Network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

/**
 * NOTE: Parts of this file is derived from the irc-parser used by irc-framework (https://github.com/kiwiirc/irc-framework)
 * Licence: MIT - https://github.com/kiwiirc/irc-framework/blob/cfcc0f9049120e22b1033346f0b197afdc3c18d1/LICENSE.txt
 */
public class IrcParser {
    private static final Pattern PARSE_REGEX = Pattern.compile("^(?:@([^ ]+) )?(?::((?:(?:([^\\s!@]+)(?:!([^\\s@]+))?)@)?(\\S+)) )?((?:[a-zA-Z]+)|(?:[0-9]{3}))(?: ([^:].*?))?(?: :(.*))?$", Pattern.CASE_INSENSITIVE);
    private Network network;

    public IrcParser(Network network) {
        this.network = network;
    }

    public ParsedLine parseLine(String line) {
        line = line.trim();
        Matcher m = PARSE_REGEX.matcher(line);

        if (!m.find()) {
            return null;
        }

        HashMap<String, String> tags = new HashMap<>();
        if (m.group(1) != null) {
            tags = parseTags(m.group(1));
        }

        String prefix = m.group(2);
        String nick = m.group(3) == null ? m.group(2) : m.group(3);
        String ident = m.group(4) == null ? "" : m.group(4);
        String hostname = m.group(5) == null ? "" : m.group(5);
        String command = m.group(6);
        String[] tmpParams = m.group(7) == null ? new String[0] : m.group(7).split(" +");


        // Add the trailing param to the params list
        if (m.group(8) != null) {
            tmpParams = Stream.concat(Arrays.stream(tmpParams), Arrays.stream(new String[]{m.group(8)})).toArray(String[]::new);
        }

        String[] params = tmpParams;

        return new ParsedLine(tags, prefix, nick, ident, hostname, command, params, network);
    }

    public HashMap<String, String> parseTags(String tagString) {
        HashMap<String, String> tags = new HashMap<>();

        for (String tag : tagString.split(";")) {
            String[] parts = tag.split("=");
            String key = parts[0].toLowerCase();
            String value = parts.length > 1 ? parts[1] : "";
            if (key.length() > 0) {
                value = value.replace("\\\\", "\\");
                value = value.replace("\\:", ";"); // Not a typo - see http://ircv3.net/specs/core/message-tags-3.2.html#escaping-values
                value = value.replace("\\s", " ");
                value = value.replace("\\n", "\n");
                value = value.replace("\\r", "\r");

                tags.put(key, value);
            }
        }

        return tags;
    }

    public void handleLine(ParsedLine line) {
        Handlers.handle(line);
    }

    public void parseAndHandle(String line) {
        handleLine(parseLine(line));
    }
}



