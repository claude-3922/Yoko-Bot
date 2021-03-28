package io.github.claude_6969.commands.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.temporal.ChronoUnit;

public class PingCommand extends Command {

    public PingCommand() {
        this.name = "ping";
        this.aliases = new String[] { "latency" };
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getChannel().sendMessage(
                new EmbedBuilder().setColor(Colors.Blue())
                        .setDescription("Pinging...")
                        .build()
        ).queue(m -> {
            long ping = commandEvent.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage(new EmbedBuilder()
                            .setColor(Colors.Blue())
                            .setDescription("Latency : `%sms`\nGateway : `%sms`\n".formatted(ping, commandEvent.getJDA().getGatewayPing()))
                            .build()
            ).queue();
        });
    }
}
