package io.github.polarizedions.fun.Quotes;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;

@DatabaseTable(tableName = "QuotesTable")
public class Quote {
    // for QueryBuilder to be able to find the fields
    public static final String QUOTE_ID_FIELD = "quoteID";
    public static final String CHANNEL_FIELD = "channel";
    public static final String NICK_FIELD = "nick";
    public static final String DATE_FIELD = "date";
    public static final String MESSAGE_FIELD = "message";

    @DatabaseField(generatedId = true)
    private int internalID;

    @DatabaseField
    private int quoteID;

    @DatabaseField
    private String channel;

    @DatabaseField
    private String nick;

    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date date;

    @DatabaseField
    private String message;


    public Quote() {
        // ORMLite needs a no-arg constructor
    }

    public Quote(int quoteID, String channel, String nick, Date date, String message) {
        this.quoteID = quoteID;
        this.channel = channel;
        this.nick = nick;
        this.date = date;
        this.message = message;
    }

    public Quote(String channel, String nick, String message) {
        this.quoteID = QuotesDatabase.nextQuoteId(nick);
        this.channel = channel;
        this.nick = nick;
        this.date = new Date();
        this.message = message;
    }

    public int getInternalID() {
        return internalID;
    }

    public void setInternalID(int internalID) {
        this.internalID = internalID;
    }

    public int getQuoteID() {
        return quoteID;
    }

    public void setQuoteID(int quoteID) {
        this.quoteID = quoteID;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "\"" + message + "\" -- " + nick + "#" + quoteID + " (" + new SimpleDateFormat("dd/MM/yyyy").format(date) + ")";
    }
}
