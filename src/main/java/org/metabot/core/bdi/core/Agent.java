package org.metabot.core.bdi.core;

import java.net.URL;

public interface Agent {
    String process(final URL url);

    String process(final byte[] bytes);

    String process(final String text);

    String process(final URL url, final String historyId);

    String process(final byte[] bytes, final String historyId);

    String process(final String text, final String historyId);
}
