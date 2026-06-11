package server;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ConnectionReceiver {
    private final Set<InetSocketAddress> clients = new HashSet<>();
    private final Logger logger;

    public ConnectionReceiver(Logger logger) {
        this.logger = logger;
    }

    public synchronized String register(DatagramPacket packet) {
        InetSocketAddress address = new InetSocketAddress(packet.getAddress(), packet.getPort());
        if (clients.add(address)) logger.info("New connection from " + address);
        return address.toString();
    }

    public synchronized Set<InetSocketAddress> clients() {
        return new HashSet<>(clients);
    }
}
