package io.github.polarizedions.networking;

import io.github.polarizedions.IrcParser.Handlers;
import io.github.polarizedions.IrcParser.Numerics;
import io.github.polarizedions.IrcParser.ParsedLine;
import io.github.polarizedions.Logger;
import io.github.polarizedions.config.NetworkConfig;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
public class NetworkManager {
    public static final NetworkManager instance = new NetworkManager();
    private final int THREAD_ITERS_PER_SECOND = 10;
    private ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<>();
    private Logger logger = Logger.getLogger("NetworkManager");
    private boolean shouldRun = true;

    private Thread recvThread = new Thread(null, () -> {
        logger.debug("recvThread starting");
        long iterPause = (long) (1d / THREAD_ITERS_PER_SECOND * 1000d);
        while (shouldRun) {
            for (Network nw : getNetworks()) {
                nw.recvToBuffer();
            }
            try {
                Thread.sleep(iterPause);
            } catch (InterruptedException e) {
                break;
            }
        }
    }, "RecvThread");

    private Thread sendThread = new Thread(null, () -> {
        logger.debug("sendThread starting");
        long iterPause = (long) (1d / THREAD_ITERS_PER_SECOND * 1000d);
        while (shouldRun) {
            for (Network nw : getNetworks()) {
                nw.sendFromBuffer();
            }
            try {
                Thread.sleep(iterPause);
            } catch (InterruptedException e) {
                break;
            }
        }
    }, "SendThread");

    private Thread parseThread = new Thread(null, () -> {
        logger.debug("parseThread starting");
        long iterPause = (long) (1d / THREAD_ITERS_PER_SECOND * 1000d);
        while (shouldRun) {
            for (Network nw : getNetworks()) {
                String line = nw.recv();
                if (line == null) {
                    continue;
                }
                ParsedLine parsedLine = nw.parser.parseLine(line);
                logger.debug("C < S | [" + Numerics.getMappedName(parsedLine.command) + "] | " + line);
                Handlers.handle(parsedLine);
            }
            try {
                Thread.sleep(iterPause);
            } catch (InterruptedException e) {
                break;
            }
        }
    }, "ParseThread");

    private NetworkManager() {
        logger.debug("Networkmanager starting");
        recvThread.start();
        sendThread.start();
        parseThread.start();
    }

    public Collection<Network> getNetworks() {
        return networks.values();
    }

    public Network addNetwork(NetworkConfig nwConfig) {
        logger.debug("Adding network " + nwConfig.host + ":" + nwConfig.port);
        Network nw = new Network(nwConfig);
        networks.put(nw.getName(), nw);

        return nw;
    }

    public void connectAll() {
        for (Network nw : getNetworks()) {
            nw.connect();
        }
    }

    public void disconnectAll() {
        for (Network nw : getNetworks()) {
            nw.disconnect();
        }
    }

    public void shutdown() {
        for (Network nw : getNetworks()) {
            nw.disconnect();
        }
        try {
            Thread.sleep((long) (1d / THREAD_ITERS_PER_SECOND * 1000d) * 2L );
        } catch (InterruptedException e) {
        }

        shouldRun = false;
        try {
            recvThread.join();
            sendThread.join();
            parseThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Network getNetwork(String name) {
        return networks.get(name);
    }
}
