package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.PlayerManager;
import jdk.jfr.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LyricsCommand extends Command {

    private final EventWaiter waiter;
    private final Paginator.Builder builder;

    public LyricsCommand(EventWaiter _waiter) {
        this.name = "lyrics";
        this.guildOnly = true;
        this.waiter = _waiter;
        this.arguments = "<songName>";
        this.help = "Get a song's lyrics.";
        this.builder = new Paginator.Builder().setColor(Colors.Blue())
                .setItemsPerPage(30)
                .showPageNumbers(true)
                .setFinalAction(n -> {
                    try { n.clearReactions().queue(); } catch(PermissionException ignored) {}
                })
                .allowTextInput(false)
                .setColumns(1)
                .wrapPageEnds(true)
                .useNumberedItems(false)
                .waitOnSinglePage(false)
                .setEventWaiter(waiter)
                .setTimeout(15, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String songName = commandEvent.getArgs();
        if (songName.isEmpty()) {
            AudioTrack np = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild()).player.getPlayingTrack();
            if (np == null) {
                commandEvent.getChannel().sendMessage(new EmbedBuilder()
                        .setDescription("You forgot to enter a song name.")
                        .setColor(Colors.Blue())
                        .build()).queue();
                return;
            }
            songName = np.getInfo().title;
        }
        LyricsClient lyricsClient = new LyricsClient();
        Lyrics lyrics = null;
        try {
            lyrics  = lyricsClient.getLyrics(songName).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (lyrics == null) {
            commandEvent.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("Lyrics not found.")
                    .setColor(Colors.Blue())
                    .build()).queue();
            return;
        }
        String[] strings = lyrics.getContent().split("\\r?\\n");
        String authorName = lyrics.getAuthor().replace(" Lyrics", "");
        Paginator paginator = builder.setText("**%s** by **%s**".formatted(lyrics.getTitle(), authorName))
                .setItems(strings).setUsers(commandEvent.getAuthor()).build();
        paginator.paginate(commandEvent.getChannel(), 1);
    }
}
