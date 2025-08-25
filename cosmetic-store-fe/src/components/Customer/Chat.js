import React, { useState, useEffect, useRef } from 'react';
import { Button, Form, ListGroup } from 'react-bootstrap';
import { BsChatDots, BsSend, BsX } from 'react-icons/bs';
import { useChatService } from '../../services/chatService';

const Chat = ({ customerId, customerName }) => {
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState('');
    const { messages, sendMessage, loading } = useChatService(customerId);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (message.trim()) {
            await sendMessage(message, customerName);
            setMessage('');
        }
    };

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
                }}
                onClick={() => setOpen(!open)}
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

                    {/* Messages */}
                    <div
                        className="flex-grow-1 p-2"
                        style={{ overflowY: 'auto' }}
                    >
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
                                            {msg.timestamp?.toDate().toLocaleTimeString([], {
                                                hour: '2-digit',
                                                minute: '2-digit',
                                            })}
                                        </small>
                                    </div>
                                </ListGroup.Item>
                            ))}
                        </ListGroup>
                        <div ref={messagesEndRef} />
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
                            disabled={loading}
                            className="me-2"
                        />
                        <Button
                            type="submit"
                            disabled={loading || !message.trim()}
                            style={{ backgroundColor: '#eabbb7', border: 'none' }}
                        >
                            <BsSend />
                        </Button>
                    </Form>
                </div>
            )}
        </>
    );
};

export default Chat;
