package org.metabot.core.bdi.domain;

import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;

@Getter
public class Action extends BDIParent {

    public Action(String id) {
        this(id, null);
    }

    public Action(String id, String description) {
        super(id, Type.ACTION, Content.of(description));
    }

    @Override
    public String toString() {
        return "-" + this.getId();
    }
}
