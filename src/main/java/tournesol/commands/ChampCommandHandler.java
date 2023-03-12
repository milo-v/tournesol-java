package tournesol.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tournesol.models.Ability;
import tournesol.models.Champion;

import java.io.IOException;
import java.util.ArrayList;

public class ChampCommandHandler extends ListenerAdapter {
    private Champion champion;
    private Ability passive;
    private Ability q;
    private Ability w;
    private Ability e;
    private Ability ult;
    private EmbedBuilder embed;
    private final ActionRow profileRow = ActionRow.of(
            Button.primary("prev", "<").asDisabled(),
            Button.primary("profile", "Profile").asDisabled(),
            Button.primary("next", ">")
    );
    private final ActionRow pRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("p", "Passive").asDisabled(),
            Button.primary("next", ">")
    );
    private final ActionRow qRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("q", "1st ability").asDisabled(),
            Button.primary("next", ">")
    );
    private final ActionRow wRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("w", "2nd ability").asDisabled(),
            Button.primary("next", ">")
    );
    private final ActionRow eRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("e", "3rd ability").asDisabled(),
            Button.primary("next", ">")
    );
    private final ActionRow ultRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("r", "Ultimate").asDisabled(),
            Button.primary("next", ">").asDisabled()
    );
    private int rowIndex;
    private final ArrayList<ActionRow> rows = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("champ")) {
            System.out.println("champ command called");
            event.deferReply().queue();

            champion = new Champion();
            passive = new Ability();
            q = new Ability();
            w = new Ability();
            e = new Ability();
            ult = new Ability();

            embed = new EmbedBuilder();

            rows.add(profileRow);
            rows.add(pRow);
            rows.add(qRow);
            rows.add(wRow);
            rows.add(eRow);
            rows.add(ultRow);

            rowIndex = 0;

            String input = event.getOption("name").getAsString();
            String normalizedName = input.toLowerCase().replaceAll(" & ", "-").replaceAll("'", "-").replaceAll(" ", "-").replaceAll("\\.", "");

            String homepage = "https://wr-meta.com/";

            Connection c1 = Jsoup.connect(homepage);
            Document d1;
            try {
                d1 = c1.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Elements listChamps = d1.selectXpath("/html/body/div[2]/div[2]/div/div[1]/div/main/div/div/div[2]/div[2]/div/div[position() > 0]");

            String champUrl = "";
            for (Element champ : listChamps) {
                if (champ.getElementsByTag("a").attr("href").contains(normalizedName)) {
                    champUrl += champ.getElementsByTag("a").attr("href");
                }
            }

            Connection c2 = Jsoup.connect(champUrl);
            Connection c3 = Jsoup.connect("https://wildrift.leagueoflegends.com/en-us/champions/" + normalizedName + "/");
            Document d2;
            Document d3;
            try {
                d2 = c2.get();
                d3 = c3.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            d2 = Jsoup.parse(d2.toString().replaceAll("<img alt=\"perlevel\" class=\"emoji\" src=\"/engine/data/emoticons/perlevel.png\">", "increased per level").replaceAll("<img alt=\"attackdamage\" class=\"emoji\" src=\"/engine/data/emoticons/attackdamage.png\">", "AD").replaceAll("<img alt=\"abilitypower\" class=\"emoji\" src=\"/engine/data/emoticons/abilitypower.png\">", "AP").replaceAll("<img alt=\"criticalstrike\" class=\"emoji\" src=\"/engine/data/emoticons/criticalstrike.png\">", "Critical Rate").replaceAll("<img style=\"position:relative;top:2px;\" src=\"/templates/wrw-v2/images/icon-stat/cdr.png\">", "Cooldown(reduction) ").replaceAll("<img style=\"position:relative;top:2px;\" src=\"/templates/wrw-v2/images/icon-stat/Mana.png\">", "Mana ").replaceAll("<img style=\"position:relative;top:2px;\" src=\"/templates/wrw-v2/images/icon-stat/energy.png\">", "Energy ").replaceAll("<img alt=\"cooldownreduction\" class=\"emoji\" src=\"/engine/data/emoticons/cooldownreduction.png\">", "Cooldown(reduction) ").replaceAll("<img alt=\"mana\" class=\"emoji\" src=\"/engine/data/emoticons/mana.png\">", "Mana "));



            // get champion's basic info
            champion.setName(d2.selectXpath("/html/head/meta[7]").attr("content"));
            champion.setSubtitle(d3.selectXpath("/html/body/div[1]/div[1]/div/main/section[1]/div[2]/div/div/div[1]/p").text());
            champion.setImageUrl(d2.selectXpath("/html/head/meta[9]").attr("content"));
            champion.setDescription(d2.selectXpath("/html/head/meta[10]").attr("content") + "...");

            // get champion's passive
            passive.setName(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(1).child(0).child(0).getElementsByTag("p").first().getElementsByTag("i").first().text());
            passive.setDescription(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(1).child(0).child(0).getElementsByTag("p").first().wholeText().replace(passive.getName(), "").replace("Open video", ""));
            passive.setThumbnailUrl("https://wr-meta.com" + d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(1).child(0).child(0).getElementsByTag("span").first().getElementsByTag("img").first().attr("data-src"));

            // get champion's q
            q.setName(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(2).child(0).child(0).getElementsByTag("p").first().getElementsByTag("i").first().text());
            q.setDescription(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(2).child(0).child(0).getElementsByTag("p").first().wholeText().replace(q.getName(), "").replace("Open video", ""));
            q.setThumbnailUrl("https://wr-meta.com" + d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(2).child(0).child(0).getElementsByTag("span").first().getElementsByTag("img").first().attr("data-src"));

            // get champion's w
            w.setName(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(3).child(0).child(0).getElementsByTag("p").first().getElementsByTag("i").first().text());
            w.setDescription(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(3).child(0).child(0).getElementsByTag("p").first().wholeText().replace(w.getName(), "").replace("Open video", ""));
            w.setThumbnailUrl("https://wr-meta.com" + d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(3).child(0).child(0).getElementsByTag("span").first().getElementsByTag("img").first().attr("data-src"));

            // get champion's e
            e.setName(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(4).child(0).child(0).getElementsByTag("p").first().getElementsByTag("i").first().text());
            e.setDescription(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(4).child(0).child(0).getElementsByTag("p").first().wholeText().replace(e.getName(), "").replace("Open video", ""));
            e.setThumbnailUrl("https://wr-meta.com" + d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(4).child(0).child(0).getElementsByTag("span").first().getElementsByTag("img").first().attr("data-src"));

            // get champion's ult
            ult.setName(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(5).child(0).child(0).getElementsByTag("p").first().getElementsByTag("i").first().text());
            ult.setDescription(d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(5).child(0).child(0).getElementsByTag("p").first().wholeText().replace(ult.getName(), "").replace("Open video", ""));
            ult.setThumbnailUrl("https://wr-meta.com" + d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article").first().getElementsByClass("sect-bg3").first().children().first().child(1).child(5).child(0).child(0).getElementsByTag("span").first().getElementsByTag("img").first().attr("data-src"));

            Elements positions = d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article/div/div[1]/div/div[1]").first().getElementsByClass("line-back-s");
            Elements positions2 = d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article/div/div[1]/div/div[1]").first().getElementsByClass("line-back");
            Elements positions3 = d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article/div/div[1]/div/div[1]").first().getElementsByClass("line-back-a");
            Elements positions4 = d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article/div/div[1]/div/div[1]").first().getElementsByClass("line-back-b");
            Elements positions5 = d2.selectXpath("/html/body/div[2]/div[1]/div/div/div/article/div/div[1]/div/div[1]").first().getElementsByClass("line-back-c");
            String pos = "";
            for (Element position : positions) {
                if (!pos.equals("")) {
                    pos += ", ";
                }
                pos += position.child(0).attr("title");
            }
            for (Element position : positions2) {
                if (!pos.equals("")) {
                    pos += ", ";
                }
                pos += position.child(0).attr("title");
            }
            for (Element position : positions3) {
                if (!pos.equals("")) {
                    pos += ", ";
                }
                pos += position.child(0).attr("title");
            }
            for (Element position : positions4) {
                if (!pos.equals("")) {
                    pos += ", ";
                }
                pos += position.child(0).attr("title");
            }
            for (Element position : positions5) {
                if (!pos.equals("")) {
                    pos += ", ";
                }
                pos += position.child(0).attr("title");
            }

            champion.setPosition(pos);

            embed.setTitle(champion.getName());
            embed.setDescription(champion.getSubtitle());

            EmbedBuilder champEmbed = new EmbedBuilder(embed);

            champEmbed.setImage(champion.getImageUrl());
            champEmbed.addField("", champion.getDescription(), false);
            champEmbed.addField("", champion.getName() + " can be played " + champion.getPosition() + ".", false);

            event.getHook().editOriginalEmbeds(champEmbed.build()).queue();
            event.getHook().editOriginalComponents(rows.get(rowIndex)).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "next":
                rowIndex++;
                break;
            case "prev":
                rowIndex--;
                break;
        }
        EmbedBuilder current = createEmbed();
        event.getMessage().editMessageEmbeds(current.build()).queue();
        event.getMessage().editMessageComponents(rows.get(rowIndex)).queue();
        event.deferEdit().queue();
    }

    private EmbedBuilder createEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder(embed);
        switch (rowIndex) {
            case 0:
                embedBuilder.addField("", champion.getDescription(), false);
                embedBuilder.addField("", champion.getName() + " can be played " + champion.getPosition() + ".", false);
                embedBuilder.setImage(champion.getImageUrl());
                break;

            case 1:
                System.out.println(this.passive.getName());
                embedBuilder.addField(passive.getName(), passive.getDescription() + "", false);
                embedBuilder.setThumbnail(passive.getThumbnailUrl());
                break;

            case 2:
                embedBuilder.addField(q.getName(), q.getDescription(), false);
                embedBuilder.setThumbnail(q.getThumbnailUrl());
                break;

            case 3:
                embedBuilder.addField(w.getName(), w.getDescription(), false);
                embedBuilder.setThumbnail(w.getThumbnailUrl());
                break;

            case 4:
                embedBuilder.addField(e.getName(), e.getDescription(), false);
                embedBuilder.setThumbnail(e.getThumbnailUrl());
                break;

            case 5:
                embedBuilder.addField(ult.getName(), ult.getDescription(), false);
                embedBuilder.setThumbnail(ult.getThumbnailUrl());
                break;
        }
        return embedBuilder;
    }
}
