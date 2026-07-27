package org.mtr.mapping.render.obj;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.render.model.RawModel;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public final class ObjModelLoader {
	public static Map<String, RawModel> loadModel(Identifier id, Function<Identifier, InputStream> streamProvider, boolean flipV, boolean splitModel) {
		return Collections.emptyMap(); // TODO full OBJ parser
	}
	public static Identifier resolveRelativePath(Identifier baseFile, String relative, @Nullable String expectExtension) {
		return baseFile;
	}
}
