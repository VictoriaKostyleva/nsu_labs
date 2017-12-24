package message;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MessageResponse extends Message {
    @JsonProperty("id")
    private int id;
    @JsonProperty("message")
    private String message;

    @JsonCreator
    public MessageResponse(@JsonProperty("id") int id, @JsonProperty("message") String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
