package org.metabot.core.bdi.domain;

import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;
import org.springframework.util.StringUtils;

public class Belief extends BDIParent {

    public Belief(String id) {
        this(id, null);
    }

    public Belief(String id, String description) {
        super(id, Type.BELIEF, Content.of(description));
    }

    @Override
    public String toString() {
        return ">" + this.getId() + (StringUtils.hasText(this.getDescription().getValue()) ? "" : ": "
                + getDescription().getValue());
    }
}
