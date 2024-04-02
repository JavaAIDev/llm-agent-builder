package io.github.alexcheng1982.agentappbuilder.example

import io.github.alexcheng1982.agentappbuilder.core.AgentRequest
import io.github.alexcheng1982.agentappbuilder.core.AgentTool
import io.github.alexcheng1982.agentappbuilder.core.AgentToolFactory
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun name(): String {
        return "write file"
    }

    override fun description(): String {
        return "write file to local disk"
    }

    override fun apply(t: WriteFileRequest): WriteFileResponse {
        val file = Files.createTempFile("tmp_", t.filename)
        Files.writeString(file, t.content)
        logger.info("Write file to {}", file.toAbsolutePath().toString())
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