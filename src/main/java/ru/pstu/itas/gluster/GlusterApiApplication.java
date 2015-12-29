package ru.pstu.itas.gluster;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class GlusterApiApplication extends Application<GlusterVolumeConfiguration> {
	public static void main(String[] args) throws Exception {
		new GlusterApiApplication().run(args);
	}

	@Override
	public String getName() {
		return "gluster-api";
	}

	@Override
	public void run(GlusterVolumeConfiguration conf, Environment env) throws Exception {
		GlusterResource resource = new GlusterResource(conf.getHost(), conf.getVolume());
		env.jersey().register(resource);
	}
}
