package server;

import network.CommandResponse;
import network.SerializationUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ResponseSender {
    public void send(DatagramSocket socket, DatagramPacket requestPacket, CommandResponse response) throws Exception {
        byte[] responseBytes = SerializationUtils.serialize(response);
        DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length,
                requestPacket.getAddress(), requestPacket.getPort());
        synchronized (socket) {
            socket.send(responsePacket);
        }
    }
}
