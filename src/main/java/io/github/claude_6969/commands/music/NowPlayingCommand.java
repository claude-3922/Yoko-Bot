package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Objects;

public class NowPlayingCommand extends Command {

    public NowPlayingCommand() {
        this.name = "nowplaying";
        this.aliases = new String[]{ "np", "current" };
        this.guildOnly = true;
    }

    @Override
    synchronized protected void execute(CommandEvent commandEvent) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        AudioTrack np = musicManager.player.getPlayingTrack();
        if (np == null) return;
        var embed = new EmbedBuilder()
                .setDescription("`🔁 %s` ".formatted(musicManager.scheduler.repeating ? "Yes" : "No") + createProgressBar((int) musicManager.player.getPlayingTrack().getPosition(), (int) np.getDuration()) + " `🔊 %s`".formatted(musicManager.player.getVolume()))
                .setColor(Colors.Blue())
                //.setImage("https://i.ytimg.com/vi/%s/hq720.jpg".formatted(np.getInfo().identifier))
                .setAuthor(np.getInfo().title, np.getInfo().uri, commandEvent.getSelfUser().getAvatarUrl())
                .build();
        commandEvent.getChannel().sendMessage(embed).queue();
    }

    private String createProgressBar(int current, int total) {
        int percentage = current / total;
        int progress = Math.round(100 * percentage);
        int emptyProgress = 100 - progress;
        String progressText = "▇".repeat(progress);
        String emptyProgressText = "-".repeat(emptyProgress);
        //━
        return progressText + emptyProgressText;
    }
}
