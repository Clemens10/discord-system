package eu.overnetwork.cmd;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import eu.overnetwork.core.Constant;
import eu.overnetwork.listeners.CommandCleanupListener;
import eu.overnetwork.util.wiki.parser.WikiPage.WikiPage;
import eu.overnetwork.util.wiki.parser.WikiPage.WikiParser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.logging.ExceptionLogger;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WikiCommand implements CommandExecutor {
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]++>");
    private static final Pattern END_OF_SENTENCE = Pattern.compile("\\.(?: |\\r?\\n)");

    /**
     * Executes the {@code !wiki} command.
     *
     * @param api The Discord api.
     * @param server  The server where the command was issued.
     * @param channel The channel where the command was issued.
     * @param message The message triggering the command.
     * @param args The command's arguments.
     * @throws IOException If the connection to the wiki failed.
     */
    @Command(aliases = {"!wiki"}, async = true, usage = "!wiki [title|full] <search>",
            description = "Shows a link to the wiki or searches through it")
    public void onCommand(DiscordApi api, Server server, TextChannel channel, Message message, String[] args)
            throws IOException {
        // Only react in #java_javacord channel on Discord API server
        if ((server.getId() == Constant.DAPI_SERVER_ID) && (channel.getId() != Constant.TALK_TO_JAMES)) {
            return;
        }

        try (InputStream javacord3Icon = getClass().getClassLoader().getResourceAsStream("over-hosting_new.png")) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setThumbnail(javacord3Icon, "png")
                    .setColor(Constant.JAVACORD_ORANGE);
            if (args.length == 0) { // Just an overview
                embed.setTitle("Javacord Wiki")
                        .setDescription("The [Javacord Wiki](" + WikiParser.BASE_URL + "/wiki) is an excellent "
                                + "resource to get you started with Javacord.\n")
                        .addInlineField("Hint", "You can search the wiki using `!wiki [title|full] <search>");
            } else {
                String searchString = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
                switch (args[0]) {
                    case "page", "p", "title", "t" -> populatePages(api, embed, titleOnly(searchString));
                    case "full", "f", "content", "c" -> populatePages(api, embed, fullSearch(searchString));
                    default -> {
                        searchString = String.join(" ", Arrays.copyOfRange(args, 0, args.length)).toLowerCase();
                        populatePages(api, embed, defaultSearch(searchString));
                    }
                }
            }
            CommandCleanupListener.insertResponseTracker(embed, message.getId());
            channel.sendMessage(embed).join();
        } catch (Throwable t) {
            channel.sendMessage("Something went wrong: ```" + ExceptionLogger.unwrapThrowable(t).getMessage() + "```")
                    .join();
            // Throw the caught exception again. The sdcf4j will log it.
            throw t;
        }
    }

    private Predicate<WikiPage> defaultSearch(String searchString) {
        return titleOnly(searchString).or(keywordsOnly(searchString));
    }

    private Predicate<WikiPage> fullSearch(String searchString) {
        return defaultSearch(searchString).or(contentOnly(searchString));
    }

    private Predicate<WikiPage> titleOnly(String searchString) {
        return page -> page.getTitle().toLowerCase().contains(searchString);
    }

    private Predicate<WikiPage> keywordsOnly(String searchString) {
        return page -> Arrays.stream(page.getKeywords())
                .map(String::toLowerCase)
                .anyMatch(keyword -> keyword.contains(searchString));
    }

    private Predicate<WikiPage> contentOnly(String searchString) {
        return page -> page.getContent().toLowerCase().contains(searchString);
    }


    private void populatePages(DiscordApi api, EmbedBuilder embed, Predicate<WikiPage> criteria) throws IOException {
        List<WikiPage> pages;

        pages = new WikiParser(api)
                .getPagesBlocking().stream()
                .filter(criteria)
                .sorted()
                .collect(Collectors.toList());

        if (pages.isEmpty()) {
            embed.setTitle("Javacord Wiki");
            embed.setUrl(WikiParser.BASE_URL + "/wiki/");
            embed.setDescription("No pages found. Maybe try another search.");
            embed.addField("Standard Search", "Use `!wiki <search>` to search page titles and keywords.");
            embed.addField("Title Search", "Use `!wiki [page|p|title|t] <search>` to exclusively search page titles.");
            embed.addField("Full Search", "Use `!wiki [full|f|content|c] <search>` to perform a full search.");
        } else if (pages.size() == 1) {
            WikiPage page = pages.get(0);
            displayPagePreview(embed, page);
        } else {
            displayPageList(embed, pages);
        }
    }

    private void displayPagePreview(EmbedBuilder embed, WikiPage page) {
        embed.setTitle("Javacord Wiki");
        String cleanedDescription = HTML_TAG.matcher(page.getContent()).replaceAll("").trim();
        int length = 0;
        int sentences = 0;
        Matcher endOfSentenceMatcher = END_OF_SENTENCE.matcher(cleanedDescription);
        while ((length < 600) && (sentences < 3) && endOfSentenceMatcher.find(length)) {
            length = endOfSentenceMatcher.end();
            sentences++;
        }
        length = Math.min(length, 1500);
        StringBuilder description = new StringBuilder()
                .append(String.format("**[%s](%s)**\n\n", page.getTitle(), WikiParser.BASE_URL + page.getPath()))
                .append(cleanedDescription, 0, length);
        if (length < cleanedDescription.length()) {
            description
                    .append("\n\n[*view full page*](")
                    .append(WikiParser.BASE_URL)
                    .append(page.getPath())
                    .append(")");
        }
        embed.setDescription(description.toString());
    }

    private void displayPageList(EmbedBuilder embed, List<WikiPage> pages) {
        embed.setTitle("Javacord Wiki");
        embed.setUrl(WikiParser.BASE_URL + "/wiki/");

        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (WikiPage page : pages) {
            String pageLink = "• " + page.asMarkdownLink();
            if (builder.length() + pageLink.length() > 1900) { // Prevent hitting the description size limit
                break;
            }
            builder.append(pageLink).append("\n");
            counter++;
        }
        if (pages.size() > counter) {
            builder.append("and ").append(pages.size() - counter).append(" more ...");
        }
        embed.setDescription(builder.toString());
    }
}
