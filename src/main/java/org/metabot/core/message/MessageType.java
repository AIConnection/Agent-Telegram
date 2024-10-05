package org.metabot.core.message;

import java.util.Arrays;

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

    private MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MessageType fromValue(final String value) {
    	return Arrays.asList(MessageType.values())
    			.stream()
    			.filter(messageType->messageType.name().equalsIgnoreCase(value))
    			.findFirst()
    			.orElseThrow(()->new IllegalArgumentException("Invalid MessageType value: " + value));
    }

}
