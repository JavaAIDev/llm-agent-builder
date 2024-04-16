# Tools

## `AgentTool`

An agent tool is an implementation of `AgentTool` interface. Below is the declaration of `AgentTool` interface. It extends from Java's `Function` interface. An agent tool also has a name and a description. Name and description will be passed to LLM to allow LLM to select the right tool to use.

```kotlin title="AgentTool"
interface AgentTool<REQUEST, RESPONSE> : Function<REQUEST, RESPONSE> {

    fun name(): String

    fun description(): String
}
```

To create a new agent tool, we can simply implement the `AgentTool` interface.

The code below shows a tool which adds two numbers.

```kotlin title="A tool to add two numbers"
data class AddToolRequest(val v1: Int, val v2: Int)

data class AddToolResponse(val result: Int)

class AddTool : AgentTool<AddToolRequest, AddToolResponse> {
    override fun name() = "addTwoNumbers"

    override fun description() = "add two numbers"

    override fun apply(request: AddToolRequest): AddToolResponse {
        return AddToolResponse(request.v1 + request.v2)
    }
}
```
