package server;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class ClientData {
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private boolean readingLength = true;
    private SelectionKey selectionKey; // для синхронизации интересов

    public ClientData() {
        this.readBuffer = ByteBuffer.allocate(4);
    }

    public synchronized ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public synchronized ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }

    public synchronized boolean advanceAfterRead() {
        if (readingLength && !readBuffer.hasRemaining()) {
            readBuffer.flip();
            int dataSize = readBuffer.getInt();
            readBuffer = ByteBuffer.allocate(dataSize);
            readingLength = false;
            return false;
        } else if (!readingLength && !readBuffer.hasRemaining()) {
            return true;
        }
        return false;
    }

    public synchronized byte[] getRequestData() {
        return readBuffer.array();
    }

    public synchronized void prepareWrite(byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length);
        buf.put(data);
        buf.flip();
        this.writeBuffer = buf;
    }

    public synchronized void resetForNextMessage() {
        readBuffer = ByteBuffer.allocate(4);
        readingLength = true;
    }

    public synchronized void setSelectionKey(SelectionKey key) {
        this.selectionKey = key;
    }
}