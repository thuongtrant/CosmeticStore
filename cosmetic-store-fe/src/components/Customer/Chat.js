import React, { useState, useEffect, useRef, useContext } from 'react';
import { Button, Form, ListGroup, Alert } from 'react-bootstrap';
import { BsChatDots, BsSend, BsX } from 'react-icons/bs';
import { MyUserContext } from '../../configs/MyContexts';
import { authApis, endpoints } from '../../configs/Apis';
import { useChatService } from '../../services/chatService';

const Chat = () => {
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState('');
    const [initialized, setInitialized] = useState(false);
    const [error, setError] = useState('');

    const user = useContext(MyUserContext);
    const { messages, sendMessage, loading, error: chatError } = useChatService(user?.id);
    const messagesEndRef = useRef(null);

    // Initialize chat when component mounts and user is available
    useEffect(() => {
        if (user?.id && open && !initialized) {
            initializeChat();
        }
    }, [user, open, initialized]);

    // Auto scroll to bottom when new messages arrive
    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    // Update error state from chat service
    useEffect(() => {
        if (chatError) {
            setError(chatError);
        }
    }, [chatError]);

    const initializeChat = async () => {
        try {
            console.log('Initializing chat for user:', user);
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
            console.error('Chat initialization error:', err);
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

    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (!message.trim() || !initialized) return;

        const messageContent = message.trim();
        setMessage(''); // Clear input immediately for better UX

        try {
            await sendMessage(messageContent, user.username || user.email || 'Khách hàng');
            setError('');
        } catch (err) {
            console.error('Send message error:', err);
            setError('Không thể gửi tin nhắn. Vui lòng thử lại.');
            setMessage(messageContent); // Restore message on error
        }
    };

    const handleToggleChat = () => {
        setOpen(!open);
        setError(''); // Clear errors when opening/closing
    };

    // Don't render if user is not logged in
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
                    bottom: '20px',
                    right: '20px',
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
                        width: '320px',
                        height: '420px',
                        backgroundColor: 'white',
                        borderRadius: '12px',
                        overflow: 'hidden',
                        zIndex: 1000
                    }}
                >
                    {/* Header */}
                    <div
                        className="d-flex justify-content-between align-items-center px-3 py-2"
                        style={{ backgroundColor: '#eabbb7', color: 'white' }}
                    >
                        <strong>Hỗ trợ khách hàng</strong>
                        <BsX
                            size={22}
                            style={{ cursor: 'pointer' }}
                            onClick={() => setOpen(false)}
                        />
                    </div>

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
                                    initializeChat();
                                }}
                            >
                                Thử lại
                            </Button>
                        </Alert>
                    )}

                    {/* Messages */}
                    <div
                        className="flex-grow-1 p-2"
                        style={{ overflowY: 'auto' }}
                    >
                        {!initialized ? (
                            <div className="text-center text-muted p-3">
                                <div className="spinner-border spinner-border-sm mb-2" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div>
                                <div>Đang khởi tạo chat...</div>
                            </div>
                        ) : messages.length === 0 ? (
                            <div className="text-center text-muted p-3">
                                <BsChatDots size={30} className="mb-2" />
                                <div>Chưa có tin nhắn nào</div>
                                <small>Hãy gửi tin nhắn đầu tiên!</small>
                            </div>
                        ) : (
                            <>
                                <ListGroup variant="flush">
                                    {messages.map((msg) => (
                                        <ListGroup.Item
                                            key={msg.id}
                                            className={`border-0 d-flex ${msg.isAdmin ? 'justify-content-start' : 'justify-content-end'
                                                }`}
                                        >
                                            <div
                                                className="p-2 rounded-3"
                                                style={{
                                                    maxWidth: '75%',
                                                    backgroundColor: msg.isAdmin ? '#f1f1f1' : '#eabbb7',
                                                    color: msg.isAdmin ? 'black' : 'white',
                                                }}
                                            >
                                                <div style={{ fontSize: '13px' }}>{msg.content}</div>
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

                    {/* Input */}
                    <Form
                        onSubmit={handleSendMessage}
                        className="d-flex p-2 border-top"
                        style={{ background: '#fff' }}
                    >
                        <Form.Control
                            type="text"
                            placeholder="Nhập tin nhắn..."
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            disabled={loading || !initialized}
                            className="me-2"
                            maxLength={1000}
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
            )}
        </>
    );
};

export default Chat;