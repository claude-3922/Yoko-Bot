package io.github.claude_6969;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jlyrics.LyricsClient;
import io.github.claude_6969.commands.info.HelpCommand;
import io.github.claude_6969.commands.info.PingCommand;
import io.github.claude_6969.commands.music.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.List;

public class Main extends ListenerAdapter {

    public static final Logger LOGGER = LoggerFactory.getLogger("fortnite");
    public static JDA Client = null;
    private static final EventWaiter Waiter = new EventWaiter();
    private static final LyricsClient LClient = new LyricsClient();

    public static void main(String[] args) {
        Client = BuildClient(BuildCommandClient());
    }

    private static JDA BuildClient(CommandClient _commandClient) {
        JDA _jda = null;
        try {
            _jda = JDABuilder.createDefault(Config.Token(),
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_MEMBERS)
                    .disableCache(EnumSet.of(CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY))
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new Main())
                    .addEventListeners(Waiter, _commandClient)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        return _jda;
    }

    private static CommandClient BuildCommandClient() {
        return new CommandClientBuilder()
                .setPrefix(Config.Prefix())
                .setAlternativePrefix("<@799904819144818688>")
                .setAlternativePrefix("@Fortnite")
                .useHelpBuilder(false)
                .setActivity(Activity.streaming("fortnite", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
                .setEmojis("☑", "❌", "⚠")
                .setOwnerId("776357859611901983")
                .addCommand(new PingCommand())
                .addCommand(new JoinCommand())
                .addCommand(new PlayCommand())
                .addCommand(new SkipCommand())
                .addCommand(new StopCommand())
                .addCommand(new QueueCommand(Waiter))
                .addCommand(new LoopCommand())
                .addCommand(new PauseCommand())
                .addCommand(new ShuffleCommand())
                .addCommand(new LyricsCommand(Waiter, LClient))
                .addCommand(new HelpCommand(Waiter))
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getName());
    }
}
