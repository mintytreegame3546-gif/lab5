package server;

import network.CommandRequest;
import network.SerializationUtils;

import java.net.DatagramPacket;

public class RequestReader {
    public CommandRequest read(DatagramPacket packet) throws Exception {
        Object object = SerializationUtils.deserialize(packet.getData(), packet.getLength());
        if (!(object instanceof CommandRequest request)) throw new IllegalArgumentException("invalid request object");
        return request;
    }
}
