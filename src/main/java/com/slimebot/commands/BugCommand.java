package com.slimebot.commands;

import com.slimebot.main.Main;
import com.slimebot.main.config.guild.GuildConfig;
import de.mineking.discord.commands.annotated.ApplicationCommand;
import de.mineking.discord.commands.annotated.ApplicationCommandMethod;
import de.mineking.discord.events.Listener;
import de.mineking.discord.events.interaction.ModalHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHIssue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationCommand(name = "bug", description = "Melde einen Bug", guildOnly = true)
public class BugCommand {
	public final static Modal modal = Modal.create("bug", "Melde einen Bug")
			.addActionRow(TextInput.create("title", "Titel", TextInputStyle.SHORT)
					.setMinLength(5)
					.setPlaceholder("Eine kurze präzise Beschreibung des Bugs")
					.build()
			)
			.addActionRow(TextInput.create("reproduction", "Schritte zum Reproduzieren", TextInputStyle.PARAGRAPH)
					.setMinLength(10)
					.setPlaceholder("""
							1. Gehe zu '....'
							2. Klicke auf '....'
							3. Scrolle nach unten zu '....'""")
					.build()
			)
			.addActionRow(TextInput.create("description", "Beschreibung", TextInputStyle.PARAGRAPH)
					.setMinLength(10)
					.setPlaceholder("Eine ausführliche Beschreibung des Bugs")
					.build()
			)
			.addActionRow(TextInput.create("expected", "Erwartetes Verhalten", TextInputStyle.PARAGRAPH)
					.setMinLength(10)
					.setPlaceholder("Ich erwarte, dass '....' passiert")
					.build()
			)
			.addActionRow(TextInput.create("solution", "Lösung", TextInputStyle.PARAGRAPH)
					.setPlaceholder("Ich würde '....' ändern, damit '....' passiert")
					.setRequired(false)
					.build()
			)
			.build();

	private final Map<Long, Long> timeout = new HashMap<>();

	@ApplicationCommandMethod
	public void performCommand(SlashCommandInteractionEvent event) {
		if(timeout.containsKey(event.getUser().getIdLong())) {
			if(timeout.get(event.getUser().getIdLong()) > System.currentTimeMillis()) {
				event.reply("Du kannst nur alle 5 Minuten einen Bug Melden!").setEphemeral(true).queue();
				return;
			}
		}

		event.replyModal(modal).queue();
	}

	@Listener(type = ModalHandler.class, filter = "bug")
	public void handleModal(ModalInteractionEvent event) throws IOException {
		timeout.put(event.getUser().getIdLong(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));

		String text = IOUtils.resourceToString("/github_issue_template", StandardCharsets.UTF_8)
				.replace("%description%", event.getValue("description").getAsString())
				.replace("%reproduction%", event.getValue("reproduction").getAsString())
				.replace("%expected%", event.getValue("expected").getAsString())
				.replace("%solution%", event.getValue("solution").getAsString());

		GHIssue issue = Main.github.getRepository(Main.config.github.repository)
				.createIssue(event.getValue("title").getAsString())
				.body(text + "\n\n" + "Reported from Discord Member " + event.getUser().getName() + " (" + event.getUser().getIdLong() + ")")
				.label("bug")
				.create();

		GuildConfig.getConfig(event.getGuild()).getLogChannel().ifPresent(channel ->
				channel.sendMessageEmbeds(
								new EmbedBuilder()
										.setColor(GuildConfig.getColor(event.getGuild()))
										.setTitle("Neuer Bug: **" + event.getValue("title").getAsString() + "**", issue.getHtmlUrl().toString())
										.setThumbnail(event.getMember().getEffectiveAvatarUrl())

										.setDescription(text.substring(0, Math.min(text.length(), MessageEmbed.DESCRIPTION_MAX_LENGTH)))
										.setFooter("Report von: " + event.getUser().getGlobalName() + " (" + event.getUser().getId() + ")")
										.build()
						)
						.addActionRow(Button.link(issue.getHtmlUrl().toString(), "Auf GitHub ansehen"))
						.queue()
		);

		event.reply("Der Report wurde erfolgreich ausgeführt").setEphemeral(true).queue();
	}
}
