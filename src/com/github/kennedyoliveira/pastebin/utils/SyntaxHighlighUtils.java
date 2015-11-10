package com.github.kennedyoliveira.pastebin.utils;

import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.sun.istack.internal.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Created by kennedy on 11/9/15.
 */
public class SyntaxHighlighUtils {

    private final static Map<String, PasteHighLight> highLightByFileType;

    static {
        highLightByFileType = Arrays.stream(PasteHighLight.values()).collect(toMap(p -> p.toString().toLowerCase(), identity(), (p1, p2) -> p1));
    }

    /**
     * @deprecated Utility Class
     */
    private SyntaxHighlighUtils() {
    }

    /**
     * Gets a {@link PasteHighLight} based on a file extension without the dot.
     *
     * @param fileExtension File extension, ex: java
     * @return An Optional with the {@link PasteHighLight}
     */
    @Nullable
    public static Optional<PasteHighLight> getHighlighByFileExtension(@Nullable String fileExtension) {
        if (fileExtension == null || fileExtension.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(highLightByFileType.get(fileExtension.toLowerCase()));
    }
}
