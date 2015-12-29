package ru.pstu.itas.gluster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.lucene.queryParser.ParseException;

import ru.pstu.itas.lucene.IdentName;
import ru.pstu.itas.lucene.LuceneFrontend;

public class GlusterFileService {
	private static final char IDENT_NAME_SEPARATOR = '@';

	private final LuceneFrontend lucene;

	private final String mountUri;

	public GlusterFileService(String host, String volume, String luceneIndexDir) {
		this.mountUri = "gluster://" + host + ":" + volume + "/";
		lucene = new LuceneFrontend(new File(luceneIndexDir));
	}

	public FileDownloadData getFile(final String ident) throws IOException, URISyntaxException {
		final Path path = getPathByIdent(ident);
		StreamingOutput out = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(Files.readAllBytes(path));
				output.flush();
			}
		};
		return new FileDownloadData(out, getFileNameWithoutIdent(path.getFileName().toString()));
	}

	public String saveFile(String name, InputStream fileStream) throws IOException, URISyntaxException {
		String ident = UUID.randomUUID().toString();
		Path filePath = Paths.get(new URI(mountUri + ident + IDENT_NAME_SEPARATOR + name));
		byte[] buffer = new byte[512];
		while ((fileStream.read(buffer, 0, buffer.length)) != -1) {
			Files.write(filePath, buffer, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}

		// lucene.index(filePath.toFile());

		return ident;
	}

	public void deleteFile(String ident) throws IOException, URISyntaxException {
		Path filePath = getPathByIdent(ident);
		// lucene.removeFromIndex(filePath.toFile().getName());
		Files.deleteIfExists(filePath);
	}

	public Set<IdentName> search(String content) throws IOException, ParseException {
		return lucene.search(content);
	}

	private Path getPathByIdent(final String ident) throws IOException, URISyntaxException {
		DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(new URI(mountUri)));
		for (Path p : stream) {
			if (p.getFileName().toString().contains(ident))
				return p;
		}
		return null;
	}

	private String getFileNameWithoutIdent(String fullname) {
		int separatorIdx = fullname.indexOf(IDENT_NAME_SEPARATOR);
		if (separatorIdx == -1)
			return fullname;
		return fullname.substring(separatorIdx + 1);
	}
}
