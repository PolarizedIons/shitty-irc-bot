package io.github.stepie22.networking;


import io.github.stepie22.IrcParser.Handlers;
import io.github.stepie22.IrcParser.IrcParser;
import io.github.stepie22.IrcParser.Numerics;
import io.github.stepie22.IrcParser.ParsedLine;
import io.github.stepie22.Logger;
import io.github.stepie22.config.Config;
import io.github.stepie22.config.NetworkConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        networkCapabilities = new NetworkCapabilities();

        // TODO: this is temp
        networkCapabilities.requestCap("userhost-in-names");
        networkCapabilities.requestCap("multi-prefix");
        networkCapabilities.requestCap("nonexistant");

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
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            socket.close();
        } catch (IOException e) {
            logger.error("Error closing socket of " + this.toString());
            e.printStackTrace();
        }
    }

    public void send(String text) {
        outBuffer.add(text);
    }

//    public String recv() {
//        return inBuffer.poll(); // Remove the head of the queue & return, or return null if queue is empty
//    }

    protected void sendFromBuffer() {
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

                    ParsedLine parsedLine = parser.parseLine(splitLine[i]);
                    logger.debug("C < S | [" + Numerics.getMappedName(parsedLine.command) + "] | " + splitLine[i]);
                    Handlers.handle(parsedLine);

//                    inBuffer.add(splitLine[i]);
//                    if (splitLine[i].startsWith("PING ")) {
//                        send(splitLine[i].replace("PING", "PONG"));
//                    }
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
