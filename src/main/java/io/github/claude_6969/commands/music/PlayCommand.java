package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.Colors;
import io.github.claude_6969.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class PlayCommand extends Command {

    public final EmbedBuilder builder = new EmbedBuilder();

    public PlayCommand() {
        this.name = "play";
        this.aliases = new String[] { "p" };
        this.guildOnly = true;
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.arguments = "<songName>";
        this.help = "Plays a song in your voice channel.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final TextChannel channel = commandEvent.getTextChannel();
        final Member self = commandEvent.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = commandEvent.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if(commandEvent.getArgs().isEmpty()) {
            var embed = builder
                    .setDescription("You forgot to enter a song name.")
                    .setColor(Colors.Blue())
                    .build();
            channel.sendMessage(embed).queue();
            return;
        }

        if(!memberVoiceState.inVoiceChannel()) {
            var embed = builder
                    .setDescription("You forgot to join a voice channel.")
                    .setColor(Colors.Blue())
                    .build();
            channel.sendMessage(embed).queue();
            return;
        }

        if(!selfVoiceState.inVoiceChannel()) {
            new JoinCommand().execute(commandEvent);
        }

        String link = String.join(" ", commandEvent.getArgs());

        if (!link.startsWith("http")) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link, commandEvent.getAuthor().getIdLong());
    }
}

