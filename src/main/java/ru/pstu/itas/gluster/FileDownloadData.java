package ru.pstu.itas.gluster;

import javax.ws.rs.core.StreamingOutput;

public final class FileDownloadData {
	public final StreamingOutput out;
	public final String name;

	public FileDownloadData(StreamingOutput out, String name) {
		this.out = out;
		this.name = name;
	}
}
