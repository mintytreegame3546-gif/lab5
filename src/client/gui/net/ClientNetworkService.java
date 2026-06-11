package client.gui.net;

import data.Organization;
import network.CommandHistoryEntry;
import network.CommandRequest;
import network.CommandResponse;
import network.Credentials;
import network.SerializationUtils;

import javax.swing.SwingUtilities;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientNetworkService implements Closeable {
    public interface CollectionUpdateListener {
        void collectionUpdated(List<Organization> organizations);
    }

    private static final int RETRIES = 3;
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(2);

    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;
    private final BlockingQueue<CommandResponse> responses = new LinkedBlockingQueue<>();
    private final List<CollectionUpdateListener> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;
    private Credentials credentials = Credentials.empty();

    public ClientNetworkService(String host, int port) throws Exception {
        this.serverAddress = new InetSocketAddress(host, port);
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(true);
        startListener();
    }

    public String username() {
        return credentials.username();
    }

    public CommandResponse login(String username, String password) throws Exception {
        Credentials requestCredentials = Credentials.of(username, password);
        CommandResponse response = send(new CommandRequest("login", new String[0], null, Map.of(), requestCredentials));
        if (response.isSuccess()) credentials = requestCredentials;
        return response;
    }

    public CommandResponse register(String username, String password) throws Exception {
        Credentials requestCredentials = Credentials.of(username, password);
        CommandResponse response = send(new CommandRequest("register", new String[0], null, Map.of(), requestCredentials));
        if (response.isSuccess()) credentials = requestCredentials;
        return response;
    }

    public CommandResponse refresh() throws Exception {
        return execute("show", new String[0], null);
    }

    public CommandResponse add(Organization organization) throws Exception {
        return execute("add", new String[0], organization);
    }

    public CommandResponse update(long id, Organization organization) throws Exception {
        return execute("update", new String[] {String.valueOf(id)}, organization);
    }

    public CommandResponse remove(long id) throws Exception {
        return execute("remove_by_id", new String[] {String.valueOf(id)}, null);
    }

    public List<CommandHistoryEntry> history() throws Exception {
        return execute("history", new String[0], null).getHistory();
    }

    public CommandResponse executeRaw(String line) throws Exception {
        return executeRawWithOrganization(line, null);
    }

    public CommandResponse executeRawWithOrganization(String line, Organization organization) throws Exception {
        String trimmed = line == null ? "" : line.trim();
        if (trimmed.isEmpty()) return new CommandResponse(false, "Error: empty command");
        String[] tokens = trimmed.split("\\s+");
        String[] args = java.util.Arrays.copyOfRange(tokens, 1, tokens.length);
        return execute(tokens[0], args, organization);
    }

    public void addCollectionUpdateListener(CollectionUpdateListener listener) {
        listeners.add(listener);
    }

    private CommandResponse execute(String command, String[] args, Organization organization) throws Exception {
        return send(new CommandRequest(command, args, organization, Map.of(), credentials));
    }

    private CommandResponse send(CommandRequest request) throws Exception {
        byte[] data = SerializationUtils.serialize(request);
        for (int attempt = 0; attempt < RETRIES; attempt++) {
            channel.send(ByteBuffer.wrap(data), serverAddress);
            CommandResponse response = responses.poll(RESPONSE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (response != null) return response;
        }
        return new CommandResponse(false, "Error: server is not responding");
    }

    private void startListener() {
        Thread listener = new Thread(this::listen, "gui-client-listener");
        listener.setDaemon(true);
        listener.start();
    }

    private void listen() {
        ByteBuffer buffer = ByteBuffer.allocate(SerializationUtils.BUFFER_SIZE);
        while (running) {
            try {
                buffer.clear();
                channel.receive(buffer);
                Optional<CommandResponse> response = readResponse(buffer);
                response.ifPresent(this::dispatch);
            } catch (Exception e) {
                if (running) responses.offer(new CommandResponse(false, "Error: " + e.getMessage()));
            }
        }
    }

    private Optional<CommandResponse> readResponse(ByteBuffer buffer) throws Exception {
        int length = buffer.position();
        if (length == 0) return Optional.empty();
        Object object = SerializationUtils.deserialize(buffer.array(), length);
        if (object instanceof CommandResponse response) return Optional.of(response);
        return Optional.empty();
    }

    private void dispatch(CommandResponse response) {
        if (response.isPush()) {
            SwingUtilities.invokeLater(() -> {
                for (CollectionUpdateListener listener : listeners) listener.collectionUpdated(response.getOrganizations());
            });
        } else {
            responses.offer(response);
        }
    }

    @Override
    public void close() {
        running = false;
        try {
            channel.close();
        } catch (Exception ignored) {
        }
    }
}
