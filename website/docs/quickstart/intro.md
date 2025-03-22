# Quick Start

## Prerequisites

To create your first LLM agent, you'll need:

- API key to access a LLM service with function calling support.
- Some tools for the agent to use.

Following LLM services are supported:

- OpenAI

For tools, there are some shared tools:

- Read local file
- Write local file
- Extract web page content
- Execute Python / Java code
- Run SQL query

It's also very easy to create custom tools.

## Create Agents using CLI

The easiest way to create agents is using the command line tool.

Download the CLI jar (`llm-agent-builder-cli.jar`)
from [release page](https://github.com/LLMAgentBuilder/llm-agent-builder/releases).

CLI has a sub-command `build` to build an agent from a config file.

```yaml title="Agent config file"
metadata:
  name: TestAgent
llm:
  openai:
    enabled: true
profile:
  system: You are a helpful assistant.
memory:
  inMemory:
    enabled: true
planner:
  reActJson:
    enabled: true
tools:
  - id: writeLocalFile
    config:
      basePath: "file-output"
    dependency:
      groupId: "com.javaaidev.llmagentbuilder"
      artifactId: "tool-write-local-file"
      version: "0.2.2"
  - id: readLocalFile
    config:
      basePath: "file-input"
    dependency:
      groupId: "com.javaaidev.llmagentbuilder"
      artifactId: "tool-read-local-file"
      version: "0.2.2"
```

The table below shows configurations.

| Configuration Key | Description                |
|-------------------|----------------------------|
| `metadata`        | Agent metadata             |
| `llm`             | LLM used for planning      |
| `profile`         | Agent profile              |
| `memory`          | Memory                     |
| `planner`         | Agent planner              |
| `tools`           | Tools                      |
| `launch`          | Agent server launch config |

By default the agent server starts at port `8080`. It provides a built-in UI. You can interact with
a running agent using this UI.

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
