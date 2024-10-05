package org.metabot.core.bdi.domain;

import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;

@Getter
public class Action extends BDIParent {

    public Action(final String id) {
        this(id, null);
    }

    public Action(final String id, final String description) {
        super(id, Type.ACTION, Content.of(description));
    }

    @Override
    public String toString() {
        return "-" + this.getId();
    }
}
