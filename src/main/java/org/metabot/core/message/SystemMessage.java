package org.metabot.core.message;

import lombok.Getter;
import lombok.ToString;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * A message of the type 'system' passed as input. The system message gives high level
 * instructions for the conversation. This role typically provides high-level instructions
 * for the conversation. For example, you might use a system message to instruct the
 * generative to behave like a certain character or to provide answers in a specific
 * format.
 */
@Getter
@ToString
public class SystemMessage extends AbstractMessage {

    public SystemMessage(String textContent) {
        super(MessageType.SYSTEM, textContent, Map.of());
    }

    public SystemMessage(Resource resource) {
        super(MessageType.SYSTEM, resource, Map.of());
    }

}
