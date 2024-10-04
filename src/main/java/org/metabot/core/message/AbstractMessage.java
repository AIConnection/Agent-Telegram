package org.metabot.core.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * The AbstractMessage class is an abstract implementation of the Message interface. It
 * provides a base implementation for message content, media attachments, metadata, and
 * message type.
 *
 * @see Message
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class AbstractMessage implements Message {

    public static final String MESSAGE_TYPE = "messageType";

    protected final MessageType messageType;

    protected final String value;

    /**
     * Additional options for the message to influence the response, not a generative map.
     */
    protected final Map<String, Object> metadata;

    protected AbstractMessage(MessageType messageType, String value, Map<String, Object> metadata) {
        Assert.notNull(messageType, "Message type must not be null");

        if (messageType == MessageType.SYSTEM || messageType == MessageType.USER) {
            Assert.notNull(value, "Content must not be null for SYSTEM or USER messages");
        }

        this.messageType = messageType;
        this.value = value;
        this.metadata = new HashMap<>(metadata);
        this.metadata.put(MESSAGE_TYPE, messageType);
    }

    protected AbstractMessage(MessageType messageType, Resource resource, Map<String, Object> metadata) {
        Assert.notNull(resource, "Resource must not be null");
        try (InputStream inputStream = resource.getInputStream()) {
            this.value = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read resource", ex);
        }
        this.messageType = messageType;
        this.metadata = new HashMap<>(metadata);
        this.metadata.put(MESSAGE_TYPE, messageType);
    }

}
