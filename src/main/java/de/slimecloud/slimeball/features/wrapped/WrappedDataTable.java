package de.slimecloud.slimeball.features.wrapped;

import de.mineking.javautils.database.Table;
import de.mineking.javautils.database.Where;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WrappedDataTable extends Table<WrappedData> {
	@NotNull
	default WrappedData getData(long guild, UserSnowflake user) {
		return selectOne(Where.allOf(
				Where.equals("guild", guild),
				Where.equals("user", user.getIdLong())
		)).orElseGet(() -> WrappedData.empty(getManager().getData("bot"), guild, user));
	}

	@NotNull
	default WrappedData getData(@NotNull Member member) {
		return getData(member.getGuild().getIdLong(), member);
	}

	@NotNull
	default List<WrappedData> getAll(@NotNull Guild guild) {
		return selectMany(Where.equals("guild", guild.getIdLong()));
	}
}