package org.metabot.core.message;

import org.metabot.core.bdi.core.Content;

/**
 * The Message interface represents a message that can be sent or received in an ia application.
 * Messages can have text, metadata and message type.
 */
public interface Message extends Content<String, Object> {

    MessageType getMessageType();

}
