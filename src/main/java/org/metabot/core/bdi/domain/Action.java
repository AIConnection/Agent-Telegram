package org.metabot.core.bdi.domain;

import lombok.Getter;
import org.metabot.core.bdi.core.BDIHandler;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;

import java.util.List;
import java.util.Optional;

@Getter
public class Action<I extends Content<?, ?>, O extends Content<?, ?>> extends BDIParent {

    private final List<BDIHandler<I, O>> handlers;

    public Action(String id) {
        this(id, "");
    }

    public Action(String id, String description) {
        //noinspection unchecked
        this(id, description, List.of(in -> (O) in));
    }

    public Action(String id, List<BDIHandler<I, O>> handlers) {
        this(id, null, handlers);
    }

    public Action(String id, String description, List<BDIHandler<I, O>> handlers) {
        super(id, Type.ACTION, Content.of(description));
        this.handlers = Optional.ofNullable(handlers).orElse(List.of());
    }

    @Override
    public String toString() {
        return "-" + this.getId();
    }
}
