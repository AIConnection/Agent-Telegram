package org.metabot.core.media;

import org.metabot.core.bdi.core.Content;

/**
 * Interface to transcript media into text.
 * Class extends this should select better transcript implementation using MIME type from {@link Media}.
 *
 * @see Media
 * https://github.com/spring-projects/spring-framework/blob/main/spring-web/src/main/resources/org/springframework/http/mime.types
 */
public interface MediaTranscription<V> {

    /**
     * Transcript media resource to text representation.
     *
     * @param media Resource data to transcript.
     * @return Resume transcript from input data.
     */
    Content<V, String> transcript(Media media);

}
