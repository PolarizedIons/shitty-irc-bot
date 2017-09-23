package io.github.stepie22.IrcParser;

import io.github.stepie22.networking.Network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.stream.Stream;

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
