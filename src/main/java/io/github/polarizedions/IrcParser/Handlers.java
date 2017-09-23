package io.github.polarizedions.IrcParser;

import io.github.polarizedions.IrcEvents.IIrcEventHandler;
import io.github.polarizedions.IrcParser.ParsedMessages.ParsedMessage;
import io.github.polarizedions.networking.NetworkCapsHandler;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
        loadIrcEventHandlers();
        addHandler("CAP", new NetworkCapsHandler());
    }

    private static void loadIrcEventHandlers() {
        Reflections reflections = new Reflections("io.github.polarizedions.IrcEvents");
        Set<Class<? extends IIrcEventHandler>> classes = reflections.getSubTypesOf(IIrcEventHandler.class);
        for (Class<? extends IIrcEventHandler> cls : classes) {
            String[] eventNames;
            try {
                Method getEventNamesMethod = cls.getMethod("getEventNames");
                eventNames = (String[]) getEventNamesMethod.invoke(null);
            } catch (NoSuchMethodException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            } catch (InvocationTargetException e) {
                continue;
            }

            if (eventNames == null || eventNames.length == 0) {
                continue;
            }

            IIrcEventHandler handlerInstance;
            try {
                handlerInstance = cls.newInstance();
            } catch (InstantiationException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            }

            for (String eventName : eventNames) {
                addHandler(eventName, handlerInstance);
            }
        }
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
            ParsedMessage parsedMessageInstance;
            try {
                parsedMessageInstance = (ParsedMessage) handler.getParsedMessageType().getConstructor(ParsedLine.class).newInstance(line);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                continue;
            }
            handler.handle(parsedMessageInstance);
        }
    }
}
