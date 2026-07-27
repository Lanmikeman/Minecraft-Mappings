package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public final class OperatingSystem extends DummyClass {

	public final net.minecraft.util.Util.OS data;

	private OperatingSystem(net.minecraft.util.Util.OS data) {
		this.data = data;
	}

	@MappedMethod
	public static OperatingSystem convert(net.minecraft.util.Util.OS data) {
		return new OperatingSystem(data);
	}

	@MappedMethod
	public void open(URI uri) {
		data.openUri(uri);
	}

	@MappedMethod
	public void open(String uri) {
		data.openUri(uri);
	}

	@MappedMethod
	public void open(URL url) {
		try {
			data.openUri(url.toURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@MappedMethod
	public void open(File file) {
		data.openFile(file);
	}

	@MappedMethod
	public void open(Path path) {
		data.openPath(path);
	}
}
