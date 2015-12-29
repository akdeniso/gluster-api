package ru.pstu.itas.gluster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class GlusterFileService {
	private static final char IDENT_NAME_SEPARATOR = '@';

	private final String mountUri;

	public GlusterFileService(String host, String volume) {
		this.mountUri = "gluster://" + host + ":" + volume;
	}

	public FileDownloadData getFile(final String ident) throws IOException {
		final Path path = getPathByIdent(ident);
		StreamingOutput out = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(Files.readAllBytes(path));
				output.flush();
			}
		};
		return new FileDownloadData(out, getFileNameWithoutIdent(path.toFile()));
	}

	public String saveFile(String name, InputStream fileStream) throws IOException {
		String ident = UUID.randomUUID().toString();

		OutputStream out = Files.newOutputStream(Paths.get(mountUri + "/" + ident + IDENT_NAME_SEPARATOR + name));
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = fileStream.read(bytes)) != -1)
			out.write(bytes, 0, read);
		out.flush();
		out.close();

		return ident;
	}

	public void deleteFile(String ident) throws IOException {
		Files.deleteIfExists(getPathByIdent(ident));
	}

	private Path getPathByIdent(final String ident) throws IOException {
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(Path entry) throws IOException {
				return entry.toFile().getName().startsWith(ident);
			}
		};
		DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(mountUri), filter);
		if (!stream.iterator().hasNext())
			throw new IOException("Path was not found by ident " + ident);
		return stream.iterator().next();
	}

	private String getFileNameWithoutIdent(File file) {
		String name = file.getName();
		int separatorIdx = name.indexOf(IDENT_NAME_SEPARATOR);
		if (separatorIdx == -1)
			return name;
		return name.substring(separatorIdx + 1);
	}
}
