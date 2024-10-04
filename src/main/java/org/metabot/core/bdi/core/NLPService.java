package org.metabot.core.bdi.core;

/**
 * Interface represents NLP methods like process text, resume or moderate.
 */
public interface NLPService {

    String process(final String input);

    String resume(final String input);

    boolean moderation(final String input);

}
