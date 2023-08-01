package com.slimebot.commands.level;

import com.slimebot.level.Level;
import com.slimebot.level.RankCard;
import de.mineking.discord.commands.annotated.ApplicationCommand;
import de.mineking.discord.commands.annotated.ApplicationCommandMethod;
import de.mineking.discord.commands.annotated.option.Option;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;

@ApplicationCommand(name = "rank", description = "Zeigt dein Aktuelles Level und XP an", feature = "level")
public class RankCommand {

    @ApplicationCommandMethod
    public void performCommand(SlashCommandInteractionEvent event, @Option(name = "user", required = false) Member member) {
        if (member == null) member = event.getMember();

        if (member.getUser().isBot()) {
            event.reply("Bots wie " + member.getAsMention() + " können nicht leveln!").queue();
            return;
        }

        event.deferReply().queue();

        try {
            event.getHook().sendFiles(new RankCard(Level.getLevel(member)).getFile()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}