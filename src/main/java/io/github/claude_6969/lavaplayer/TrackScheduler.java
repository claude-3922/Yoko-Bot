package io.github.claude_6969.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.github.claude_6969.Colors;
import io.github.claude_6969.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;
    private final EmbedBuilder builder = new EmbedBuilder();
    private final Map<String, String> playMessages = new HashMap<>();

    public boolean repeating = false;

    public TextChannel channel;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        if (queue.size() <= 0) {
            player.destroy();
            if (channel.getGuild().getAudioManager().isConnected()) {
                channel.getGuild().getAudioManager().closeAudioConnection();
            }
        }
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        channel.deleteMessageById(playMessages.get(channel.getGuild().getId())).queue();
        playMessages.remove(channel.getGuild().getId());
        if (endReason.mayStartNext)
        {
            if (isRepeating())
                player.startTrack(track.makeClone(), false);
            else
                nextTrack();
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        Long member = track.getUserData(Long.class);
        if (this.channel != null) {
            var embed = builder
                    .setDescription("<@%s> [**%s**](%s) is now being played.".formatted(member, track.getInfo().title, track.getInfo().uri))
                    .setThumbnail("https://i.ytimg.com/vi/%s/hq720.jpg".formatted(track.getInfo().identifier))
                    .setColor(Colors.Blue())
                    .build();
            channel.sendMessage(embed).queue(m -> {
                playMessages.put(channel.getGuild().getId(), m.getId());
            });
        }
    }

    public boolean isRepeating() {
        return this.repeating;
    }

    public void setRepeating(boolean repeat) {
        this.repeating = repeat;
    }

    public void shuffle() {
        AudioTrack[] arr = queue.toArray(new AudioTrack[0]);
        Collections.shuffle(Arrays.asList(arr));
        queue.clear();
        for (AudioTrack o : arr) {
            try {
                queue.put(o);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
