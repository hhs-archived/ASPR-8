package gov.hhs.aspr.ms.gcm.simulation.nucleus;

import java.util.Arrays;
import java.util.List;

public class StandardVersioning {
    private StandardVersioning() {
    }

    public static final String VERSION = "4.4.0";

    private static final List<String> supportedVersions = Arrays.asList(VERSION);

    public static boolean checkVersionSupported(String version) {
        return supportedVersions.contains(version);
    }
}
