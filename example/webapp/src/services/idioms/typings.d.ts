declare namespace API {
  type ChatAgentRequest = {
    input: string;
    memoryId?: string;
  };

  type ChatAgentResponse = {
    output: string;
  };
}
