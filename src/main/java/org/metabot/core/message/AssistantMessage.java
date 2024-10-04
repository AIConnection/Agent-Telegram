package org.metabot.core.message;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Lets the generative know the content was generated as a response to the user. This role
 * indicates messages that the generative has previously generated in the conversation. By
 * including assistant messages in the series, you provide context to the generative about
 * prior exchanges in the conversation.
 */
@Getter
@ToString
public class AssistantMessage extends AbstractMessage {

    public record ToolCall(String id, String type, String name, String arguments) {
    }

    private final List<ToolCall> toolCalls;

    public AssistantMessage(String content) {
        this(content, Map.of());
    }

    public AssistantMessage(String content, Map<String, Object> properties) {
        this(content, properties, List.of());
    }

    public AssistantMessage(String content, Map<String, Object> properties, List<ToolCall> toolCalls) {
        super(MessageType.ASSISTANT, content, properties);
        Assert.notNull(toolCalls, "Tool calls must not be null");
        this.toolCalls = toolCalls;
    }

}
