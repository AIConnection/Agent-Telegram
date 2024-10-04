package org.metabot.core.bdi.domain;

import lombok.Getter;
import org.metabot.core.bdi.core.BDIParent;
import org.metabot.core.bdi.core.Content;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Intention extends BDIParent {
    private final Desire desire;
    private final List<Plan> plans;

    public Intention(String id, Desire desire, Plan... plans) {
        this(id, null, desire, plans);
    }

    public Intention(String id, String description, Desire desire, Plan... plans) {
        super(id, Type.INTENTION, Content.of(description));
        Assert.notNull(desire, "Desire must not be null");
        this.desire = desire;
        this.plans = Optional.ofNullable(plans)
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "DESIRE: " + desire +
                plans.stream()
                        .map(plan -> "-> PLAN: " + plan)
                        .collect(Collectors.joining("\n"));
    }
}
