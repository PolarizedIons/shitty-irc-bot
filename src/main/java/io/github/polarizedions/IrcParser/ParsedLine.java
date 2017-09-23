package io.github.polarizedions.IrcParser;

import io.github.polarizedions.networking.Network;

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
public class ParsedLine {
    public final HashMap<String, String> tags;
    public final String prefix;
    public final String nick;
    public final String ident;
    public final String hostname;
    public final String command;
    public final String[] params;

    public final Network originNetwork;

    public ParsedLine(HashMap<String, String> tags, String prefix, String nick, String ident, String hostname, String command, String[] params, Network network) {
        this.tags = tags;
        this.prefix = prefix;
        this.nick = nick;
        this.ident = ident;
        this.hostname = hostname;
        this.command = command;
        this.params = params;
        this.originNetwork = network;
    }

    @Override
    public String toString() {
        return "ParsedLine{" +
                "tags=" + tags +
                ", prefix='" + prefix + '\'' +
                ", nick='" + nick + '\'' +
                ", ident='" + ident + '\'' +
                ", hostname='" + hostname + '\'' +
                ", command='" + command + '\'' +
                ", params=" + Arrays.toString(params) +
                ", network=" + originNetwork +
                '}';
    }
}
