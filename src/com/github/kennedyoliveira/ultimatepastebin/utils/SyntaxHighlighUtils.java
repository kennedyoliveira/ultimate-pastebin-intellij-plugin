package com.github.kennedyoliveira.ultimatepastebin.utils;

import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Utility class to manage SintaxHighligh of files to the opitons in Pastebin.
 */
public class SyntaxHighlighUtils {

  private static final Logger logger = UltimatePasteBinUtils.logger;
  private static final Map<String, PasteHighLight> highLightByFileType;

  static {
    highLightByFileType = Arrays.stream(PasteHighLight.values()).collect(toMap(p -> p.toString().toLowerCase(), identity(), (p1, p2) -> p1));

    // Some especial cases
    highLightByFileType.put("html", PasteHighLight.HTML_5);
    highLightByFileType.put("xhtml", PasteHighLight.HTML_5);
    highLightByFileType.put("htm", PasteHighLight.HTML);
    highLightByFileType.put("html", PasteHighLight.HTML_5);
    highLightByFileType.put("yml", PasteHighLight.YAML);
    highLightByFileType.put("rb", PasteHighLight.Ruby);
    highLightByFileType.put("py", PasteHighLight.Python);
    highLightByFileType.put("erb", PasteHighLight.Rails);
    highLightByFileType.put("rhtml", PasteHighLight.Rails);
    highLightByFileType.put("rjs", PasteHighLight.Rails);
    highLightByFileType.put("rxml", PasteHighLight.Rails);
    highLightByFileType.put("gsp", PasteHighLight.Groovy);
    highLightByFileType.put("gradle", PasteHighLight.Groovy);
  }

  private SyntaxHighlighUtils() {
    logger.info("Available file highlights: " + highLightByFileType);
  }

  /**
   * Gets a {@link PasteHighLight} based on a file extension without the dot.
   *
   * @param fileExtension File extension, ex: java
   * @return An Optional with the {@link PasteHighLight}
   */
  @NotNull
  public static Optional<PasteHighLight> getHighlighByFileExtension(@Nullable String fileExtension) {
    if (fileExtension == null || fileExtension.isEmpty()) {
      return Optional.empty();
    }

    return Optional.ofNullable(highLightByFileType.get(fileExtension.toLowerCase()));
  }
}
