package org.metabot.core.bdi.domain.nlp;

import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;
import org.springframework.util.StringUtils;

public class Task extends BDIParent {

    public Task(String id, String description) {
        super(id, Type.NLP_TASK, Content.of(description));
    }

    @Override
    public String toString() {
        return "- " + this.getId() + (StringUtils.hasText(this.getDescription().getValue()) ? ": "
                + this.getDescription().getValue() : "");
    }
}
