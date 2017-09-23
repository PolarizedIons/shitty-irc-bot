package io.github.stepie22.networking;

import io.github.stepie22.IrcEvents.IIrcEventHandler;
import io.github.stepie22.IrcParser.ParsedLine;
import io.github.stepie22.Logger;

import java.util.ArrayList;
import java.util.Arrays;

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
public class NetworkCapsHandler implements IIrcEventHandler {
    @Override
    public void handle(ParsedLine line) {
        Logger logger = Logger.getLogger("NetworkCaps");
        logger.debug(line.toString());
        Network network = line.originNetwork;
        NetworkCapabilities networkCaps = network.getNetworkCapabilities();
        switch (line.params[1]) {
            case "LS":
                String[] supportedCaps = line.params[line.params.length -1].split(" ");
                ArrayList<String> requestedCaps = networkCaps.getRequestedCaps();
                for (String cap : supportedCaps) {
                    if (requestedCaps.contains(cap)) {
                        networkCaps.addSupportedCap(cap);
                    }
                    else {
                        requestedCaps.remove(cap);
                    }
                }


                if (!line.params[2].equals("*")) { // Last line of CAP LS
                    network.send("CAP REQ :" + String.join(" ", requestedCaps));
                }

                break;

            case "ACK":
                String[] acktCaps = line.params[line.params.length - 1].split(" ");
                for (String cap : acktCaps) {
                    networkCaps.addActivatedCap(cap);
                }

                networkCaps.lock();

                // Do multiline ACK's exist?
                network.send("CAP END");

                break;
            default:
                network.send("CAP END");
        }
    }
}
