package cc.vividcode.ai.agentappbuilder.example

import cc.vividcode.ai.agentappbuilder.core.AgentRequest
import cc.vividcode.ai.agentappbuilder.core.AgentTool
import cc.vividcode.ai.agentappbuilder.core.AgentToolFactory
import java.nio.file.Files

class AddTool : AgentTool<AddRequest, AddResponse> {
    override fun name(): String {
        return "add"
    }

    override fun description(): String {
        return "add two numbers"
    }

    override fun apply(t: AddRequest): AddResponse {
        return AddResponse(t.op1 + t.op2)
    }

}

class AddToolFactory : AgentToolFactory<AddTool> {
    override fun create(): AddTool {
        return AddTool()
    }
}

class WriteFileTool : AgentTool<WriteFileRequest, WriteFileResponse> {
    override fun name(): String {
        return "write file"
    }

    override fun description(): String {
        return "write file to local disk"
    }

    override fun apply(t: WriteFileRequest): WriteFileResponse {
        val file = Files.createTempFile("tmp_", t.filename)
        Files.writeString(file, t.content)
        return WriteFileResponse(file.toAbsolutePath().toString())
    }
}

class WriteFileToolFactory : AgentToolFactory<WriteFileTool> {
    override fun create(): WriteFileTool {
        return WriteFileTool()
    }
}

data class MathAgentRequest(val input: String) : AgentRequest {
    override fun toMap(): Map<String, Any> {
        return mapOf("input" to input)
    }
}

data class MathAgentResponse(val result: String)