package io.github.claude_6969.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.claude_6969.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private final EmbedBuilder builder = new EmbedBuilder();
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        musicManager.scheduler.channel = channel;

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                var embed = builder
                        .setDescription("Enqueued [**%s**](%s) at position %s.".formatted(track.getInfo().title, track.getInfo().uri, musicManager.scheduler.queue.size() + 1))
                        .setColor(Colors.Blue())
                        .build();
                channel.sendMessage(embed).queue();
                musicManager.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack track = playlist.getTracks().get(0);
                    var embed = builder
                            .setDescription("Enqueued [**%s**](%s) at position %s.".formatted(track.getInfo().title, track.getInfo().uri, musicManager.scheduler.queue.size() + 1))
                            .setColor(Colors.Blue())
                            .build();
                    channel.sendMessage(embed).queue();
                    musicManager.scheduler.queue(track);
                } else {
                    var embed = builder
                            .setDescription("Enqueued `%s tracks` from **%s**.".formatted(playlist.getTracks().size(), playlist.getName()))
                            .setColor(Colors.Blue())
                            .build();
                    channel.sendMessage(embed).queue();
                    for (AudioTrack track : playlist.getTracks()) {
                        musicManager.scheduler.queue(track);
                    }
                }
            }

            @Override
            public void noMatches() {
                var embed = builder
                        .setDescription("No matches found for your query.")
                        .setColor(Colors.Blue())
                        .build();
                channel.sendMessage(embed).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                var embed = builder
                        .setDescription("Exception encountered while loading the track.\n`Error : \"%s\"`".formatted(exception.getMessage()))
                        .setColor(Colors.Blue())
                        .build();
                channel.sendMessage(embed).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}

