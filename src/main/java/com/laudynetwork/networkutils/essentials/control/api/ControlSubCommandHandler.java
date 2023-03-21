package com.laudynetwork.networkutils.essentials.control.api;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlSubCommandHandler {

    @Getter
    private final Map<String, ControlSubCommand> subCommands = new HashMap<>();

    public ControlSubCommandHandler() {

        // register sub commands

    }

    public List<String> getSubCommandIDs() {
        return subCommands.keySet().stream().toList();
    }

    private void registerSubCommand(ControlSubCommand subCommand) {
        subCommands.put(subCommand.id(), subCommand);
    }

}
