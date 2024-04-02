import { chat } from '@/services/idioms/agentController';
import { ClearOutlined, RobotOutlined, RocketOutlined, UserOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { Avatar, Button, Card, Col, Input, Row, Space, Spin, message } from 'antd';
import React, { useState } from 'react';

function nextId() {
  let count = 0;
  return function () {
    count++;
    return count;
  };
}

const idGenerator = nextId();

enum Role {
  USER,
  ASSISTANT,
}

type Message = {
  id: string;
  role: Role;
  content: string;
};

const MessageComponent: React.FC<{ message: Message }> = ({ message }) => {
  return (
    <Card
      style={{
        marginBottom: '10px',
        width: 800,
        alignSelf: message.role === Role.USER ? 'start' : 'end',
        backgroundColor: message.role === Role.USER ? '#d2f8d2' : '#ebf5ff',
      }}
    >
      <Space>
        <Avatar
          size={32}
          icon={message.role === Role.USER ? <UserOutlined /> : <RobotOutlined />}
        ></Avatar>
        <p>{message.content}</p>
      </Space>
    </Card>
  );
};

const Welcome: React.FC = () => {
  const [userInput, setUserInput] = useState<string>();
  const [gameStarted, setGameStarted] = useState<boolean>(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [aiThinking, setAiThinking] = useState<boolean>(false);

  function addMessage(role: Role, content: string) {
    setMessages((messages) =>
      messages.concat([
        {
          id: idGenerator() + '',
          role,
          content,
        },
      ]),
    );
  }

  function addUserMessage(content: string) {
    addMessage(Role.USER, content);
  }

  function addAiMessage(content: string) {
    addMessage(Role.ASSISTANT, content);
  }

  async function submitUserInput() {
    if (!userInput) {
      return;
    }
    addUserMessage(userInput);
    setUserInput('');
    setAiThinking(true);
    try {
      const response = await chat({
        input: userInput,
      });
      addAiMessage(response.output);
    } catch (e) {
      message.error('出了点问题，请重试');
    } finally {
      setAiThinking(false);
    }
  }

  function startGame() {
    setGameStarted(true);
    setMessages([
      {
        id: idGenerator() + '',
        role: Role.ASSISTANT,
        content: '欢迎来到成语接龙，请随便说一个成语。',
      },
    ]);
  }

  function resetGame() {
    startGame();
  }

  return (
    <PageContainer>
      {!gameStarted && (
        <Row style={{ margin: '10px 0' }}>
          <Col offset={12}>
            <Button type="primary" size="large" onClick={startGame} icon={<RocketOutlined />}>
              开始游戏
            </Button>
          </Col>
        </Row>
      )}
      {gameStarted && (
        <>
          <Row style={{ margin: '10px 0' }}>
            <Col offset={9} span={6}>
              <Button
                type="primary"
                danger
                size="large"
                onClick={resetGame}
                icon={<ClearOutlined />}
              >
                重新开始
              </Button>
            </Col>
          </Row>
          {aiThinking && (
            <Row style={{ margin: '10px 0' }}>
              <Col offset={10} span={4}>
                <Spin size="large" />
              </Col>
            </Row>
          )}
          <Row>
            <Col style={{ display: 'flex', flexDirection: 'column' }} span={24}>
              {messages &&
                messages.map((message) => <MessageComponent key={message.id} message={message} />)}
            </Col>
          </Row>

          <Row>
            <Col offset={1} span={22}>
              <Space.Compact style={{ width: '100%' }}>
                <Input
                  placeholder="你的输入"
                  value={userInput}
                  onChange={(e) => setUserInput(e.target.value)}
                  size="large"
                  disabled={aiThinking}
                />
                <Button type="primary" size="large" disabled={aiThinking} onClick={submitUserInput}>
                  提交
                </Button>
              </Space.Compact>
            </Col>
          </Row>
        </>
      )}
    </PageContainer>
  );
};

export default Welcome;
