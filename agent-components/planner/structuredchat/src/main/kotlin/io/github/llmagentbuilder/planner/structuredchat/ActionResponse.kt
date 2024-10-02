package io.github.llmagentbuilder.planner.structuredchat;

import com.fasterxml.jackson.annotation.JsonProperty;

class ActionResponse {

    @JsonProperty("action")
    lateinit var action: String

    @JsonProperty("action_input")
    lateinit var actionInput: String
}
