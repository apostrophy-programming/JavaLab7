package server;

import command.ServerCommand;
import connection.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Server {
    private final int port;
    private Selector selector;
    private final RealCollectionManager realCollectionManager;
    private volatile boolean running = true;
    private Thread consoleThread;
    private final ExecutorService readPool = Executors.newCachedThreadPool();
    private final ForkJoinPool processingPool = new ForkJoinPool();
    private final ForkJoinPool sendPool = new ForkJoinPool();

    public Server(int port, String dbUrl, String dbUser, String dbPassword) {
        this.port = port;
        realCollectionManager = new RealCollectionManager(dbUrl, dbUser, dbPassword);
    }

    public void start() throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Сервер запущен на порту: " + port);

        startConsoleHandler();

        while (running) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;
                if (key.isAcceptable()) {
                    doAccept(key);
                } else if (key.isReadable()) {
                    doRead(key);
                } else if (key.isWritable()) {
                    doWrite(key);
                }
            }
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        if (clientChannel != null) {
            System.out.println("Принято подключение от: " + clientChannel.getRemoteAddress());
            clientChannel.configureBlocking(false);
            ClientData clientData = new ClientData();
            clientData.setSelectionKey(key);
            clientChannel.register(selector, SelectionKey.OP_READ, clientData);
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientData clientData = (ClientData) key.attachment();
        ByteBuffer buf = clientData.getReadBuffer();

        int bytesRead = sc.read(buf);
        if (bytesRead == -1) {
            System.out.println("Соединение закрыто: " + sc.getRemoteAddress());
            key.cancel();
            sc.close();
            return;
        }

        if (clientData.advanceAfterRead()) {
            byte[] commandData = clientData.getRequestData().clone();
            clientData.resetForNextMessage();
            readPool.submit(() -> processRequest(commandData, sc, clientData, key));
        }
    }

    private void processRequest(byte[] commandData, SocketChannel sc, ClientData clientData, SelectionKey key) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(commandData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            ServerCommand command = (ServerCommand) ois.readObject();
            System.out.println("Получена команда: " + command.getName());

            processingPool.submit(() -> {
                Response response;
                try {
                    response = command.execute(realCollectionManager);
                } catch (Exception e) {
                    response = new Response(new String[]{"Ошибка выполнения команды: " + e.getMessage()});
                }

                Response finalResponse = response;
                sendPool.submit(() -> sendResponse(finalResponse, sc, clientData, key));
            });
        } catch (Exception e) {
            System.err.println("Ошибка при десериализации команды: " + e.getMessage());
            Response errorResponse = new Response(new String[]{"Ошибка десериализации команды"});
            sendPool.submit(() -> sendResponse(errorResponse, sc, clientData, key));
        }
    }

    private void sendResponse(Response response, SocketChannel sc, ClientData clientData, SelectionKey key) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(response);
            oos.flush();
            byte[] responseData = baos.toByteArray();

            clientData.prepareWrite(responseData);

            synchronized (key) {
                if (key.isValid()) {
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    selector.wakeup();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка подготовки ответа для " + sc + ": " + e.getMessage());
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientData data = (ClientData) key.attachment();
        ByteBuffer buf = data.getWriteBuffer();

        if (buf != null && buf.hasRemaining()) {
            sc.write(buf);
        }
        if (buf != null && !buf.hasRemaining()) {
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public void exit() {
        if (!running) return;
        running = false;
        System.out.println("Завершение работы сервера...");

        readPool.shutdown();
        processingPool.shutdown();
        sendPool.shutdown();

        if (selector != null && selector.isOpen()) {
            selector.wakeup();
            try {
                selector.close();
            } catch (IOException e) {
                System.err.println("Ошибка закрытия selector: " + e.getMessage());
            }
        }
        if (consoleThread != null && consoleThread.isAlive()) {
            consoleThread.interrupt();
        }
        System.exit(0);
    }

    private void startConsoleHandler() {
        consoleThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while (running) {
                    line = reader.readLine();
                    if (line == null) break;
                    line = line.trim().toLowerCase();
                    if ("exit".equals(line)) {
                        System.out.println("Получена команда завершения из консоли.");
                        exit();
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка чтения консоли: " + e.getMessage());
            }
        });
        consoleThread.setDaemon(false);
        consoleThread.start();
    }
}