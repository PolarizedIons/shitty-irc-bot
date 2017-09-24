package io.github.polarizedions.IrcParser.ParsedMessages;

import io.github.polarizedions.IrcParser.ParsedLine;
import io.github.polarizedions.networking.Network;

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
public class Privmsg extends ParsedMessage {
    public final User from;
    public final String to;
    public final String message;
    public final HashMap<String, String> tags;
    public final Network originNetwork;

    public Privmsg(ParsedLine line) {
        from = new User(line.nick, line.ident, line.hostname);
        to = line.params[0];
        message = line.params[1];
        tags = line.tags;
        originNetwork = line.originNetwork;
    }

    public void reply(String message, Object... objects) {
        boolean chanMsg = !to.equals(originNetwork.getNetworkConfig().nick);
        if (chanMsg) {
            originNetwork.send("PRIVMSG " + to + " :" + String.format(message, objects));
        } else {
            originNetwork.send("PRIVMSG " + from.nick + " :" + String.format(message, objects));
        }
    }

    @Override
    public String toString() {
        return "Privmsg{" +
                "from=" + from +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                ", tags=" + tags +
                ", originNetwork=" + originNetwork +
                '}';
    }
}
