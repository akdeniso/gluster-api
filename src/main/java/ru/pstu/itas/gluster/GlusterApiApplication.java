package ru.pstu.itas.gluster;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class GlusterApiApplication extends Application<GlusterConfiguration> {
	public static void main(String[] args) throws Exception {
		new GlusterApiApplication().run(args);
	}

	@Override
	public String getName() {
		return "gluster-api";
	}

	@Override
	public void run(GlusterConfiguration conf, Environment env) throws Exception {
		GlusterResource resource = new GlusterResource(conf.getHost(), conf.getVolume(), conf.getLuceneIndexDir());
		env.jersey().register(resource);
		env.jersey().register(MultiPartFeature.class);
	}
}
