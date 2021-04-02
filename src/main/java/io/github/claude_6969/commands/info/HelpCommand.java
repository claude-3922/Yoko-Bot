package io.github.claude_6969.commands.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import io.github.claude_6969.Colors;
import io.github.claude_6969.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.Arrays;
import java.util.List;

public class HelpCommand extends Command {

    private final EventWaiter waiter;
    private final Paginator.Builder builder;

    public HelpCommand(EventWaiter _waiter) {
        this.name = "help";
        this.aliases = new String[] {"commands", "allcommands", "listcommands", "ls"};
        this.guildOnly = false;
        this.help = "Shows you this message.";
        this.arguments = "none";
        this.waiter = _waiter;
        this.builder = new Paginator.Builder()
                .setEventWaiter(waiter)
                .setColor(Colors.Blue())
                .allowTextInput(false)
                .setColumns(1)
                .setFinalAction(n -> {
                    try { n.clearReactions().queue(); } catch (PermissionException ignored) {}
                })
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setItemsPerPage(3)
                .waitOnSinglePage(false);
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final List<Command> Commands = commandEvent.getClient().getCommands();
        String[] commandLines = new String[Commands.size()];
        int i = 0;
        for (Command command : Commands) {
            String aliasString = Arrays.toString(command.getAliases()).replace("[", "").replace("]", "");
            if (aliasString.length() == 0) { aliasString = "none"; }
            commandLines[i] = "```nim\nCommand : ;;%s\nAliases : %s\nGuild Only : %s\nDescription : \"%s\"\nRequired Arguments : %s\nCooldown : %s seconds```".formatted(command.getName(), aliasString, command.isGuildOnly() ? "true" : "false", command.getHelp(), command.getArguments(), command.getCooldown());
            i++;
        }
        builder.setText("Prefix : `;;` or `@Fortnite`")
                .setItems(commandLines)
                .build().paginate(commandEvent.getChannel(), 1);
    }
}
