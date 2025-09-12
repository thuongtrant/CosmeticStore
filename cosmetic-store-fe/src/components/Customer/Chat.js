import React, { useState, useEffect, useRef, useContext } from 'react';
import { Button, Form, ListGroup, Alert, Tab, Tabs } from 'react-bootstrap';
import { BsChatDots, BsSend, BsX, BsRobot, BsPerson } from 'react-icons/bs';
import { MyUserContext } from '../../configs/MyContexts';
import { authApis, endpoints } from '../../configs/Apis';
import { useChatService } from '../../services/chatService';
import AiChat from './AiChat';

const Chat = () => {
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState('');
    const [initialized, setInitialized] = useState(false);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('ai');

    const user = useContext(MyUserContext);
    const { messages, sendMessage, loading, error: chatError } = useChatService(user?.id);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        if (user?.id && open && !initialized && activeTab === 'human') {
            initializeHumanChat();
        }
    }, [user, open, initialized, activeTab]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    useEffect(() => {
        if (chatError) {
            setError(chatError);
        }
    }, [chatError]);

    const initializeHumanChat = async () => {
        try {
            setError('');

            const response = await authApis().post(endpoints['chat-init'], {
                customerName: user.username || user.email || 'Khách hàng'
            });

            console.log('Chat init response:', response.data);
            if (response.data.success) {
                setInitialized(true);
                setError('');
            } else {
                setError(response.data.message || 'Không thể khởi tạo chat');
            }
        } catch (err) {
            if (err.response?.status === 401) {
                setError('Vui lòng đăng nhập để sử dụng chat');
            } else if (err.response?.status === 403) {
                setError('Không có quyền truy cập chat');
            } else {
                setError('Lỗi kết nối. Vui lòng thử lại sau.');
            }
        }
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const handleSendHumanMessage = async (e) => {
        e.preventDefault();
        if (!message.trim() || !initialized) return;

        const messageContent = message.trim();
        setMessage('');

        try {
            await sendMessage(messageContent, user.username || user.email || 'Khách hàng');
            setError('');
        } catch (err) {
            console.error('Send message error:', err);
            setError('Không thể gửi tin nhắn. Vui lòng thử lại.');
            setMessage(messageContent);
        }
    };

    const handleToggleChat = () => {
        setOpen(!open);
        setError('');
    };

    const handleTabChange = (tab) => {
        setActiveTab(tab);
        setError('');
        if (tab === 'human' && !initialized) {
            initializeHumanChat();
        }
    };

    const handleSwitchToHuman = () => {
        setActiveTab('human');
        if (!initialized) {
            initializeHumanChat();
        }
    };

    if (!user) {
        return null;
    }

    return (
        <>
            {/* Floating Chat Button */}
            <Button
                variant="light"
                className="position-fixed shadow"
                style={{
                    bottom: '150px',
                    right: '50px',
                    borderRadius: '50%',
                    width: '60px',
                    height: '60px',
                    backgroundColor: '#eabbb7',
                    border: 'none',
                    zIndex: 1000
                }}
                onClick={handleToggleChat}
            >
                <BsChatDots size={26} color="white" />
            </Button>

            {/* Chat Box */}
            {open && (
                <div
                    className="position-fixed shadow-lg d-flex flex-column"
                    style={{
                        bottom: '90px',
                        right: '20px',
                        width: '400px',
                        maxWidth: 'calc(100vw - 40px)',
                        maxHeight: '600px',
                        minHeight: '400px',
                        height: 'auto',
                        backgroundColor: 'white',
                        borderRadius: '12px',
                        overflow: 'hidden',
                        zIndex: 1000
                    }}
                >
                    {/* Header */}
                    <div
                        className="d-flex justify-content-between align-items-center px-3 py-3"
                        style={{ 
                            backgroundColor: '#eabbb7', 
                            color: 'white',
                            flexShrink: 0,
                            minHeight: '50px'
                        }}
                    >
                        <strong style={{ fontSize: '16px', fontWeight: '600' }}>Hỗ trợ khách hàng</strong>
                        <BsX
                            size={22}
                            style={{ cursor: 'pointer' }}
                            onClick={() => setOpen(false)}
                        />
                    </div>

                    {/* Tab Navigation */}
                    <div style={{ flexShrink: 0 }}>
                        <Tabs
                            activeKey={activeTab}
                            onSelect={handleTabChange}
                            className="px-2 pt-2"
                            style={{ fontSize: '14px' }}
                        >
                            <Tab
                                eventKey="ai"
                                title={
                                    <span>
                                        <BsRobot className="me-1" />
                                        AI Assistant
                                    </span>
                                }
                            >
                                <div style={{ 
                                    height: 'calc(100% - 100px)', 
                                    minHeight: '300px',
                                    display: 'flex',
                                    flexDirection: 'column'
                                }}>
                                    <AiChat
                                        userId={user.id}
                                        userName={user.username || user.email || `Khách hàng ${user.id}`}
                                        onSwitchToHuman={handleSwitchToHuman}
                                    />
                                </div>
                            </Tab>

                            <Tab
                                eventKey="human"
                                title={
                                    <span>
                                        <BsPerson className="me-1" />
                                        Nhân viên
                                    </span>
                                }
                            >
                                <div 
                                    className="d-flex flex-column" 
                                    style={{ 
                                        height: 'calc(100% - 100px)', 
                                        minHeight: '300px' 
                                    }}
                                >
                                {/* Error Alert */}
                                {error && (
                                    <Alert variant="danger" className="m-2 py-2" style={{ fontSize: '12px' }}>
                                        {error}
                                        <Button
                                            variant="outline-danger"
                                            size="sm"
                                            className="ms-2"
                                            onClick={() => {
                                                setError('');
                                                setInitialized(false);
                                                initializeHumanChat();
                                            }}
                                        >
                                            Thử lại
                                        </Button>
                                    </Alert>
                                )}

                                    {/* Messages */}
                                    <div
                                        className="flex-grow-1 p-2"
                                        style={{
                                            overflowY: 'auto',
                                            minHeight: '200px',
                                            maxHeight: '350px',
                                            flex: '1 1 auto'
                                        }}
                                    >
                                    {!initialized ? (
                                        <div className="text-center text-muted p-3">
                                            <div className="spinner-border spinner-border-sm mb-2" role="status">
                                                <span className="visually-hidden">Loading...</span>
                                            </div>
                                            <div>Đang kết nối với nhân viên...</div>
                                        </div>
                                    ) : messages.length === 0 ? (
                                        <div className="text-center text-muted p-3">
                                            <BsPerson size={30} className="mb-2" />
                                            <div>Chưa có tin nhắn nào</div>
                                            <small>Nhân viên sẽ phản hồi sớm nhất!</small>
                                        </div>
                                    ) : (
                                        <>
                                            <ListGroup variant="flush">
                                                {messages.map((msg) => (
                                                    <ListGroup.Item
                                                        key={msg.id}
                                                        className={`border-0 d-flex ${msg.isAdmin ? 'justify-content-start' : 'justify-content-end'}`}
                                                    >
                                                        <div
                                                            className="p-2 rounded-3"
                                                            style={{
                                                                maxWidth: '75%',
                                                                backgroundColor: msg.isAdmin ? '#f1f1f1' : '#eabbb7',
                                                                color: msg.isAdmin ? 'black' : 'white',
                                                                fontSize: '13px'
                                                            }}
                                                        >
                                                            <div>{msg.content}</div>
                                                            <small className="text-muted" style={{ fontSize: '11px' }}>
                                                                {msg.timestamp?.toDate ?
                                                                    msg.timestamp.toDate().toLocaleTimeString([], {
                                                                        hour: '2-digit',
                                                                        minute: '2-digit',
                                                                    }) :
                                                                    new Date().toLocaleTimeString([], {
                                                                        hour: '2-digit',
                                                                        minute: '2-digit',
                                                                    })
                                                                }
                                                            </small>
                                                        </div>
                                                    </ListGroup.Item>
                                                ))}
                                            </ListGroup>
                                            <div ref={messagesEndRef} />
                                        </>
                                    )}
                                </div>

                                    {/* Input for Human Chat */}
                                    <Form
                                        onSubmit={handleSendHumanMessage}
                                        className="d-flex p-2 border-top"
                                        style={{ 
                                            background: '#fff',
                                            flexShrink: 0,
                                            marginTop: 'auto'
                                        }}
                                    >
                                    <Form.Control
                                        type="text"
                                        placeholder="Nhập tin nhắn cho nhân viên..."
                                        value={message}
                                        onChange={(e) => setMessage(e.target.value)}
                                        disabled={loading || !initialized}
                                        className="me-2"
                                        maxLength={1000}
                                        style={{ fontSize: '13px' }}
                                    />
                                    <Button
                                        type="submit"
                                        disabled={loading || !message.trim() || !initialized}
                                        style={{ backgroundColor: '#eabbb7', border: 'none' }}
                                    >
                                        {loading ? (
                                            <div className="spinner-border spinner-border-sm" role="status">
                                                <span className="visually-hidden">Loading...</span>
                                            </div>
                                        ) : (
                                            <BsSend />
                                        )}
                                    </Button>
                                </Form>
                                </div>
                            </Tab>
                        </Tabs>
                    </div>
                </div>
            )}
        </>
    );
};

export default Chat;