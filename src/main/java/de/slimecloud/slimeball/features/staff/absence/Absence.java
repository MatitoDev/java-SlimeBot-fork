package de.slimecloud.slimeball.features.staff.absence;

import de.mineking.databaseutils.Column;
import de.mineking.databaseutils.DataClass;
import de.mineking.databaseutils.Table;
import de.slimecloud.slimeball.main.SlimeBot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Absence implements DataClass<Absence> {
	private final SlimeBot bot;

	@Column(key = true)
	private UserSnowflake teamMember;

	@Column(key = true)
	private Guild guild;

	@Column
	private Instant time;

	@NotNull
	@Override
	public Table<Absence> getTable() {
		return bot.getAbsences();
	}
}