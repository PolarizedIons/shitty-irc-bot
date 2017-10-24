package io.github.polarizedions.fun.Quotes;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.github.polarizedions.IRCBot;
import io.github.polarizedions.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
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

class QuotesDatabase {
    private static final Logger logger = Logger.getLogger("QuotesDatabase");
    public static Dao<Quote, Integer> quotesDao;
    private static ConnectionSource connectionSource;

    private static void ensureConnected() {
        if (connectionSource == null) {
            connect();
        }
    }

    public static void connect() {
        String dbPath = Paths.get(IRCBot.STORAGE_DIR.toString(), "quotes.db").toAbsolutePath().toString();
        try {
            connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + dbPath);
            TableUtils.createTableIfNotExists(connectionSource, Quote.class);
            quotesDao = DaoManager.createDao(connectionSource, Quote.class);
            logger.debug("Connected to the database!");

        } catch (SQLException e) {
            logger.error("Error connecting to the quotes database!");
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            connectionSource.close();
        } catch (IOException e) {
            logger.error("Error disconnecting to the quotes database!");
            e.printStackTrace();
        }
    }

    public static Quote getRandom() {
        ensureConnected();

        QueryBuilder<Quote, Integer> statementBuilder = quotesDao.queryBuilder();

        try {
            long rowsCount = quotesDao.countOf();
            long offset = Math.abs(new Random().nextInt()) % Math.max(1L, rowsCount);

            statementBuilder
                    .limit(1L)
                    .offset(offset);

            List<Quote> result = quotesDao.query(statementBuilder.prepare());

            if (result.size() == 0) {
                return null;
            }

            return result.get(0);

        } catch (SQLException e) {
            logger.error("Error while getting random quote ");
            e.printStackTrace();
        }
        return null;
    }

    public static Quote getByID(String user, int id) {
        ensureConnected();

        QueryBuilder<Quote, Integer> statementBuilder = quotesDao.queryBuilder();

        try {
            statementBuilder
                    .where()
                    .like(Quote.QUOTE_ID_FIELD, id)
                    .and()
                    .like(Quote.NICK_FIELD, user);

            List<Quote> result = quotesDao.query(statementBuilder.prepare());

            if (result.size() == 0) {
                return null;
            }

            return result.get(0);

        } catch (SQLException e) {
            logger.error("Error while getting quote " + id + " for " + user);
            e.printStackTrace();
        }
        return null;
    }

    public static List<Quote> find(String user, String containing) {
        ensureConnected();

        QueryBuilder<Quote, Integer> statementBuilder = quotesDao.queryBuilder();

        try {
            statementBuilder
                    .where()
                    .like(Quote.NICK_FIELD, user)
                    .and()
                    .like(Quote.MESSAGE_FIELD, "%" + containing + "%");

            return quotesDao.query(statementBuilder.prepare());

        } catch (SQLException e) {
            logger.error("Error while searching for quote! (user:" + user + ", containing:" + containing);
            e.printStackTrace();
        }
        return null;
    }

    public static int nextQuoteId(String nick) {
        ensureConnected();

        QueryBuilder<Quote, Integer> statementBuilder = quotesDao.queryBuilder();

        try {
            statementBuilder
                    .selectColumns(Quote.QUOTE_ID_FIELD)
                    .limit(1L)
                    .orderBy(Quote.QUOTE_ID_FIELD, false)
                    .where()
                    .like(Quote.NICK_FIELD, nick);

            List<Quote> result = quotesDao.query(statementBuilder.prepare());

            if (result.size() == 0) {
                return 1;
            }

            return result.get(0).getQuoteID() + 1;
        } catch (SQLException e) {
            logger.error("Error while getting next quote id for " + nick);
            e.printStackTrace();
        }

        return 1;
    }
}

