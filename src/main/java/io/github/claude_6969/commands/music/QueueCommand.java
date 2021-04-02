package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.GuildMusicManager;
import io.github.claude_6969.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends Command {

    private final Paginator.Builder builder;

    public QueueCommand(EventWaiter _waiter) {
        this.name = "queue";
        this.aliases = new String[] { "q", "list" };
        this.guildOnly = true;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.arguments = "(pageNumber)";
        this.help = "Shows this server's song queue.";
        this.builder = new Paginator.Builder().setColor(Colors.Blue())
                .allowTextInput(true)
                .setColumns(1)
                .setFinalAction(n -> {
                    try { n.clearReactions().queue(); } catch (PermissionException ignore) {}
                })
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(_waiter)
                .useNumberedItems(true)
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(commandEvent.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        AudioTrack np = musicManager.player.getPlayingTrack();
        if (np == null) {
            commandEvent.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("Nothing playing in this server.")
                    .setColor(Colors.Blue())
                    .build()).queue();
            return;
        }
        if (queue.isEmpty()) {
            commandEvent.getChannel().sendMessage(new EmbedBuilder()
                    .setDescription("Currently Playing : [**%s**](%s) - `%s`".formatted(np.getInfo().title, np.getInfo().uri, millisToMMSS(np.getInfo().length)))
                    .setColor(Colors.Blue())
                    .build()).queue();
            return;
        }
        int pagenum = 1;
        try {
            pagenum = Integer.parseInt(commandEvent.getArgs());
        } catch (NumberFormatException ignored) {}
        List<AudioTrack> list = new ArrayList<>(queue);
        String[] songs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            AudioTrack song = list.get(i);
            Long member = song.getUserData(Long.class);
            songs[i] = "<@%s> [**%s**](%s) - `%s`".formatted(member, song.getInfo().title, song.getInfo().uri, millisToMMSS(song.getInfo().length));
        }
        Paginator paginator = builder.setText("⠀")
                .setItems(songs).setUsers(commandEvent.getAuthor()).build();
        paginator.paginate(commandEvent.getChannel(), pagenum);
    }

    public static String millisToMMSS(long millis) {
         long seconds = millis / 1000;
         String s = "%s".formatted(seconds % 60);
        String m = "%s".formatted((seconds / 60) % 60);
         if (s.length() == 1) s = "0%s".formatted(s);
         return "%s:%s".formatted(m, s);
    }
}
