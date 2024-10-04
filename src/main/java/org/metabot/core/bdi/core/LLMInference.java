package org.metabot.core.bdi.core;

import org.metabot.core.message.Message;

import java.util.Collection;
import java.util.List;

public interface LLMInference {
    String complete(String systemPrompt, String prompt);
    String complete(String systemPrompt, Collection<String> prompts);
    String complete(final String prompt);
    String complete(final Message... messages);
}
