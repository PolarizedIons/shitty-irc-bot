package io.github.polarizedions.networking;


import io.github.polarizedions.IrcParser.Handlers;
import io.github.polarizedions.IrcParser.IrcParser;
import io.github.polarizedions.IrcParser.Numerics;
import io.github.polarizedions.IrcParser.ParsedLine;
import io.github.polarizedions.Logger;
import io.github.polarizedions.config.NetworkConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
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
public class Network {
    private final int MESSAGE_RATELIMIT = 5;
    private final int SOCKET_READ_BUFFER_SIZE = 1024;
    private NetworkConfig networkConfig;
    private Socket socket;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private ConcurrentLinkedQueue<String> inBuffer = new ConcurrentLinkedQueue<>();
    private String inTemp = "";
    private ConcurrentLinkedQueue<String> outBuffer = new ConcurrentLinkedQueue<>();
    private Logger logger;
    private IrcParser parser;
    private NetworkCapabilities networkCapabilities;

    public Network(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
        logger = Logger.getLogger(String.format("Network(%s)", networkConfig.host));
        parser = new IrcParser(this);


    }

    public void connect() {
        networkCapabilities = new NetworkCapabilities();

        try {
            socket = new Socket(networkConfig.host, networkConfig.port);
            out = new BufferedOutputStream(socket.getOutputStream());
            in = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.error("Error connecting to " + this.toString());
            e.printStackTrace();
        }

        send("CAP LS 302");
        send("NICK " + networkConfig.nick);
        send("USER " + networkConfig.gecos + " 0 * :" + networkConfig.realname);
    }

    public void disconnect() {
        try {
            out.write("QUIT :Bye!".getBytes());
            out.flush();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error("Error closing socket of " + this.toString());
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && in != null && out != null && socket.isConnected();
    }

    public void send(String text) {
        outBuffer.add(text);
    }

//    public String recv() {
//        return inBuffer.poll(); // Remove the head of the queue & return, or return null if queue is empty
//    }

    protected void sendFromBuffer() {
        if (!isConnected()) {
            return;
        }
        long msgDelay = 1L / MESSAGE_RATELIMIT * 1000L;
        int queueSize = outBuffer.size(); // This is just to make sure we don't just keep sending more data as more is added, creating a possible large loop
        for (int i = 0; i < queueSize; i++) {
            String text = outBuffer.remove();
            try {
                out.write((text + "\r\n").getBytes());
                out.flush();
                logger.debug("C > S | " + text);
                Thread.sleep(msgDelay);
            } catch (IOException e) {
                logger.error("Error sending '" + text + "' to " + this.toString());
                e.printStackTrace();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while sending '" + text + "' to " + this.toString());
                e.printStackTrace();
            }
        }
    }

    protected void recvToBuffer() {
        if (!isConnected()) {
            return;
        }
        try {
            if (in.available() > 0) {
                byte[] tmp = new byte[SOCKET_READ_BUFFER_SIZE];
                int bytesRead = in.read(tmp, 0, SOCKET_READ_BUFFER_SIZE);
                if (bytesRead < 0) {
                    return;
                }
                String line = inTemp + new String(tmp, 0, bytesRead);
                String[] splitLine = line.replace("\r", "").split("\n");
                boolean completeLine = line.endsWith("\n");
                int loopIters = splitLine.length - (completeLine ? 0 : 1);
                for (int i = 0; i < loopIters; i++) {


                    // TODO: temp
                    ParsedLine parsedLine = parser.parseLine(splitLine[i]);
                    logger.debug("C < S | [" + Numerics.getMappedName(parsedLine.command) + "] | " + splitLine[i]);
                    Handlers.handle(parsedLine);

//                    inBuffer.add(splitLine[i]);
                }

                inTemp = completeLine ? "" : splitLine[splitLine.length - 1];
            }
        } catch (IOException e) {
            logger.error("Error reading from socket of " + this.toString());
            e.printStackTrace();
        }
    }

    public NetworkConfig getNetworkConfig() {
        return networkConfig;
    }

    public NetworkCapabilities getNetworkCapabilities() {
        return networkCapabilities;
    }

    @Override
    public String toString() {
        return "Network<host: '" + networkConfig.host + "', port: " + networkConfig.port + ">";
    }
}
