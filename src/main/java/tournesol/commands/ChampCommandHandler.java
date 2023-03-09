package tournesol.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import tournesol.models.Champion;
import tournesol.models.Ability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ChampCommandHandler extends ListenerAdapter {
    private Champion champion = new Champion();
    private Ability passive = new Ability();
    private Ability q = new Ability();
    private Ability w = new Ability();
    private Ability e = new Ability();
    private Ability ult = new Ability();
    private EmbedBuilder embed = new EmbedBuilder();
    private ActionRow profileRow = ActionRow.of(
            Button.primary("prev", "<").asDisabled(),
            Button.primary("profile", "Profile").asDisabled(),
            Button.primary("next", ">")
    );
    private ActionRow pRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("p", "Passive").asDisabled(),
            Button.primary("next", ">")
    );
    private ActionRow qRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("q", "1st ability").asDisabled(),
            Button.primary("next", ">")
    );
    private ActionRow wRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("w", "2nd ability").asDisabled(),
            Button.primary("next", ">")
    );
    private ActionRow eRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("e", "3rd ability").asDisabled(),
            Button.primary("next", ">")
    );
    private ActionRow ultRow = ActionRow.of(
            Button.primary("prev", "<"),
            Button.primary("r", "Ultimate").asDisabled(),
            Button.primary("next", ">").asDisabled()
    );
    private int rowIndex = 0;
    private ArrayList<ActionRow> rows = new ArrayList<>();
    /*private ActionRow row = ActionRow.of(
            StringSelectMenu.create("Options")
                    .addOption("Ultimate", "p")
                    .addOption("Ultimate", "r")
                    .build()
    );*/
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("champ")) {
            event.deferReply().queue();

            rows.add(profileRow);
            rows.add(pRow);
            rows.add(qRow);
            rows.add(wRow);
            rows.add(eRow);
            rows.add(ultRow);

            String input = event.getOption("name").getAsString();
            String normalizedName = Pattern.compile("\\b*(.)(.*?)\\b").matcher(input).replaceAll(
                    matchResult -> matchResult.group(1).toUpperCase() + matchResult.group(2).toLowerCase()
            ).replaceAll(" ", "_");
            String urlWR = "https://leagueoflegends.fandom.com/wiki/" + normalizedName + "/WR";
            String urlLoL = "https://leagueoflegends.fandom.com/wiki/" + normalizedName + "/LoL";


            Connection wrConnection = Jsoup.connect(urlWR);
            Connection lolConnection = Jsoup.connect(urlLoL);
            Document wrDocument;
            Document lolDocument;
            try {
                wrDocument = wrConnection.get();
                lolDocument = lolConnection.get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // get champion's basic info
            Element imgElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[3]/div[1]/div[2]/a").first();
            Element nameElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[3]/div[2]/span").first();
            Element subtitleElem = lolDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[6]/aside/div[1]/div/span").first();
            Element positionElem = lolDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[6]/aside/div[6]/div/span/a[1]/img").first();

            String positionUrl = positionElem.attr("data-src");
            positionUrl = positionUrl.substring(0, positionUrl.indexOf(".png") + 4);

            this.champion.setName(nameElem.text());
            this.champion.setSubtitle(subtitleElem.text());
            this.champion.setPositionUrl(positionUrl);
            this.champion.setImageUrl(imgElem.attr("href"));

            this.embed.setTitle(champion.getName());
            this.embed.setDescription(champion.getSubtitle());
            this.embed.setFooter("Press the buttons to view abilities");

            // get champion's passive ability
            Element pNameElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[7]/div/div/div[1]/h3").first();
            Element pDescriptionElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[7]/div/div/div[2]/div[2]/div/p").first();
            Element pImageElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[7]/div/div/div[2]/div[1]/div/img").first();

            String pImageUrl = pImageElem.attr("data-src");
            pImageUrl = pImageUrl.substring(0, pImageUrl.indexOf(".png") + 4);

            this.passive.setName(pNameElem.text());
            this.passive.setDescription(pDescriptionElem.text());
            this.passive.setThumbnailUrl(pImageUrl);

            System.out.println(passive.getName());

            // get champion's 1st ability
            Element qNameElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[10]/div/div/div[1]/h3").first();
            Element qDescriptionElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[10]/div/div/div[2]").first();
            Element qImageElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[10]/div/div/div[2]/div[1]/div/img").first();

            String qImageUrl = qImageElem.attr("data-src");
            qImageUrl = qImageUrl.substring(0, qImageUrl.indexOf(".png") + 4);

            this.q.setName(qNameElem.text());
            this.q.setDescription(qDescriptionElem.text());
            this.q.setThumbnailUrl(qImageUrl);

            System.out.println(q.getName());

            // get champion's 2nd ability
            Element wNameElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[13]/div/div/div[1]/h3").first();
            Element wDescriptionElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[13]/div/div/div[2]").first();
            Element wImageElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[13]/div/div/div[2]/div[1]/div/img").first();

            String wImageUrl = wImageElem.attr("data-src");
            wImageUrl = wImageUrl.substring(0, wImageUrl.indexOf(".png") + 4);

            this.w.setName(wNameElem.text());
            this.w.setDescription(wDescriptionElem.text());
            this.w.setThumbnailUrl(wImageUrl);

            System.out.println(w.getName());

            // get champion's 3rd ability
            Element eNameElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[16]/div/div/div[1]/h3").first();
            Element eDescriptionElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[16]/div/div/div[2]").first();
            Element eImageElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[16]/div/div/div[2]/div[1]/div/img").first();

            String eImageUrl = eImageElem.attr("data-src");
            eImageUrl = eImageUrl.substring(0, eImageUrl.indexOf(".png") + 4);

            this.e.setName(eNameElem.text());
            this.e.setDescription(eDescriptionElem.text());
            this.e.setThumbnailUrl(eImageUrl);

            System.out.println(e.getName());

            // get champion's ultimate
            Element rNameElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[19]/div/div/div[1]/h3").first();
            Element rDescriptionElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[19]/div/div/div[2]").first();
            Element rImageElem = wrDocument.selectXpath("/html/body/div[4]/div[3]/div[2]/main/div[3]/div[2]/div[1]/div[19]/div/div/div[2]/div[1]/div/img").first();

            String rImageUrl = rImageElem.attr("data-src");
            rImageUrl = rImageUrl.substring(0, rImageUrl.indexOf(".png") + 4);

            ult.setName(rNameElem.text());
            ult.setDescription(rDescriptionElem.text());
            ult.setThumbnailUrl(rImageUrl);

            System.out.println(ult.getName());

            EmbedBuilder champEmbed = new EmbedBuilder(this.embed);

            champEmbed.setThumbnail(champion.getPositionUrl());
            champEmbed.setImage(champion.getImageUrl());

            event.getHook().editOriginal(MessageEditData.fromEmbeds(champEmbed.build())).queue();
            event.getHook().editOriginalComponents(profileRow).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        System.out.println("Button " + event.getComponentId() + " pressed by " + event.getUser().getName());
        switch (event.getComponentId()) {
            /*case "p":
                EmbedBuilder pEmbed = new EmbedBuilder(this.embed);
                pEmbed.addField("Passive: " + this.passive.getName(), this.passive.getDescription(), false);
                pEmbed.setThumbnail(passive.getThumbnailUrl());
                event.getMessage().editMessageEmbeds(pEmbed.build()).queue();
                event.deferEdit().queue();

                break;

            case "q":
                EmbedBuilder qEmbed = new EmbedBuilder(this.embed);

            case "r":
                EmbedBuilder rEmbed = new EmbedBuilder(this.embed);
                rEmbed.addField("Ultimate: " + this.ult.getName(), this.ult.getDescription(), false);
                rEmbed.setThumbnail(ult.getThumbnailUrl());
                event.getMessage().editMessageEmbeds(rEmbed.build()).queue();
                event.deferEdit().queue();

                break;*/
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
                embedBuilder.setThumbnail(champion.getPositionUrl());
                embedBuilder.setImage(champion.getImageUrl());
                break;

            case 1:
                embedBuilder.addField("Passive: " + passive.getName(), passive.getDescription(), false);
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
                embedBuilder.addField("Ultimate: " + ult.getName(), ult.getDescription(), false);
                embedBuilder.setThumbnail(ult.getThumbnailUrl());
                break;
        }
        return embedBuilder;
    }

    /*@Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        System.out.println("String select interaction");
        switch (event.getValues().get(0)) {
            case "p":
                EmbedBuilder pEmbed = new EmbedBuilder(this.embed);
                pEmbed.addField("Passive: " + this.passive.getName(), this.passive.getDescription(), false);
                pEmbed.setThumbnail(passive.getThumbnailUrl());
                event.getMessage().editMessageEmbeds(pEmbed.build()).queue();
                event.deferEdit().queue();
                break;

            case "r":
                EmbedBuilder rEmbed = new EmbedBuilder(this.embed);
                rEmbed.addField("Ultimate: " + this.ult.getName(), this.ult.getDescription(), false);
                rEmbed.setThumbnail(ult.getThumbnailUrl());
                event.getMessage().editMessageEmbeds(rEmbed.build()).queue();
                event.deferEdit().queue();
                break;
        }
    }*/
}
