package io.github.claude_6969.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.claude_6969.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends Command {

    public final EmbedBuilder builder = new EmbedBuilder();

    public JoinCommand() {
        this.name = "join";
        this.aliases = new String[] { "joinvc" };
        this.guildOnly = true;
        this.arguments = "none";
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER;
        this.help = "Joins your voice channel.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final TextChannel channel = commandEvent.getTextChannel();
        final Member self = commandEvent.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (selfVoiceState.inVoiceChannel()) {
            var embed = builder
                    .setDescription("Bot already in a channel.")
                    .setColor(Colors.Blue())
                    .build();
            channel.sendMessage(embed).queue();
            return;
        }

        final Member member = commandEvent.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inVoiceChannel()) {
            var embed = builder
                    .setDescription("You forgot to join a voice channel.")
                    .setColor(Colors.Blue())
                    .build();
            channel.sendMessage(embed).queue();
            return;
        }

        final AudioManager audioManager = commandEvent.getGuild().getAudioManager();
        final VoiceChannel memberChannel = memberVoiceState.getChannel();

        var embed = builder
                .setDescription("Connecting to `\uD83D\uDD0A %s`".formatted(memberChannel.getName()))
                .setColor(Colors.Blue())
                .build();
        channel.sendMessage(embed).queue(m -> {
            audioManager.openAudioConnection(memberChannel);
            var embed_2 = builder
                    .setDescription("Connected to `\uD83D\uDD0A %s`".formatted(memberChannel.getName()))
                    .setColor(Colors.Blue())
                    .build();
            m.editMessage(embed_2).queue();
        });
    }
}

