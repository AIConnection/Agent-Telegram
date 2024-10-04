package org.metabot.core.bdi.core;

public interface BDI<I> {
    enum Type {
        BELIEF,
        DESIRE,
        INTENTION,
        ACTION, STATE, TRANSITION, NLP_TASK, PLAN
    }

    I getId();
    Type getType();
}
