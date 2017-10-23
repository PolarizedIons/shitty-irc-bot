package io.github.polarizedions.BotCommands;

import io.github.polarizedions.IrcParser.ParsedMessages.Command;
import io.github.polarizedions.IrcParser.ParsedMessages.Privmsg;
import io.github.polarizedions.config.ConfigHandler;
import io.github.polarizedions.fun.Quotes.Quote;
import io.github.polarizedions.fun.Quotes.QuotesManager;

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
public class QuotesCommandHandler implements IBotCommandHandler {
    private final String COMMAND_USAGE = ConfigHandler.getConfig().botPrefix + getCommand() + " random OR save <nick> <quote filter> OR get <nick> [quote #] OR find <nick> [quote filter]";

    @Override
    public void handle(Command command) {
        if (command.args.length < 2) {
            if (command.args.length == 0 || !command.args[0].equals("random")) {
                command.reply("%s: You need to specify at least two arguments!", command.from.nick);
                command.reply("%s: Usage: " + COMMAND_USAGE, command.from.nick);
                return;
            }
        }

        switch (command.args[0]) {
            case "save":
                save(command);
                break;
            case "get":
                get(command);
                break;
            case "find":
                find(command);
                break;
            case "random":
                random(command);
                break;
            default:
                command.reply("%s: Unknown second argument '%s'. Usage: %s", command.from.nick, command.args[0], COMMAND_USAGE);
        }
    }

    private void save(Command command) {
        if (!command.toChannel) {
            command.reply("This command must be used in a channel!");
            return;
        }

        if (command.args.length < 3) {
            command.reply("%s: You must specify the quote filter to find the message", command.from.nick);
            return;
        }

        String nick = command.args[1];
        String filter = String.join(" ", Arrays.copyOfRange(command.args, 2, command.args.length));
        Privmsg[] messages = QuotesManager.getMessagesBy(command.to, nick, filter);

        if (messages == null || messages.length == 0) {
            command.reply("%s: Couldn't find a message by '%s' containing '%s'", command.from.nick, nick, filter);
            return;
        }

        Quote quote = QuotesManager.saveQuote(messages[0]);
        if (quote == null) {
            command.reply("%s: Error saving quote", command.from.nick);
            return;
        }

        command.reply("%s: Saved quote %s", command.from.nick, quote.toString());
    }

    private void get(Command command) {
        String nick = command.args[1];
        String quoteNum = command.args.length > 2 ? command.args[2] : null;

        if (quoteNum != null) {
            int quoteID;
            try {
                quoteID = Integer.parseInt(quoteNum);
            } catch (NumberFormatException ex) {
                command.reply("%s: '%s' is not a number, did you mean to use the find command?", command.from.nick, quoteNum);
                return;
            }
            Quote quote = QuotesManager.getQuote(nick, quoteID);

            if (quote == null) {
                command.reply("%s: Could not find quote #%s for %s", command.from.nick, quoteNum, nick);
                return;
            }

            command.reply("%s: %s", command.from.nick, quote.toString());
        } else {
            Quote quote = QuotesManager.findQuote(nick);

            if (quote == null) {
                command.reply("%s: Could not find a quote for %s", command.from.nick, nick);
                return;
            }

            command.reply("%s: %s", command.from.nick, quote.toString());
        }
    }


    public void find(Command command) {
        String nick = command.args[1];
        String filterText = command.args.length > 2 ? String.join(" ", Arrays.copyOfRange(command.args, 2, command.args.length)) : "";

        if (nick.equals("*") && !filterText.isEmpty()) {
            nick = "%"; // Match anything
        }

        Quote quote = QuotesManager.findQuote(nick, filterText);

        if (quote == null) {
            if (nick.equals("%")) {
                command.reply("%s: Could not find a quote containing '%s'", command.from.nick, filterText);
                return;
            } else if (filterText.isEmpty()) {
                command.reply("%s: Could not find a quote by '%s'", command.from.nick, nick);
                return;
            } else {
                command.reply("%s: Could not find a quote by '%s' containing '%s'", command.from.nick, nick, filterText);
                return;
            }
        }

        command.reply("%s: %s", command.from.nick, quote.toString());
    }

    public void random(Command command) {
        Quote quote = QuotesManager.getRandomQuote();

        if (quote == null) {
            command.reply("%s: Could not find any quotes in my database! Sorry :(", command.from.nick);
            return;
        }

        command.reply("%s: %s", command.from.nick, quote.toString());
    }

    @Override
    public String getCommand() {
        return "quote";
    }
}
