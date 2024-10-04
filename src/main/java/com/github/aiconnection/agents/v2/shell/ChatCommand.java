package com.github.aiconnection.agents.v2.shell;

import lombok.extern.slf4j.Slf4j;
import org.metabot.core.bdi.core.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.Assert;

@Slf4j
@ShellComponent
public class ChatCommand {

    private final Agent agent;

    @Autowired
    public ChatCommand(@Qualifier("shellAgent") Agent agent) {
        super();
        Assert.notNull(agent, "Agent must not be null");
        this.agent = agent;
    }

    @ShellMethod(group = "IA COMMANDS", value = "Open chat with agent IA")
    public String chat(@ShellOption String message) {
        try {
            return this.agent.process(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }

}
