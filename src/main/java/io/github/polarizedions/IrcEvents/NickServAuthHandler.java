package io.github.polarizedions.IrcEvents;

import io.github.polarizedions.IrcParser.ParsedMessages.ParsedMessage;
import io.github.polarizedions.IrcParser.ParsedMessages.Privmsg;
import io.github.polarizedions.config.NetworkConfig;
import io.github.polarizedions.networking.Network;

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
public class NickServAuthHandler implements IIrcEventHandler {
    public static String[] getEventNames() {
        return new String[]{"NOTICE"};
    }

    @Override
    public void handle(ParsedMessage line) {
        Privmsg msg = (Privmsg) line;
        Network network = msg.originNetwork;
        NetworkConfig networkConfig = network.getNetworkConfig();

        if (!networkConfig.nickServAuth.equals("true") || network.isAuthed()) {
            return;
        }

        if (!msg.from.nick.equals("NickServ")) {
            return;
        }

        if (msg.message.startsWith("This nickname is registered")) {
            msg.reply("IDENTIFY %s %s", networkConfig.loginUser, networkConfig.loginPass);
            return;
        }

        if (msg.message.contains("are now identified for") || msg.message.contains("you are now recognized")) {
            network.setAuthed(true);
        }
    }

    @Override
    public Class getParsedMessageType() {
        return Privmsg.class;
    }
}
