package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;

public class PauseCommand extends Command {

    public PauseCommand() {
        this.name = "pause";
        this.aliases = new String[] { "resume", "res", "pau", "pp" };
        this.guildOnly = true;
        this.cooldown = 3;
        this.cooldownScope = CooldownScope.USER;
        this.arguments = "none";
        this.help = "Pauses or resumes the player.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        if (musicManager.player.getPlayingTrack() == null) {
            commandEvent.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("Nothing playing in this server.")
                    .setColor(Colors.Blue())
                    .build()).queue();
            return;
        }

        if (musicManager.player.isPaused()) {
            musicManager.player.setPaused(false);
            commandEvent.getMessage().addReaction("▶").queue();
        } else {
            musicManager.player.setPaused(true);
            commandEvent.getMessage().addReaction("⏸").queue();
        }
    }
}
