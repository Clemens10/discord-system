package eu.overnetwork.reaction;

import eu.overnetwork.util.AssingRole;
import eu.overnetwork.util.NamesMap;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.awt.*;
import java.io.File;

public class VerifyAdd implements ReactionAddListener {

    private  static final String VERIFY = "✅";

    /**
     * This method is called every time a reaction is added to a message.
     *
     * @param event The event.
     */
    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        if (event.getServer().isPresent() && event.getUser().isPresent()) {
            Server server = event.getServer().get();
            User user = event.getUser().get();
            String emoji = event.getEmoji().asUnicodeEmoji().orElse("");

            switch (emoji) {
                case VERIFY:
                    new AssingRole(server, user, NamesMap.namesMap.get("VERIFY"));

                    EmbedBuilder addrole = new EmbedBuilder()
                            .setTitle("Over-Network System")
                            .setThumbnail(new File("https://raw.githubusercontent.com/mrtuxa/bot-images/main/over-hosting_new.png"))
                            .addField("Verify System", "You've verified yourself")
                            .setColor(Color.BLACK)
                            .setFooter("over-network");
                    user.sendMessage(addrole);
            }
        }
    }
}
