package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;

public class LoopCommand extends Command {

    public LoopCommand() {
        this.name = "loop";
        this.aliases = new String[] { "repeat" };
        this.guildOnly = true;
        this.cooldown = 3;
        this.cooldownScope = CooldownScope.USER;
        this.arguments = "";
        this.help = "Sets the song's loop to true or false.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        musicManager.scheduler.setRepeating(!musicManager.scheduler.repeating);
        commandEvent.getChannel().sendMessage(new EmbedBuilder()
                .setDescription("Loop **%s**".formatted(musicManager.scheduler.repeating ? "on" : "off"))
                .setColor(Colors.Blue())
                .build()
        ).queue();
    }
}
