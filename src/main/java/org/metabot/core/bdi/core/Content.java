package org.metabot.core.bdi.core;

import java.util.Map;

/**
 * Content represents one value(V) and your metadata by key(String)/value(M) hash.
 * @param <V> Type of value.
 * @param <M> Type of metadata value.
 */
public interface Content<V, M> {

    /**
     * Get content value.
     *
     * @return Value of this content.
     */
    V getValue();

    /**
     * Key(String)/Value(M) in memory metadata to this content.
     * return Get the metadata associated with the content.
     */
    Map<String, M> getMetadata();

    /**
     * Creates content by type {@link String} without metadata.
     * @param value String value of content.
     * @return Return new instance of Content<String, String>
     */
    static Content<String, String> of(final String value) {
        return new Content<>() {
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public Map<String, String> getMetadata() {
                return Map.of();
            }

            @Override
            public String toString() {
                return value == null ? "" : value;
            }
        };
    }

}
