package io.github.polarizedions.IrcParser.ParsedMessages;

import io.github.polarizedions.Logger;
import io.github.polarizedions.config.ConfigHandler;
import io.github.polarizedions.networking.Network;
import sun.rmi.runtime.Log;

import java.util.Arrays;
import java.util.HashMap;

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
    public final String prefix;
    public final String command;
    public final String[] args;
    public final HashMap<String, String> tags;
    public final Network originNetwork;

    public Command(Privmsg msg) {
        from = msg.from;
        to = msg.to;
        prefix = ConfigHandler.getConfig().botPrefix;

        boolean hasArgs = msg.message.indexOf(" ") != -1;
        command = hasArgs ? msg.message.substring(prefix.length(), msg.message.indexOf(" ")) : msg.message.substring(prefix.length());
        args = hasArgs ? msg.message.substring(msg.message.indexOf(" ") + 1).split(" ") : new String[0];
        tags = msg.tags;
        originNetwork = msg.originNetwork;
    }

    public void reply(String message) {
        boolean chanMsg = !to.equals(originNetwork.getNetworkConfig().nick);
        if (chanMsg) {
            originNetwork.send("PRIVMSG " + to + " :" + message);
        }
        else {
            originNetwork.send("PRIVMSG " + from.nick + " :" + message);
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
