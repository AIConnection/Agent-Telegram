package org.metabot.core.bdi.core;

import java.util.Collection;

import org.metabot.core.message.Message;

public interface LLMInference {
    String complete(final String systemPrompt, final String prompt);
    String complete(final String systemPrompt, final Collection<String> prompts);
    String complete(final String prompt);
    String complete(final Message... messages);
}
