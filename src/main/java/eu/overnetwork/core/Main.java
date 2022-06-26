package eu.overnetwork.core;

import eu.overnetwork.cfg.Settings;
import eu.overnetwork.listeners.*;
import eu.overnetwork.music.audio.PlayerManager;
import eu.overnetwork.music.commands.LeaveCommand;
import eu.overnetwork.music.commands.PlayCommand;
import eu.overnetwork.music.commands.SkipCommand;
import eu.overnetwork.music.commands.StopCommand;
import eu.overnetwork.reaction.RollenAdd;
import eu.overnetwork.reaction.RollenRemove;
import eu.overnetwork.reaction.VerifyAdd;
import eu.overnetwork.reaction.VerifyRemove;
import io.github.cdimascio.dotenv.Dotenv;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {


    private  static final String VERIFY = "✅";
    private static final String NEWS = "\uD83D\uDCF0";
    private static final String STATUSMELDUNGEN = "\uD83D\uDCC8";

    private static final String german = ":flag_de:";
    private static final String english = ":flag_us:";
    public static void main(String[] args) {

        PlayerManager.init();

        Settings cfg = new Settings();
        Dotenv dotenv = Dotenv.load();
        DiscordApi api = new DiscordApiBuilder()
                .setToken(dotenv.get("TOKEN"))
                .setAllIntents()
                .login()
                .join();


        System.out.println("Connected with " + api.getYourself().getDiscriminatedName());

        api.addServerVoiceChannelMemberLeaveListener(new ServerVoice());

        api.addMessageCreateListener(new VerifyListener());
        api.addListener(new Ping());
        api.addListener(new Groups());
        api.addListener(new Language());
        api.addListener(new PlayCommand());
        api.addListener(new SkipCommand());
        api.addListener(new StopCommand());
        api.addListener(new LeaveCommand());
        api.addReactionAddListener(new RollenAdd());
        api.addReactionRemoveListener(new RollenRemove());
        api.addReactionAddListener(new VerifyAdd());
        api.addReactionRemoveListener(new VerifyRemove());
    }
}
