package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;

public class SkipCommand extends Command {

    public SkipCommand() {
        this.name = "skip";
        this.aliases = new String[] { "s", "next" };
        this.guildOnly = true;
        this.cooldown = 3;
        this.cooldownScope = CooldownScope.USER;
        this.arguments = "none";
        this.help = "Skips the current playing song.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        musicManager.scheduler.nextTrack();
        commandEvent.getMessage().addReaction("⏩").queue();
    }
}
