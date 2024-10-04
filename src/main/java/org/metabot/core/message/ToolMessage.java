package org.metabot.core.message;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * The class represents a message with a function content in an ia application.
 */
@Getter
@ToString
public class ToolMessage extends AbstractMessage {

    public record Tool(String id, String name, String responseData) {
    }

    protected final List<Tool> responses;

    public ToolMessage(List<Tool> responses, Map<String, Object> metadata) {
        super(MessageType.TOOL, "", metadata);
        this.responses = responses;
    }

    public ToolMessage(List<Tool> responses) {
        this(responses, Map.of());
    }

}