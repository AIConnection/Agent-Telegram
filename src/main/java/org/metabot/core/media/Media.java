package org.metabot.core.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

import java.net.URL;

/**
 * The Media class represents the data and metadata of a media attachment in a message.
 * It consists of a MIME type and the raw data.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Media {

    private final MimeType type;
    private final Object data;

    private Media(final MimeType type, final Object data) {
        super();
        this.type = type;
        this.data = data;
    }

    public Media(final MimeType type, final URL data) {
        this(type, (Object) data);
    }

    public Media(final MimeType type, final Resource data) {
        this(type, (Object) data);
    }

}
