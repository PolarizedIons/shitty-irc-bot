package io.github.polarizedions.IrcParser;

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

import java.util.HashMap;

/**
 * NOTE: Parts of this file is derived from the irc-parser used by irc-framework (https://github.com/kiwiirc/irc-framework)
 * Licence: MIT - https://github.com/kiwiirc/irc-framework/blob/bc564f1509cc9e02bb4ef9fb9712a1eb1952b4ea/LICENSE.txt
 */

public class Numerics {

    private static HashMap<String, String> numericMap;
    private static void initMap() {
        numericMap = new HashMap<>();
        numericMap.put("001", "RPL_WELCOME");
        numericMap.put("002", "RPL_YOURHOST");
        numericMap.put("003", "RPL_CREATED");
        numericMap.put("004", "RPL_MYINFO");
        numericMap.put("005", "RPL_ISUPPORT");
        numericMap.put("006", "L_MAPMORE");
        numericMap.put("007", "RPL_MAPEND");
        numericMap.put("008", "RPL_SNOMASK");
        numericMap.put("015", "RPL_MAP");
        numericMap.put("017", "RPL_MAPEND");
        numericMap.put("200", "RPL_TRACELINK");
        numericMap.put("201", "RPL_TRACECONNECTING");
        numericMap.put("202", "RPL_TRACEHANDSHAKE");
        numericMap.put("203", "RPL_TRACEUNKNOWN");
        numericMap.put("204", "RPL_TRACEOPERATOR");
        numericMap.put("205", "RPL_TRACEUSER");
        numericMap.put("206", "RPL_TRACESERVER");
        numericMap.put("207", "RPL_TRACESERVICE");
        numericMap.put("208", "RPL_TRACENEWTYPE");
        numericMap.put("209", "RPL_TRACECLASS");
        numericMap.put("210", "RPL_TRACERECONNECT");
        numericMap.put("211", "RPL_STATSLINKINFO");
        numericMap.put("212", "RPL_STATSCOMMANDS");
        numericMap.put("213", "RPL_STATSCLINE");
        numericMap.put("214", "RPL_STATSNLINE");
        numericMap.put("215", "RPL_STATSILINE");
        numericMap.put("216", "RPL_STATSKLINE");
        numericMap.put("217", "RPL_STATSPLINE");
        numericMap.put("218", "RPL_STATSYLINE");
        numericMap.put("219", "RPL_ENDOFSTATS");
        numericMap.put("220", "RPL_STATSBLINE");
        numericMap.put("221", "RPL_UMODEIS");
        numericMap.put("222", "RPL_SQLINE_NICK");
        numericMap.put("223", "RPL_STATS_E");
        numericMap.put("224", "RPL_STATS_D");
        numericMap.put("229", "RPL_SPAMFILTER");
        numericMap.put("231", "RPL_SERVICEINFO");
        numericMap.put("232", "RPL_ENDOFSERVICES");
        numericMap.put("233", "RPL_SERVICE");
        numericMap.put("234", "RPL_SERVLIST");
        numericMap.put("235", "RPL_SERVLISTEND");
        numericMap.put("241", "RPL_STATSLLINE");
        numericMap.put("242", "RPL_STATSUPTIME");
        numericMap.put("243", "RPL_STATSOLINE");
        numericMap.put("244", "RPL_STATSHLINE");
        numericMap.put("245", "RPL_STATSSLINE");
        numericMap.put("246", "RPL_STATSGLINE");
        numericMap.put("247", "RPL_STATSXLINE");
        numericMap.put("248", "RPL_STATSULINE");
        numericMap.put("249", "RPL_STATSDEBUG");
        numericMap.put("250", "RPL_STATSCONN");
        numericMap.put("251", "RPL_LUSERCLIENT");
        numericMap.put("252", "RPL_LUSEROP");
        numericMap.put("253", "RPL_LUSERUNKNOWN");
        numericMap.put("254", "RPL_LUSERCHANNELS");
        numericMap.put("255", "RPL_LUSERME");
        numericMap.put("256", "RPL_ADMINME");
        numericMap.put("257", "RPL_ADMINLOC1");
        numericMap.put("258", "RPL_ADMINLOC2");
        numericMap.put("259", "RPL_ADMINEMAIL");
        numericMap.put("265", "RPL_LOCALUSERS");
        numericMap.put("266", "RPL_GLOBALUSERS");
        numericMap.put("290", "RPL_HELPHDR");
        numericMap.put("291", "RPL_HELPOP");
        numericMap.put("292", "RPL_HELPTLR");
        numericMap.put("301", "RPL_AWAY");
        numericMap.put("303", "RPL_ISON");
        numericMap.put("304", "RPL_ZIPSTATS");
        numericMap.put("305", "RPL_UNAWAY");
        numericMap.put("306", "RPL_NOWAWAY");
        numericMap.put("307", "RPL_WHOISREGNICK");
        numericMap.put("310", "RPL_WHOISHELPOP");
        numericMap.put("311", "RPL_WHOISUSER");
        numericMap.put("312", "RPL_WHOISSERVER");
        numericMap.put("313", "RPL_WHOISOPERATOR");
        numericMap.put("314", "RPL_WHOWASUSER");
        numericMap.put("315", "RPL_ENDOFWHO");
        numericMap.put("317", "RPL_WHOISIDLE");
        numericMap.put("318", "RPL_ENDOFWHOIS");
        numericMap.put("319", "RPL_WHOISCHANNELS");
        numericMap.put("320", "RPL_WHOISSPECIAL");
        numericMap.put("321", "RPL_LISTSTART");
        numericMap.put("322", "RPL_LIST");
        numericMap.put("323", "RPL_LISTEND");
        numericMap.put("324", "RPL_CHANNELMODEIS");
        numericMap.put("328", "RPL_CHANNEL_URL");
        numericMap.put("329", "RPL_CREATIONTIME");
        numericMap.put("330", "RPL_WHOISACCOUNT");
        numericMap.put("331", "RPL_NOTOPIC");
        numericMap.put("332", "RPL_TOPIC");
        numericMap.put("333", "RPL_TOPICWHOTIME");
        numericMap.put("335", "RPL_WHOISBOT");
        numericMap.put("338", "RPL_WHOISACTUALLY");
        numericMap.put("341", "RPL_INVITING");
        numericMap.put("352", "RPL_WHOREPLY");
        numericMap.put("353", "RPL_NAMEREPLY");
        numericMap.put("364", "RPL_LINKS");
        numericMap.put("365", "RPL_ENDOFLINKS");
        numericMap.put("366", "RPL_ENDOFNAMES");
        numericMap.put("367", "RPL_BANLIST");
        numericMap.put("368", "RPL_ENDOFBANLIST");
        numericMap.put("369", "RPL_ENDOFWHOWAS");
        numericMap.put("371", "RPL_INFO");
        numericMap.put("372", "RPL_MOTD");
        numericMap.put("374", "RPL_ENDINFO");
        numericMap.put("375", "RPL_MOTDSTART");
        numericMap.put("376", "RPL_ENDOFMOTD");
        numericMap.put("378", "RPL_WHOISHOST");
        numericMap.put("379", "RPL_WHOISMODES");
        numericMap.put("381", "RPL_NOWOPER");
        numericMap.put("396", "RPL_HOSTCLOACKING");
        numericMap.put("401", "ERR_NOSUCHNICK");
        numericMap.put("402", "ERR_NOSUCHSERVER");
        numericMap.put("404", "ERR_CANNOTSENDTOCHAN");
        numericMap.put("405", "ERR_TOOMANYCHANNELS");
        numericMap.put("406", "ERR_WASNOSUCHNICK");
        numericMap.put("421", "ERR_UNKNOWNCOMMAND");
        numericMap.put("422", "ERR_NOMOTD");
        numericMap.put("423", "ERR_NOADMININFO");
        numericMap.put("432", "ERR_ERRONEUSNICKNAME");
        numericMap.put("433", "ERR_NICKNAMEINUSE");
        numericMap.put("441", "ERR_USERNOTINCHANNEL");
        numericMap.put("442", "ERR_NOTONCHANNEL");
        numericMap.put("443", "ERR_USERONCHANNEL");
        numericMap.put("451", "ERR_NOTREGISTERED");
        numericMap.put("461", "ERR_NOTENOUGHPARAMS");
        numericMap.put("464", "ERR_PASSWDMISMATCH");
        numericMap.put("470", "ERR_LINKCHANNEL");
        numericMap.put("471", "ERR_CHANNELISFULL");
        numericMap.put("472", "ERR_UNKNOWNMODE");
        numericMap.put("473", "ERR_INVITEONLYCHAN");
        numericMap.put("474", "ERR_BANNEDFROMCHAN");
        numericMap.put("475", "ERR_BADCHANNELKEY");
        numericMap.put("481", "ERR_NOPRIVILEGES");
        numericMap.put("482", "ERR_CHANOPRIVSNEEDED");
        numericMap.put("483", "ERR_CANTKILLSERVER");
        numericMap.put("484", "ERR_ISCHANSERVICE");
        numericMap.put("485", "ERR_ISREALSERVICE");
        numericMap.put("491", "ERR_NOOPERHOST");
        numericMap.put("670", "RPL_STARTTLS");
        numericMap.put("671", "RPL_WHOISSECURE");
        numericMap.put("900", "RPL_SASLAUTHENTICATED");
        numericMap.put("903", "RPL_SASLLOGGEDIN");
        numericMap.put("904", "ERR_SASLNOTAUTHORISED");
        numericMap.put("906", "ERR_SASLABORTED");
        numericMap.put("907", "ERR_SASLALREADYAUTHED");
        numericMap.put("972", "ERR_CANNOTDOCOMMAND");
        numericMap.put("WALLOPS", "RPL_WALLOP");
    }

    public static String getMappedName(int numeric) {
        return getMappedName(String.format("%03d", numeric));
    }
    public static String getMappedName(String numeric) {
        if (numericMap == null) {
            initMap();
        }

        return numericMap.getOrDefault(numeric, numeric);
    }
}
