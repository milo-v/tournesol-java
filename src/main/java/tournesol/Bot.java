package tournesol;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import tournesol.commands.ChampCommandHandler;
import tournesol.commands.CommandHandler;

import java.util.Collections;

public class Bot {
    public static void main(String[] args) {
        // Dotenv dotenv = Dotenv.load();
        JDA jda = JDABuilder.createLight("NzYxNTg0NzMyNDMzMTU0MDc4.GKl6X3.9LjUE55FmgJb0n6qUEbFnnKAgM0poHIxoI8NlU", Collections.emptyList())
                .setActivity(Activity.playing("with Ery"))
                .build();

        jda.addEventListener(new CommandHandler());
        jda.addEventListener(new ChampCommandHandler());
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Ping from bot"),
                Commands.slash("ery", "ery"),
                Commands.slash("champ", "shows information about a Wild Rift champion")
                        .addOption(OptionType.STRING, "name", "the champion's name")
        );
    }
}