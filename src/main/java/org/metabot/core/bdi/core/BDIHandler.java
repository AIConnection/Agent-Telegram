package org.metabot.core.bdi.core;

@FunctionalInterface
public interface BDIHandler<I, O> {

    O perform(I in);

}
