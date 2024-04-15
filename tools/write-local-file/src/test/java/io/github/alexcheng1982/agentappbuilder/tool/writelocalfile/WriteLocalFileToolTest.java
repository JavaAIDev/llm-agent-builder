package io.github.alexcheng1982.agentappbuilder.tool.writelocalfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
class WriteLocalFileToolTest {

  @Test
  void testWriteContent() throws IOException {
    var tool = new WriteLocalFileToolFactory().create();
    String content = "world";
    var request = new WriteLocalFileRequest("hello.txt", null, content);
    var response = tool.apply(request);
    assertNotNull(response.path());
    assertEquals(content, Files.readString(Path.of(response.path())));
  }

  @Test
  void testDownload() {
    var tool = new WriteLocalFileToolFactory().create();
    var request = new WriteLocalFileRequest("hello.txt", "http://www.baidu.com",
        null);
    var response = tool.apply(request);
    assertNotNull(response.path());
  }

  @Test
  void testWithConfig() throws Exception {
    var tempPath = Files.createTempDirectory("some_path_");
    withEnvironmentVariable("writeLocalFile_basePath",
        tempPath.toAbsolutePath().toString())
        .execute(() -> {
          var tool = new WriteLocalFileToolFactory().create();
          String content = "world";
          var request = new WriteLocalFileRequest("hello.txt", null, content);
          var response = tool.apply(request);
          assertNotNull(response.path());
          assertTrue(
              response.path().startsWith(tempPath.toAbsolutePath().toString()));
        });
  }
}