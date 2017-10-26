package io.github.polarizedions.BotCommands;

import io.github.polarizedions.IrcParser.ParsedMessages.Command;
import io.github.polarizedions.Logger;
import io.github.polarizedions.Utils.Callback;
import io.github.polarizedions.config.ConfigHandler;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

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
public class Weather implements IBotCommandHandler {
    private static final String apiKey = ConfigHandler.getConfig().openweatherApi;
    private static final Logger logger = Logger.getLogger("WeatherCommand");

    @Override
    public void handle(Command command) {

        if (apiKey.length() == 0) {
            command.reply("%s: OpenWeather API key not found, sorry :(", command.from.nick);
            return;
        }
        String location = String.join(" ", command.args);

        if (location.length() == 0) {
            command.reply("%s: You have to specify a location!", command.from.nick);
            return;
        }

        getWeather(location, weatherObj -> {
            JSONObject weather = (JSONObject) weatherObj[0];

            if (weather == null) {
                command.reply("%s: An error occurred while getting the weather for '%s'", command.from.nick, location);
                return;
            }

            long resultCode = (long) weather.get("cod");
            if (resultCode != 200) {
                command.reply("%s: An error occurred while getting the weather for '%s'; %s", command.from.nick, location, weather.get("message"));
                return;
            }

            String locationName = (String) weather.get("name");
            String locationCountry = (String) ((JSONObject) weather.get("sys")).get("country");
            String currentCondition = (String) ((JSONObject) ((JSONArray) weather.get("weather")).get(0)).get("description");
            double windSpd = (double) ((JSONObject) weather.get("wind")).get("speed");
            double windDeg = Double.parseDouble(String.valueOf(((JSONObject) weather.get("wind")).get("deg")));
            double curTemp = (double) ((JSONObject) weather.get("main")).get("temp");
            double minTemp = (double) ((JSONObject) weather.get("main")).get("temp_min");
            double maxTemp = (double) ((JSONObject) weather.get("main")).get("temp_max");
            long humidity = (long) ((JSONObject) weather.get("main")).get("humidity");
            long cloudiness = (long) ((JSONObject) weather.get("clouds")).get("all");

            command.reply("Weather for %s, %s: Current Weather: %s; Min: %.2f°C (%.2f°F), Max: %.2f°C (%.2f°F), Cur: %.2f°C (%.2f°F); Wind: %.2f m/s (%.2f m/h) %s; Humidity: %d%%; Cloudiness: %d%%",
                    locationName,
                    locationCountry,
                    currentCondition,
                    calcCelsius(minTemp),
                    calcFahrenheit(minTemp),
                    calcCelsius(maxTemp),
                    calcFahrenheit(maxTemp),
                    calcCelsius(curTemp),
                    calcFahrenheit(curTemp),
                    windSpd,
                    calcMPH(windSpd),
                    degToDir(windDeg),
                    humidity,
                    cloudiness
            );
        });
    }

    private double calcCelsius(double kelvin) {
        return kelvin - 273.15d;
    }

    private double calcFahrenheit(double kelvin) {
        return kelvin * 9d / 5d - 459.67d;
    }

    private double calcMPH(double mps) {
        return mps * 2.2369d;
    }

    private String degToDir(double deg) {
        // Top 0deg, clockwise
        deg = deg % 360;
        if (deg > 0 && deg <= 22.5) {
            return "N";
        } else if (deg > 22.5 && deg <= 67.5) {
            return "NW";
        } else if (deg > 67.5 && deg <= 112.5) {
            return "W";
        } else if (deg > 112.5 && deg <= 157.5) {
            return "SW";
        } else if (deg > 157.5 && deg <= 202.5) {
            return "S";
        } else if (deg > 202.5 && deg <= 247.5) {
            return "SE";
        } else if (deg > 247.5 && deg <= 292.5) {
            return "E";
        } else if (deg > 292.5 && deg <= 337.5) {
            return "NE";
        } else if (deg > 337.5) {
            return "N";
        }

        return "?";
    }

    private void getWeather(String location, Callback cb) {
        JSONObject weather = null;
        try {
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(location, "UTF-8") + "&APPID=" + URLEncoder.encode(apiKey, "UTF-8");
            weather = (JSONObject) JSONValue.parseWithException(IOUtils.toString(new URL(url).openStream()));
            cb.reply(weather);
        } catch (ParseException | IOException e) {
            logger.error("Error looking up the weather for " + location);
            logger.error(e);
            logger.error("Response: ", weather);
            cb.reply(null);
        }
    }

    @Override
    public String getCommand() {
        return "weather";
    }
}

