package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;

public class ShuffleCommand extends Command {

    public ShuffleCommand() {
        this.name = "shuffle";
        this.aliases = new String[] { "sh", "randomize", "randomizequeue", "shqueue" };
        this.guildOnly = true;
        this.cooldown = 3;
        this.cooldownScope = CooldownScope.USER;
        this.arguments = "none";
        this.help = "Shuffles this server's queue.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        if (musicManager.player.getPlayingTrack() == null) {
            commandEvent.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("Nothing playing in this server.")
                    .setColor(Colors.Blue())
                    .build()).queue();
        }
        if (musicManager.scheduler.queue.size() < 2) {
            commandEvent.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("Cannot shuffle a queue with less than 2 tracks.")
                    .setColor(Colors.Blue())
                    .build()).queue();
        }
        musicManager.scheduler.shuffle();
        commandEvent.getMessage().addReaction("🔀").queue();
    }
}
