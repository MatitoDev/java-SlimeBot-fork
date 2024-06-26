package de.slimecloud.slimeball.features.alerts.youtube.model;

import org.jetbrains.annotations.NotNull;

public record Channel(String id, @NotNull ChannelSnippet snippet) {

	@NotNull
	public String getTitle() {
		return snippet.title();
	}
}
