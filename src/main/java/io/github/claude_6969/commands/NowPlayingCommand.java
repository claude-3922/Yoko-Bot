package io.github.claude_6969.commands;

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
    protected void execute(CommandEvent commandEvent) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        AudioTrack np = Objects.requireNonNull(musicManager.player.getPlayingTrack());
        var embed = new EmbedBuilder()
                .setDescription(progressBar((double)(np.getPosition() / np.getInfo().length)))
                .setColor(Colors.Blue())
                .setImage("https://i.ytimg.com/vi/%s/hq720.jpg".formatted(np.getInfo().identifier))
                .setTitle("Currently Playing")
                .addField("Title", "[**%s**](%s)".formatted(np.getInfo().title, np.getInfo().uri), false)
                .addField("Duration", QueueCommand.millisToMMSS(np.getInfo().length), true)
                .addField("Uploaded by", np.getInfo().author, true)
                .addField("Seekable", np.isSeekable() ? "Yes" : "No", false)
                .build();
        commandEvent.getChannel().sendMessage(embed).queue();
    }

    private String progressBar(double percent) {
        String str = "";
        for (int i = 0; i < 25; i++)
            if(i == (int)(percent * 25))
                str += "\uD83D\uDD18"; // 🔘
            else
                str += "▬";
        return str;
    }
}
