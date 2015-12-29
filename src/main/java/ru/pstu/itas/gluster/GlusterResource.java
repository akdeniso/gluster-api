package ru.pstu.itas.gluster;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/api")
public class GlusterResource {
	private final GlusterFileService service;

	public GlusterResource(String host, String volume) {
		this.service = new GlusterFileService(host, volume);
	}

	@POST
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public Response saveFile(@FormDataParam("file") InputStream fileStream,
			@FormDataParam("file") FormDataContentDisposition cdh) {
		String ident = null;
		try {
			ident = service.saveFile(cdh.getFileName(), fileStream);
		} catch (IOException e) {
			throw new WebApplicationException("Error during file save", e);
		}
		return Response.ok(ident).build();
	}

	@DELETE
	@Path("/{ident}")
	public void deleteFile(@PathParam("ident") String ident) {
		try {
			service.deleteFile(ident);
		} catch (IOException e) {
			throw new WebApplicationException("Error during file delete", e);
		}
	}

	@GET
	@Path("/{ident}")
	public Response getFile(@PathParam("ident") String ident) {
		FileDownloadData downloadData = null;
		try {
			downloadData = service.getFile(ident);
		} catch (IOException e) {
			throw new WebApplicationException("Error during file download", e);
		}
		return Response.ok(downloadData.out, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename = " + downloadData.name).build();
	}

}
