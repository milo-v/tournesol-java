package tournesol.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class CommandHandler extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true)
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time)
                        ).queue();
                break;

            case "ery":
                EmbedBuilder eryEmbed = new EmbedBuilder();
                eryEmbed.setTitle("ery");
                System.out.println("In ery case");
                event.getGuild().retrieveMemberById(456456700367470592l).queue(member -> {
                    eryEmbed.setImage(member.getUser().getAvatarUrl());
                    System.out.println(member.getUser().getAvatarUrl());
                    event.reply(MessageCreateData.fromEmbeds(eryEmbed.build())).queue();
                });
                // embed.setImage(event.getGuild().retrieveMemberById("456456700367470592"));

                break;
        }
    }
}
