package org.metabot.core.message;

import lombok.Getter;
import lombok.ToString;
import org.metabot.core.media.Media;
import org.metabot.core.media.MediaContent;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.*;

/**
 * A message of the type 'user' passed as input Messages with the user role are from the
 * end-user or developer. They represent questions, prompts, or any input that you want
 * the generative to respond to.
 */
@Getter
@ToString
public class UserMessage extends AbstractMessage implements MediaContent {

    protected final List<Media> media;

    public UserMessage(final String textContent) {
        this(MessageType.USER, textContent, new ArrayList<>(), Map.of());
    }

    public UserMessage(final Resource resource) {
        super(MessageType.USER, resource, Map.of());
        this.media = new ArrayList<>();
    }

    public UserMessage(final String textContent, final Media... media) {
        this(textContent, Arrays.asList(media));
    }

    public UserMessage(final String textContent, final List<Media> media) {
        this(MessageType.USER, textContent, media, Map.of());
    }

    public UserMessage(final String textContent, final Collection<Media> mediaList, final Map<String, Object> metadata) {
        this(MessageType.USER, textContent, mediaList, metadata);
    }

    public UserMessage(final MessageType messageType, final String textContent, final Collection<Media> media,
    		final Map<String, Object> metadata) {
        super(messageType, textContent, metadata);
        Assert.notNull(media, "media data must not be null");
        this.media = new ArrayList<>(media);
    }

    @Override
    public Collection<Media> getMedia() {
        return this.media;
    }

}
