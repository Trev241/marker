package io.github.trev.marker;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Mark {
    public final String label;
    public final int x, y, z;
    public final String dimension, creator;

    public Mark(String label, int x, int y, int z, String dimension, String creator) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.creator = creator;
    }

    public static String getPrettyTitle(String s) {
        return Arrays.stream(s.split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return String.format("%s (%d, %d, %d) [%s] marked by [%s]", getPrettyTitle(label), x, y, z, dimension, creator);
    }
}