package io.github.stepie22.IrcEvents;

import io.github.stepie22.BotCommands.IBotCommandHandler;
import io.github.stepie22.BotCommands.PingCommandHandler;
import io.github.stepie22.IrcParser.ParsedLine;
import io.github.stepie22.Logger;
import io.github.stepie22.config.Config;
import io.github.stepie22.config.ConfigHandler;

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
public class CommandHandler implements IIrcEventHandler {
    private HashMap<String, IBotCommandHandler> botCommandHandlers;

    private void initHandlers() {
        botCommandHandlers = new HashMap<>();

        botCommandHandlers.put("ping", new PingCommandHandler());
    }

    @Override
    public void handle(ParsedLine line) {
        if (botCommandHandlers == null) {
            initHandlers();
        }

        Logger logger = Logger.getLogger("CommandHandler");
        Config config = ConfigHandler.getConfig();

        if (line.params[1].startsWith(config.botPrefix)) {
            logger.debug("GET: " + line.params[1].replaceFirst(config.botPrefix,"").toLowerCase());
            IBotCommandHandler commandHandler = botCommandHandlers.get(line.params[1].replaceFirst(config.botPrefix,"").toLowerCase());
            if (commandHandler != null) {
                commandHandler.handle(line);
            }
        }
    }
}
