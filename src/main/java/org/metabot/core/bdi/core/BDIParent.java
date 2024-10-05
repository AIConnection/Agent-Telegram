package org.metabot.core.bdi.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Getter
@EqualsAndHashCode(of = {"type", "id"})
public abstract class BDIParent implements BDI<String> {

    private final String id;
    private final Content<String, String> description;

    private final Type type;

    public BDIParent(final String id, final Type type) {
        this(id, type, null);
    }

    public BDIParent(final String id, final Type type, final Content<String, String> description) {
        Assert.notNull(id, "ID must not be null");
        Assert.notNull(type, "Type must not be null");
        this.id = id;
        this.type = type;
        this.description = description;
    }

    @Override
    public String toString() {
        return type + "[" + id + "]" + (StringUtils.hasText(description.getValue()) ? "" : ": " + description.getValue());
    }
}
