# Quick Start

## Prerequisites

To create your first LLM agent, you need:

- API key to access a LLM service with function calling support.
- Some tools for the agent to use

Following LLM services are supported:

- OpenAI
- Mistral AI
- Aliyun Dashscope

For tools, there are some shared tools:

- Read local file
- Write local file
- Extract web page content
- Execute Python code

It's also very easy to create custom tools.

## Create Spring Boot Project

Create a Spring Boot project with `spring-boot-starter-web` starter.

Add dependency of LLM Agent Builder Spring Boot starter.

```xml
<dependency>
  <groupId>io.github.llmagentbuilder</groupId>
  <artifactId>spring-boot-starter</artifactId>
  <version>${llm-agent-builder.version}</version>
</dependency>
```

Add dependency of Spring AI LLM integration. Aliyun Dashscope is used below. You can change to OpenAI or Mistral AI.

```xml
<dependency>
  <groupId>io.github.alexcheng1982</groupId>
  <artifactId>spring-ai-dashscope-spring-boot-starter</artifactId>
  <version>${dashscope-client.version}</version>
</dependency>
```

Add LLM adapter dependency for the selected LLM. `llm-dashscope` is for Dashscope.

```xml
<dependency>
  <groupId>io.github.llmagentbuilder</groupId>
  <artifactId>llm-dashscope</artifactId>
  <version>${llm-agent-builder.version}</version>
</dependency>
```

If shared tools are used, add dependencies of these tools.

```xml
<dependency>
  <groupId>io.github.llmagentbuilder</groupId>
  <artifactId>tool-python-code-execution</artifactId>
  <version>${toolkit-code-execution.version}</version>
</dependency>
```

Optionally add `spring-dev` module to expose agent info.

```xml
<dependency>
  <groupId>io.github.llmagentbuilder</groupId>
  <artifactId>spring-dev</artifactId>
  <version>${llm-agent-builder.version}</version>
</dependency>
```

:::info Agent Info
Agent info REST API can be accessed at `/api/_agent/info`.
:::

## Configure Agent

Configure the agent using prefix `io.github.llmagentbuilder.chatagent`.

| Configuration Key  | Description                |
| ------------------ | -------------------------- |
| `name`             | Agent name                 |
| `description`      | Agent description          |
| `usageInstruction` | Usage instruction for user |
| `reActJson`        | ReAct planner              |
| `memory`           | Memory                     |
| `tools`            | Tools                      |
| `tools.config`     | Tools configuration        |

```yaml
io:
  github:
    llmagentbuilder:
      chatagent:
        name: CSV Processor
        description: Process CSV files
        usageInstruction: What you want to do with the CSV file?
        reActJson:
          systemInstructions: |
            Process CSV files.
        memory:
          enabled: false
        tools:
          config:
            readLocalFile:
              basePath: test_data/input
            writeLocalFile:
              basePath: test_data/output
            executePythonCode:
              workingDirectory: test_data/input
```

## Spring Application

The Spring Boot application should import two configurations:

- `AgentControllerConfiguration`: Enable Agent controller REST API endpoint `/api/chat`.
- `AgentDevConfiguration`: Enable Agent Info REST API endpoint `/api/_agent/info`.

```java
@SpringBootApplication
@Import({AgentDevConfiguration.class, AgentControllerConfiguration.class})
public class CsvProcessorApplication {

  public static void main(String[] args) {
    SpringApplication.run(CsvProcessorApplication.class, args);
  }
}
```

You can now use any REST client to interact with the agent.

## Use streamlit ChatAgent UI

A streamlit based [ChatAgent UI](https://github.com/LLMAgentBuilder/llm-agent-builder/tree/main/chat-agent-ui) is created to interact with the agent.

Run the UI.

```sh
streamlit run ChatAgentUI.py
```

You can interact with a running agent using this UI.

## Examples

### CSV Processor

Process local CSV files.

Tools used:

- readLocalFile
- writeLocalFile
- executePythonCode

[GitHub repo](https://github.com/LLMAgentBuilder/example-csv-processor)

### Chinese Idioms Game

成语接龙游戏

Use custom tools.

[GitHub repo](https://github.com/LLMAgentBuilder/example-chinese-idioms-game)
