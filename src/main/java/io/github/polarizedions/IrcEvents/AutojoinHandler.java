package io.github.polarizedions.IrcEvents;

import io.github.polarizedions.IrcParser.ParsedLine;
import io.github.polarizedions.IrcParser.ParsedMessages.ParsedMessage;
import io.github.polarizedions.IrcParser.ParsedMessages.Unparsed;
import io.github.polarizedions.Logger;
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
public class AutojoinHandler implements IIrcEventHandler {

    @Override
    public void handle(ParsedMessage parsedMessage) {
        Unparsed line = (Unparsed) parsedMessage;
        Network nw = line.originNetwork;
        String autojoinChannels = nw.getNetworkConfig().autojoinChannels;
        if (autojoinChannels != null & autojoinChannels.length() > 0) {
            Logger.getLogger("AutojoinHandler").debug("Autojoining " + autojoinChannels);
            nw.send("JOIN " + autojoinChannels);
        }
    }

    public static String[] getEventNames() {
        return new String[] {"RPL_ENDOFMOTD", "ERR_NOMOTD"};
    }
}
