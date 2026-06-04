package connection;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private String[] text;

    public Response(String[] text) {
        this.text = text;
    }

    public String[] getText() {
        return text;
    }

    @Override
    public String toString() {
        return "lab.common.Message{" + "text='" + text + "'}";
    }
}
