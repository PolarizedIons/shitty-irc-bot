package io.github.polarizedions;

public class Main {

    public static void main(String[] args) {
        Logger.setLogLevel(Logger.LOG_LEVEL.DEBUG);
        IRCBot bot = IRCBot.bot;
        // TODO stuff here

        bot.start();
    }
}
