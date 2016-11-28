package org.openremote.controller.rules;

import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.event.EventProcessor;
import org.openremote.controller.deploy.CommandDefinition;
import org.openremote.controller.model.Deployment;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Can be used directly and/or by {@link EventProcessor}s to trigger
 * {@link ExecutableCommand}s (e.g. in rules or from simple client call).
 */
public class CommandFacade {

    private static final Logger LOG = Logger.getLogger(CommandFacade.class.getName());

    final protected Deployment deployment;

    public CommandFacade(Deployment deployment) {
        this.deployment = deployment;
    }

    public void command(String commandName) {
        command(commandName, null);
    }

    public void command(String commandName, int arg) {
        command(commandName, Integer.toString(arg));
    }

    public void command(String commandName, String arg) {
        CommandDefinition commandDefinition = deployment.getCommandDefinition(commandName);
        if (commandDefinition == null) {
            LOG.warning("Command definition not found, ignoring execution: " + commandName);
            return;
        }
        execute(commandDefinition, arg);
    }

    /**
     * Executes a command. This normally means a device protocol is used to communicate with
     * some physical device.
     */
    public void execute(CommandDefinition commandDefinition) {
        execute(commandDefinition, null);
    }

    /**
     * Build a {@link Command} from this definition and, if possible, execute it with the given argument.
     *
     * @param arg command parameter value
     */
    public void execute(CommandDefinition commandDefinition, String arg) {
        Command command;
        try {
            command = deployment.getCommandBuilder().build(commandDefinition);
            if (command == null) {
                LOG.log(Level.WARNING,
                    "No command was produced (does the protocol have an ExecutableCommand?): " + commandDefinition
                );
            } else if (command instanceof ExecutableCommand) {
                LOG.fine("Executing command '" + command + "' with: " + arg);
                ExecutableCommand executableCommand = (ExecutableCommand) command;
                try {
                    executableCommand.send(arg);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error executing command '" + command + "' with: " + arg, ex);
                }
            } else {
                LOG.log(Level.WARNING, "Ignoring, not an ExecutableCommand: " + command);
            }
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, "Error building command: " + commandDefinition, t);
        }
    }
}

