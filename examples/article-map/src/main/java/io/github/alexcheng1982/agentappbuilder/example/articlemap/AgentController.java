package io.github.alexcheng1982.agentappbuilder.example.articlemap;

import io.github.alexcheng1982.agentappbuilder.core.ChatAgentRequest;
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentResponse;
import io.github.alexcheng1982.agentappbuilder.spring.chatagent.ChatAgentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentController {

  private final ChatAgentService chatAgentService;

  public AgentController(ChatAgentService chatAgentService) {
    this.chatAgentService = chatAgentService;
  }

  @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ChatAgentResponse chat(@RequestBody ChatAgentRequest request) {
    return chatAgentService.chat(request);
  }
}
