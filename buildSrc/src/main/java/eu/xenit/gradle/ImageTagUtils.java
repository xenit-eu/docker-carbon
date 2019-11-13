package eu.xenit.gradle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageTagUtils {

    private ImageTagUtils() {
        // private ctor to hide implicit public one
    }

    public static List<String> toTags(final String version) {
        List<String> ret = new ArrayList<>();

        if (version == null || version.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String[] parts = version.split("\\.");

        String versionPart = null;
        for (String part : parts) {
            versionPart = append(versionPart, part);
            ret.add(versionPart);
        }

        return ret;
    }

    private static String append(final String originalString, final String versionPart) {
        if (originalString == null) {
            return versionPart;
        }

        return originalString + "." + versionPart;
    }
}
