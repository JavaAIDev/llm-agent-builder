import uuid

import requests
import streamlit as st

with st.sidebar:
    server_url = st.text_input("Server", key="server_url", help="Server of ChatAgent",
                               value="http://localhost:8080/api/chat")
    memory_id = st.text_input("Memory Id", key="memory_id", value=uuid.uuid4(),
                              help="Use a non-empty value to enable memories")

st.title("💬 ChatAgent UI")
st.caption("🚀 A simple UI for ChatAgent")
if "messages" not in st.session_state:
    st.session_state["messages"] = [{"role": "assistant", "content": "How can I help you?"}]

for msg in st.session_state.messages:
    st.chat_message(msg["role"]).write(msg["content"])

if prompt := st.chat_input():
    st.session_state.messages.append({"role": "user", "content": prompt})
    st.chat_message("user").write(prompt)
    response = requests.post(server_url, json={"input": prompt, "memoryId": memory_id})
    msg = response.json()
    st.session_state.messages.append({"role": "assistant", "content": msg})
    st.chat_message("assistant").write(msg)
