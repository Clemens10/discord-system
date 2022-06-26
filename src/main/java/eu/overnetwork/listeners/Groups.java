package eu.overnetwork.listeners;

import eu.overnetwork.cfg.Settings;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

public class Groups implements MessageCreateListener {
    /**
     * @param event
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Settings cfg = new Settings();
        if (event.getMessageAuthor().canBanUsersFromServer() && event.getMessageContent().equalsIgnoreCase(cfg.getPrefix() + "rollen")) {
            EmbedBuilder rollenembed = new EmbedBuilder()
                    .setTitle("Over-Network System")
                    .setAuthor("Over-Network", cfg.getWEBSITE(), cfg.getPROFILE())
                    .setThumbnail(cfg.getPROFILE())
                    .setColor(Color.BLACK)
                    .addField("Rollenvergabe", "Reagiere mit folgende Emojis, um die Ränge freizuschalten")
                    .addField(":newspaper:", "News")
                    .addField(":chart_with_upwards_trend:", "Statusmeldungen");
            event.getChannel().sendMessage(rollenembed);
        }
    }
}
