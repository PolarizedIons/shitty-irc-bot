package io.github.polarizedions.IrcEvents;

import io.github.polarizedions.IrcParser.Numerics;
import io.github.polarizedions.IrcParser.ParsedMessages.Unparsed;
import io.github.polarizedions.Utils;
import io.github.polarizedions.config.NetworkConfig;
import io.github.polarizedions.networking.Network;
import io.github.polarizedions.networking.NetworkCapsHandler;
import sun.misc.BASE64Encoder;

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
public class SaslAuthHandler implements IIrcEventHandler<Unparsed> {
    private static final String[] SASL_AUTH_SUCCESS = new String[]{"RPL_LOGGEDIN", "RPL_SASLSUCCESS", "ERR_SASLALREADY", "RPL_SASLAUTHENTICATED", "RPL_SASLLOGGEDIN"};
    private static final String[] SASL_AUTH_FAIL = new String[]{"ERR_NICKLOCKED", "ERR_SASLFAIL", "ERR_SASLTOOLONG", "ERR_SASLABORTED", "RPL_SASLMECHS", "ERR_SASLNOTAUTHORISED"};

    private static HashMap<Network, Boolean> doneWithSasl = new HashMap<>();

    public static String[] getEventNames() {
        return Utils.ConcatArrays(new String[]{"AUTHENTICATE"}, SASL_AUTH_SUCCESS, SASL_AUTH_FAIL);
    }

    public static boolean isDone(Network network) {
        if (!network.getNetworkConfig().SASLAuth.equals("true") || !network.getNetworkCapabilities().isActive("sasl")) {
            return true;
        }

        return doneWithSasl.getOrDefault(network, false);
    }

    @Override
    public void handle(Unparsed line) {
        Network network = line.originNetwork;

        if (!network.getNetworkConfig().SASLAuth.equals("true") || !network.getNetworkCapabilities().isActive("sasl")) {
            return;
        }

        String lineStr = line.command + " " + String.join(" ", line.params);
        if (lineStr.equals("AUTHENTICATE +")) {
            NetworkConfig nwConfig = network.getNetworkConfig();

            // username\0username\0password
            String authString = String.format("%s\0%s\0%s", nwConfig.loginUser, nwConfig.loginUser, nwConfig.loginPass);

            network.send("AUTHENTICATE " + new BASE64Encoder().encode(authString.getBytes()));

            doneWithSasl.put(network, false);
            return;
        }

        String command = Numerics.getMappedName(line.command);

        if (Arrays.stream(SASL_AUTH_SUCCESS).anyMatch(v -> v.equals(command))) {
            network.setAuthed(true);
            doneWithSasl.put(network, true);

            NetworkCapsHandler.checkDone(network);
            return;
        }

        if (Arrays.stream(SASL_AUTH_FAIL).anyMatch(v -> v.equals(command))) {
            network.setAuthed(false);
            doneWithSasl.put(network, true);

            NetworkCapsHandler.checkDone(network);
            return;
        }
    }
}
