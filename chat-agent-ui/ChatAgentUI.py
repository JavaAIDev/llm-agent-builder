import json
import uuid
from json import JSONDecodeError

import requests
import streamlit as st
from requests import RequestException

with st.sidebar:
    chat_server_url = st.text_input("Chat Server", key="chat_server_url", help="URL of chat service",
                                    value="http://localhost:8080/api/chat")
    agent_info_url = st.text_input("Agent Info", key="agent_info_url", help="URL of agent info",
                                   value="http://localhost:8080/api/_agent/info")
    memory_id = st.text_input("Memory Id", key="memory_id", value=uuid.uuid4(),
                              help="Use a non-empty value to enable memories")

    try:
        agent_info = requests.get(agent_info_url).json()
        title = agent_info["name"]
        caption = agent_info["description"]
        usage_instruction = agent_info["usageInstruction"]
        tools = agent_info["tools"]
    except RequestException as e:
        title = "ChatAgent UI"
        caption = "A simple UI for ChatAgent"
        usage_instruction = "How can I help you?"
        tools = []

    with st.expander("Tools"):
        for tool in tools:
            with st.container():
                st.caption(tool["name"])
                st.text(tool["description"])

st.title(f"💬 {title}")
st.subheader(f"🚀 {caption}")
if "messages" not in st.session_state:
    st.session_state["messages"] = [{"role": "assistant", "content": usage_instruction}]

for msg in st.session_state.messages:
    st.chat_message(msg["role"]).write(msg["content"])

if prompt := st.chat_input():
    st.session_state.messages.append({"role": "user", "content": prompt})
    st.chat_message("user").write(prompt)
    response = requests.post(chat_server_url, json={"input": prompt, "memoryId": memory_id})
    msg = response.json()
    st.session_state.messages.append({"role": "assistant", "content": msg})
    image_saved = False
    if msg["output"]:
        try:
            data = json.loads(msg["output"])
            if data["path"] and data["path"].endswith(".png"):
                st.image(data["path"])
                image_saved = True
        except JSONDecodeError:
            pass
    if not image_saved:
        st.chat_message("assistant").write(msg)
