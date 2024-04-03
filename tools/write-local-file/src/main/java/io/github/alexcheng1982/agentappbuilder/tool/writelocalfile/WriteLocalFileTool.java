package io.github.alexcheng1982.agentappbuilder.tool.writelocalfile;

import io.github.alexcheng1982.agentappbuilder.core.ConfigurableAgentTool;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteLocalFileTool implements
    ConfigurableAgentTool<WriteLocalFileRequest, WriteLocalFileResponse, WriteLocalFileConfig> {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final WriteLocalFileConfig config;

  public WriteLocalFileTool(WriteLocalFileConfig config) {
    this.config = config;
  }

  @NotNull
  @Override
  public String name() {
    return "writeLocalFile";
  }

  @NotNull
  @Override
  public String description() {
    return "write content to a local file, or download content from a url then write to a local file";
  }

  @Override
  public WriteLocalFileResponse apply(
      WriteLocalFileRequest request) {
    try {
      var savePath = calculateSavePath(request);
      if (StringUtils.isNotEmpty(request.content())) {
        Files.writeString(savePath, request.content(), StandardCharsets.UTF_8,
            StandardOpenOption.CREATE_NEW,
            StandardOpenOption.TRUNCATE_EXISTING);
      } else if (StringUtils.isNotEmpty(request.url())) {
        FileUtils.copyURLToFile(new URI(request.url()).toURL(),
            savePath.toFile());
      }
      String path = savePath.toAbsolutePath().toString();
      logger.info("Written to file {}", path);
      return new WriteLocalFileResponse(path);
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private Path calculateSavePath(WriteLocalFileRequest request)
      throws IOException {
    Path basePath =
        StringUtils.isEmpty(config.basePath()) ? Files.createTempDirectory(
            "write-local-file-") : Paths.get(config.basePath());
    var filename = Optional.ofNullable(
            StringUtils.trimToNull(request.filename()))
        .orElseGet(() -> UUID.randomUUID().toString());
    return basePath.resolve(filename);
  }
}
