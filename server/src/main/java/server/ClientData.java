package server;

import java.nio.ByteBuffer;

public class ClientData {
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private boolean readingLength = true;

    public ClientData() {
        this.readBuffer = ByteBuffer.allocate(4);
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }
    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }

    public boolean advanceAfterRead() {
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

    public byte[] getRequestData() {
        return readBuffer.array();
    }

    public void prepareWrite(byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(4 + data.length);
        buf.putInt(data.length);
        buf.put(data);
        buf.flip();
        this.writeBuffer = buf;
    }

    public void resetForNextMessage() {
        readBuffer = ByteBuffer.allocate(4);
        readingLength = true;
    }

}