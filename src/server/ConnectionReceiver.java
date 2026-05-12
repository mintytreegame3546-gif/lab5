package server;

import java.net.DatagramPacket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ConnectionReceiver {
    private final Set<String> clients = new HashSet<>();
    private final Logger logger;

    public ConnectionReceiver(Logger logger) {
        this.logger = logger;
    }

    public String register(DatagramPacket packet) {
        String client = packet.getAddress().getHostAddress() + ":" + packet.getPort();
        if (clients.add(client)) logger.info("New connection from " + client);
        return client;
    }
}
