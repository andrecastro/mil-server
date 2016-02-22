package br.edu.ifce.ppd.tria.server.socket;

import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.core.protocol.Connection;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by andrecoelho on 2/17/16.
 */
public class SocketConnection implements Connection {

    private volatile boolean connected;

    private Socket socket;

    private ConcurrentLinkedQueue<Action> socketQueue;
    private Route route;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private Thread readThread;
    private Thread writeThread;

    private SocketClient client;

    public SocketConnection(Socket socket, Route route) throws IOException {
        this.connected = true;
        this.route = route;
        this.socket = socket;
        this.socketQueue = new ConcurrentLinkedQueue<>();
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void start() {
        registerClient();
        startReading();
        startWriting();
    }

    public void send(Action action) {
        socketQueue.offer(action);
    }

    private void registerClient() {
        client = route.toRegistration(this);
        System.out.println("Connection established with: " + client.getId());
        try {
            outputStream.writeObject(client.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        System.out.println("Closing connection with " + client.getId());

        socketQueue.clear(); // clean all
        connected = false; // stop threads
        route.toDeregister(client); // deregister client

        // close everything
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    private void startReading() {
        if (readThread == null) {
            readThread = new Thread(() -> { while (connected) { handleRead(); } }, "Read - " + client.getId());
        }

        if (!readThread.isAlive()) {
            readThread.start();
        }
    }

    private void startWriting() {
        if (writeThread == null) {
            writeThread = new Thread(() -> { while (connected) { handleWrite(); } }, "Write - " + client.getId());
        }

        if (!writeThread.isAlive()) {
            writeThread.start();
        }
    }

    private void handleWrite() {
        try {
            while (!socketQueue.isEmpty()) {
                Action response = socketQueue.poll();

                System.out.println("Responding: " + response);

                outputStream.writeObject(response);
                outputStream.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRead() {
        try {
            Action requestAction = (Action) inputStream.readObject();

            System.out.println("Handling request: " + requestAction);

            if (isClose(requestAction)) {
                close();
                return;
            }

            Action responseAction = route.to(requestAction, client);
            socketQueue.offer(responseAction);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isClose(Action requestAction) {
        return requestAction.getPath().equals("register-service/close-game");
    }
}
