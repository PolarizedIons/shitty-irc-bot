package io.github.polarizedions.IrcEvents;

import io.github.polarizedions.BotCommands.IBotCommandHandler;
import io.github.polarizedions.IrcParser.ParsedMessages.Command;
import io.github.polarizedions.IrcParser.ParsedMessages.Privmsg;
import io.github.polarizedions.config.Config;
import io.github.polarizedions.config.ConfigHandler;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
public class CommandHandler implements IMessageHandler<Privmsg> {
    private HashMap<String, IBotCommandHandler> botCommandHandlers;

    public static String[] getEventNames() {
        return new String[]{"PRIVMSG"};
    }

    private void initHandlers() {
        botCommandHandlers = new HashMap<>();
        loadBotCommandHandlers();
    }

    @Override
    public void handle(Privmsg msg) {
        if (botCommandHandlers == null) {
            initHandlers();
        }

        Config config = ConfigHandler.getConfig();

        if (msg.message.startsWith(config.botPrefix)) {
            IBotCommandHandler commandHandler = botCommandHandlers.get(msg.message.split(" ")[0].replaceFirst(config.botPrefix, "").toLowerCase());
            if (commandHandler != null) {
                commandHandler.handle(new Command(msg));
            }
        }
    }

    private void loadBotCommandHandlers() {
        Reflections reflections = new Reflections("io.github.polarizedions.BotCommands");
        Set<Class<? extends IBotCommandHandler>> classes = reflections.getSubTypesOf(IBotCommandHandler.class);
        for (Class<? extends IBotCommandHandler> cls : classes) {
            IBotCommandHandler commandHandlerInstance;
            try {
                commandHandlerInstance = cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                continue;
            }

            String command;
            try {
                Method getCommandMethod = cls.getMethod("getCommand");
                command = (String) getCommandMethod.invoke(commandHandlerInstance);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                continue;
            }

            if (command == null) {
                continue;
            }

            botCommandHandlers.put(command, commandHandlerInstance);
        }
    }
}
