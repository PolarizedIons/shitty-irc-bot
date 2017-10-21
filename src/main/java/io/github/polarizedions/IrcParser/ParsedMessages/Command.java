package io.github.polarizedions.IrcParser.ParsedMessages;

import io.github.polarizedions.IrcParser.ParsedLine;
import io.github.polarizedions.config.ConfigHandler;
import io.github.polarizedions.networking.Network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class Command extends ParsedMessage {
    public final User from;
    public final String to;
    public final boolean toChannel;
    public final String prefix;
    public final String command;
    public final String[] args;
    public final HashMap<String, String> tags;
    public final Network originNetwork;

    public Command(ParsedLine line) {
        this(new Privmsg(line));
    }

    public Command(Privmsg msg) {
        from = msg.from;
        to = msg.to;
        toChannel = !to.equals(msg.originNetwork.getNetworkConfig().nick);
        prefix = ConfigHandler.getConfig().botPrefix;
        Matcher commandMatcher = (Pattern.compile("^" + prefix + "([a-zA-Z]*)(?: (.*))?")).matcher(msg.message);
        commandMatcher.find();
        command = commandMatcher.group(1);
        args = commandMatcher.group(2) == null ? new String[0] : commandMatcher.group(2).split(" ");
        tags = msg.tags;
        originNetwork = msg.originNetwork;
    }

    public void reply(String message, Object... objects) {
        if (toChannel) {
            originNetwork.send("PRIVMSG " + to + " :" + String.format(message, objects));
        } else {
            originNetwork.send("PRIVMSG " + from.nick + " :" + String.format(message, objects));
        }
    }

    @Override
    public String toString() {
        return "Command{" +
                "from=" + from +
                ", to='" + to + '\'' +
                ", prefix='" + prefix + '\'' +
                ", command='" + command + '\'' +
                ", args=" + Arrays.toString(args) +
                ", tags=" + tags +
                ", originNetwork=" + originNetwork +
                '}';
    }
}
