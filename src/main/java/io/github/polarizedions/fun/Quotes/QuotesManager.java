package io.github.polarizedions.fun.Quotes;

import com.google.common.collect.EvictingQueue;
import io.github.polarizedions.IrcParser.ParsedMessages.Privmsg;
import io.github.polarizedions.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
public class QuotesManager {
    private static final int MESSAGE_QUEUE_SIZE = 200;

    private static HashMap<String, EvictingQueue<Privmsg>> messageQueues = new HashMap<>();
    private static Logger logger = Logger.getLogger("QuotesManager");


    public static void handleChannelMessage(Privmsg line) {
        if (!messageQueues.containsKey(line.to)) {
            messageQueues.put(line.to, EvictingQueue.create(MESSAGE_QUEUE_SIZE));
        }

        logger.debug("Adding line from '" + line.from.nick + "' in '" + line.to + "' : '" + line.message + "'");
        messageQueues.get(line.to).add(line);
    }

    public static Privmsg[] getAllChannelMessages(String channel) {
        return messageQueues.get(channel).toArray(new Privmsg[0]);
    }

    public static Privmsg[] getMessagesBy(String channel, String nick) {
        return Arrays.stream(getAllChannelMessages(channel)).filter(privmsg -> privmsg.from.nick.equalsIgnoreCase(nick)).toArray(Privmsg[]::new);
    }

    public static Privmsg[] getMessagesBy(String channel, String nick, String containing) {
        return Arrays.stream(getMessagesBy(channel, nick)).filter(privmsg -> privmsg.message.contains(containing)).toArray(Privmsg[]::new);
    }

    public static Quote saveQuote(Privmsg line) {
        Quote quote = new Quote(line.to, line.from.nick, line.message);
        try {
            QuotesDatabase.quotesDao.create(quote);
            return quote;
        } catch (SQLException e) {
            logger.error("Error saving quote " + line);
            e.printStackTrace();
        }

        return null;
    }

    public static Quote findQuote(String nick) {
        List<Quote> quotes = QuotesDatabase.find(nick, "");
        if (quotes == null || quotes.size() == 0) {
            return null;
        }

        return quotes.get(new Random().nextInt(quotes.size()));
    }

    public static Quote findQuote(String nick, String containing) {
        List<Quote> quotes = QuotesDatabase.find(nick, containing);
        if (quotes == null || quotes.size() == 0) {
            return null;
        }

        return quotes.get(new Random().nextInt(quotes.size()));
    }

    public static Quote getQuote(String nick, int id) {
        return QuotesDatabase.getByID(nick, id);
    }

    public static Quote getRandomQuote() {
        return QuotesDatabase.getRandom();
    }
}
