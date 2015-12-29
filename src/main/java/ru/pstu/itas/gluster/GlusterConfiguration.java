package ru.pstu.itas.gluster;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class GlusterConfiguration extends Configuration {
	@NotEmpty
	private String host;
	@NotEmpty
	private String volume;
	@NotEmpty
	private String luceneIndexDir;

	@JsonProperty
	public String getHost() {
		return host;
	}

	@JsonProperty
	public void setHost(String host) {
		this.host = host;
	}

	@JsonProperty
	public String getVolume() {
		return volume;
	}

	@JsonProperty
	public void setVolume(String volume) {
		this.volume = volume;
	}

	@JsonProperty
	public String getLuceneIndexDir() {
		return luceneIndexDir;
	}

	@JsonProperty
	public void setLuceneIndexDir(String luceneIndexDir) {
		this.luceneIndexDir = luceneIndexDir;
	}

}
