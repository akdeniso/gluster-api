package ru.pstu.itas.gluster;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.lucene.queryParser.ParseException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api")
public class GlusterResource {
	private final GlusterFileService service;

	public GlusterResource(String host, String volume, String luceneIndexDir) {
		this.service = new GlusterFileService(host, volume, luceneIndexDir);
	}

	@POST
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public Response saveFile(@FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition cdh) {
		String ident = null;
		try {
			ident = service.saveFile(cdh.getFileName(), fileStream);
		} catch (IOException | URISyntaxException e) {
			throw new WebApplicationException("Error during file save", e);
		}
		return Response.ok(ident).build();
	}

	@DELETE
	@Path("/{ident}")
	public void deleteFile(@PathParam("ident") String ident) {
		try {
			service.deleteFile(ident);
		} catch (IOException | URISyntaxException e) {
			throw new WebApplicationException("Error during file delete", e);
		}
	}

	@GET
	@Path("/{ident}")
	public Response getFile(@PathParam("ident") String ident) {
		FileDownloadData downloadData = null;
		try {
			downloadData = service.getFile(ident);
		} catch (IOException | URISyntaxException e) {
			throw new WebApplicationException("Error during file download", e);
		}
		return Response.ok(downloadData.out, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename = " + downloadData.name).build();
	}

	@GET
	@Path("/search/{content}")
	public Response search(@PathParam("content") String content) {
		try {
			return Response.ok(service.search(content), MediaType.APPLICATION_JSON).build();
		} catch (IOException | ParseException e) {
			throw new WebApplicationException("Error during files search by content", e);
		}
	}
}
