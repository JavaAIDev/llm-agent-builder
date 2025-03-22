package io.github.llmagentbuilder.core.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaaidev.easyllmtools.llmtoolspec.Tool;
import org.springframework.ai.model.function.FunctionCallback;

public class ToolFunctionCallback implements FunctionCallback {

  private final Tool tool;
  private final ObjectMapper objectMapper;

  public ToolFunctionCallback(Tool tool, ObjectMapper objectMapper) {
    this.tool = tool;
    this.objectMapper = objectMapper;
  }

  @Override
  public String getName() {
    return this.tool.getName();
  }

  @Override
  public String getDescription() {
    return this.tool.getDescription();
  }

  @Override
  public String getInputTypeSchema() {
    return this.tool.getParametersSchema();
  }

  @Override
  public String call(String functionInput) {
    try {
      var type = objectMapper.getTypeFactory().constructType(tool.getRequestType());
      var input = objectMapper.readValue(functionInput, type);
      var result = tool.call(input);
      return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      throw new RuntimeException("Failed to call tool", e);
    }
  }
}
