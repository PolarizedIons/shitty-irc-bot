package io.github.polarizedions.Utils;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import io.github.polarizedions.IRCBot;
import io.github.polarizedions.Logger;

import java.io.*;
import java.nio.file.Paths;
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
public class PersistentFile {
    private File file;
    private HashMap<String, Object> values;
    private Logger logger;

    public PersistentFile(String configName) {
        logger = Logger.getLogger("PersistentFile:" + configName);
        file = Paths.get(IRCBot.STORAGE_DIR.toString(), configName + ".dat").toFile();

        if (file.exists()) {
            load();
        }
        else {
            values = new HashMap<>();
            save();
        }
    }

    public void load() {
        logger.debug("Loading persistent file");

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            values = (HashMap<String, Object>) ois.readObject();
        } catch (Exception e) {
            logger.error("Error loading persistent file");
            e.printStackTrace();
        } finally {
            if(ois != null){
                try {
                    ois .close();
                } catch (IOException e) {
                    logger.error("Error loading persistent file");
                    e.printStackTrace();
                }
            }
        }
    }

    public void save() {
        logger.debug("Saving " + file.getName());
        logger.debug(values);

        ObjectOutputStream oos = null;
        try{
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(values);
        } catch (Exception ex) {
            logger.error("Error saving persistent file");
            ex.printStackTrace();
        } finally {
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException ex) {
                    logger.error("Error saving persistent file");
                    ex.printStackTrace();
                }
            }
        }

    }

    public HashMap<String, Object> getFileObject() {
        return values;
    }

    public void put(String key, Object value) {
        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public String getString(String key) {
        return (String) values.get(key);
    }

    public int getInt(String key) {
        return (int) values.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) values.get(key);
    }

}
