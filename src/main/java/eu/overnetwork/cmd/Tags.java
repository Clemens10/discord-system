package eu.overnetwork.cmd;

import eu.overnetwork.cfg.Settings;
import eu.overnetwork.util.CustomEmbedBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.File;

public class Tags implements MessageCreateListener {

    /**
     * This method is called every time a message is created.
     *
     * @param event The event.
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Settings cfg = new Settings();
        if (event.getMessageContent().equalsIgnoreCase(cfg.getPrefix() + "tags")) {
         EmbedBuilder c1 = new CustomEmbedBuilder()
                 .setTitle(event.getMessageAuthor().getAvatar() + event.getMessageAuthor().getDiscriminatedName())
                 .addField("Tags", "`bild-drehen`, `frage-stellen`, `keine-reaktionen`");
         event.getChannel().sendMessage(c1);
        } else if (event.getMessageContent().equalsIgnoreCase(cfg.getPrefix() + "tag bild-drehen")) {
            EmbedBuilder c1 = new CustomEmbedBuilder()
                    .setTitle(event.getMessageAuthor().getAvatar() + event.getMessageAuthor().getDiscriminatedName())
                    .addField("Bild Drehen", "Es wäre super, wenn du das Bild nochmal richtig rum reinschicken kannst, damit wir dir besser bei deinem Problem helfen können.");
        event.getChannel().sendMessage(c1);
        } else if (event.getMessageContent().equalsIgnoreCase(cfg.getPrefix() + "tag frage-stellen")) {
            TextChannel frage = event.getChannel();
            MessageBuilder c1 = new MessageBuilder()
                    .addAttachment(new File("https://c.tenor.com/mbUlXX7DCrAAAAAM/sta-naocari.gif"))
                    .setContent("Bitte stelle deine Frage einfach direkt, dann wissen wir worum es geht und können dir schneller und einfacher helfen.");
        }
    }
}
