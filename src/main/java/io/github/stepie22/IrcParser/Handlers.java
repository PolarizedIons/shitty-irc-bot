package io.github.stepie22.IrcParser;

import io.github.stepie22.IrcEvents.AutojoinHandler;
import io.github.stepie22.IrcEvents.CommandHandler;
import io.github.stepie22.IrcEvents.IIrcEventHandler;
import io.github.stepie22.networking.NetworkCapsHandler;
import io.github.stepie22.IrcEvents.PingHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class Handlers {
    private static HashMap<String, List<IIrcEventHandler>> handlerMap;

    private static void initMap() {
        handlerMap = new HashMap<>();

        // TODO: addHandler calls here
        addHandler("PING", new PingHandler());

        AutojoinHandler autojoinHandler = new AutojoinHandler();
        addHandler("RPL_ENDOFMOTD", autojoinHandler);
        addHandler("ERR_NOMOTD", autojoinHandler);
        addHandler("CAP", new NetworkCapsHandler());
        addHandler("PRIVMSG", new CommandHandler());
    }

    public static void addHandler(String numeric, IIrcEventHandler handler) {
        handlerMap.putIfAbsent(numeric, new ArrayList<>());
        handlerMap.get(numeric).add(handler);
    }

    public static void handle(ParsedLine line) {
        if (handlerMap == null) {
            initMap();
        }

        String numeric = Numerics.getMappedName(line.command);

        List<IIrcEventHandler> handlers = handlerMap.get(numeric);
        if (handlers == null) {
            return;
        }

        for (IIrcEventHandler handler : handlers) {
            handler.handle(line);
        }
    }
}
