package org.metabot.core.message;

/**
 * The MessageType enum represents the type of message in an ia application.
 */
public enum MessageType {

    /**
     * A message of the type 'user' passed as input Messages with the user role are from
     * the end-user or developer.
     * @see UserMessage
     */
    USER("user"),

    /**
     * A message of the type 'assistant' passed as input Messages with the message is
     * generated as a response to the user.
     * @see AssistantMessage
     */
    ASSISTANT("assistant"),

    /**
     * A message of the type 'system' passed as input Messages with high level
     * instructions for the conversation, such as behave like a certain character or
     * provide answers in a specific format.
     * @see SystemMessage
     */
    SYSTEM("system"),

    /**
     * A message of the type 'function' passed as input Messages with a function content
     * in an ia application.
     * @see ToolMessage
     */
    TOOL("tool");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MessageType fromValue(String value) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getValue().equals(value)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Invalid MessageType value: " + value);
    }

}
