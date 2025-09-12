import React, { useState, useEffect, useRef } from 'react';
import { Button, Form, ListGroup, Alert, Badge, Card, Row, Col } from 'react-bootstrap';
import { BsRobot, BsSend, BsLightbulb, BsCart3, BsArrowRight, BsArrowClockwise } from 'react-icons/bs';
import { useAiChatService } from '../../services/aiChatService';

const AiChat = ({ userId, userName, onSwitchToHuman }) => {
    const [message, setMessage] = useState('');
    const [selectedSkinType, setSelectedSkinType] = useState('');
    const [selectedConcern, setSelectedConcern] = useState('');
    const [validationError, setValidationError] = useState('');
    const [charCount, setCharCount] = useState(0);
    const messagesEndRef = useRef(null);

    const {
        aiMessages,
        sendAiMessage,
        sendQuickResponse,
        clearAiChat,
        loading,
        error,
        aiEnabled,
        quickResponses,
    } = useAiChatService(userId);

    const ChangeLabel = {
        da_dau: "Da dầu",
        da_kho: "Da khô",
        da_hon_hop: "Da hỗn hợp",
        da_nhay_cam: "Da nhạy cảm",
        mun: "Trị mụn",
        chua_biet: "Chưa biết"

    };

    useEffect(() => {
        scrollToBottom();
    }, [aiMessages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const validateMessage = (messageText) => {
        const trimmed = messageText.trim();

        if (trimmed.length === 0) {
            return 'Tin nhắn không được để trống';
        }

        if (trimmed.length > 500) {
            return 'Tin nhắn phải từ 1 đến 500 ký tự';
        }

        return '';
    };

    const handleMessageChange = (e) => {
        const value = e.target.value;
        setMessage(value);
        setCharCount(value.length);

        if (validationError) {
            setValidationError('');
        }

        const error = validateMessage(value);
        if (error) {
            setValidationError(error);
        }
    };

    const handleSendMessage = async (e) => {
        e.preventDefault();

        const validationErr = validateMessage(message);
        if (validationErr) {
            setValidationError(validationErr);
            return;
        }

        if (!aiEnabled) {
            setValidationError('AI Assistant hiện không khả dụng');
            return;
        }

        const messageContent = message.trim();
        setMessage('');
        setCharCount(0);
        setValidationError('');

        try {
            await sendAiMessage(messageContent, selectedSkinType, selectedConcern);
        } catch (err) {
            console.error('Send AI message error:', err);
            setMessage(messageContent);
            setCharCount(messageContent.length);

            if (err.needAuth) {
                setValidationError('Vui lòng đăng nhập để sử dụng tính năng này');
            } else {
                setValidationError(err.message || 'Có lỗi xảy ra khi gửi tin nhắn');
            }
        }
    };

    const handleQuickResponse = async (responseKey) => {
        setValidationError('');
        try {
            await sendQuickResponse(responseKey);
        } catch (err) {
            console.error('Quick response error:', err);
            if (err.needAuth) {
                setValidationError('Vui lòng đăng nhập để sử dụng tính năng này');
            } else {
                setValidationError(err.message || 'Có lỗi xảy ra');
            }
        }
    };


    // Check if send button should be disabled
    const isSendDisabled = () => {
        return loading ||
            !aiEnabled ||
            message.trim().length === 0 ||
            message.length > 500 ||
            !!validationError;
    };

    const formatMessage = (content) => {
        return content
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .split('\n').map((line, index) => (
                <div key={index} dangerouslySetInnerHTML={{ __html: line || '<br>' }} />
            ));
    };

    const ProductRecommendations = ({ recommendations }) => {
        if (!recommendations || recommendations.length === 0) return null;

        return (
            <div className="mt-3">
                <div className="d-flex align-items-center mb-2">
                    <BsLightbulb className="me-2 text-warning" />
                    <small className="text-muted fw-bold">Sản phẩm được gợi ý:</small>
                </div>
                <Row>
                    {recommendations.map((product) => (
                        <Col key={product.productId} md={12} className="mb-2">
                            <Card className="border-0 shadow-sm">
                                <Card.Body className="p-2">
                                    <div className="d-flex">
                                        {product.imageUrl && (
                                            <img
                                                src={product.imageUrl}
                                                alt={product.productName}
                                                style={{ width: '50px', height: '50px', objectFit: 'cover' }}
                                                className="rounded me-2"
                                            />
                                        )}
                                        <div className="flex-grow-1">
                                            <h6 className="mb-1" style={{ fontSize: '12px' }}>
                                                {product.productName}
                                            </h6>
                                            <div className="text-primary fw-bold" style={{ fontSize: '11px' }}>
                                                {new Intl.NumberFormat('vi-VN', {
                                                    style: 'currency',
                                                    currency: 'VND'
                                                }).format(product.price)}
                                            </div>
                                            <small className="text-muted" style={{ fontSize: '10px' }}>
                                                {product.reason}
                                            </small>
                                        </div>
                                        <Button
                                            variant="outline-primary"
                                            size="sm"
                                            onClick={() => window.open(`/productdetail/${product.productId}`, '_blank')}
                                            style={{ fontSize: '10px', padding: '2px 6px' }}
                                        >
                                            <BsArrowRight />
                                        </Button>
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </div>
        );
    };

    if (!aiEnabled) {
        return (
            <div className="text-center p-3">
                <BsRobot size={40} className="mb-3 text-muted" />
                <div className="text-muted">AI Assistant hiện đang bảo trì</div>
                <Button
                    variant="outline-primary"
                    size="sm"
                    className="mt-2"
                    onClick={onSwitchToHuman}
                >
                    Chat với nhân viên
                </Button>
            </div>
        );
    }

    return (
        <div className="d-flex flex-column h-100">

            {/* Quick Response Buttons */}
            {Object.keys(quickResponses).length > 0 && (
                <div className="p-2 border-bottom">
                    <div className="d-flex flex-wrap gap-1">
                        {Object.entries(quickResponses).slice(0, 6).map(([key, value]) => (
                            <Button
                                key={key}
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => handleQuickResponse(key)}
                                disabled={loading}
                                style={{ fontSize: '10px', padding: '2px 6px' }}
                            >
                                {ChangeLabel[key] || value}
                            </Button>
                        ))}
                    </div>
                </div>
            )}

            {/* Messages */}
            <div className="flex-grow-1 p-2" style={{ overflowY: 'auto', maxHeight: 'calc(60vh - 200px)', minHeight: '200px' }}>
                {aiMessages.length === 0 ? (
                    <div className="text-center text-muted p-3">
                        <BsRobot size={30} className="mb-2" />
                        <div>AI Assistant sẵn sàng hỗ trợ!</div>
                        <small>Hãy mô tả tình trạng da của bạn</small>
                    </div>
                ) : (
                    <>
                        <ListGroup variant="flush">
                            {aiMessages.map((msg) => (
                                <ListGroup.Item
                                    key={msg.id}
                                    className={`border-0 d-flex ${msg.isAi ? 'justify-content-start' : 'justify-content-end'}`}
                                >
                                    <div
                                        className="p-2 rounded-3"
                                        style={{
                                            maxWidth: '85%',
                                            backgroundColor: msg.isAi
                                                ? (msg.isError ? '#f8d7da' : '#e3f2fd')
                                                : '#eabbb7',
                                            color: msg.isAi
                                                ? (msg.isError ? '#721c24' : '#0d47a1')
                                                : 'white',
                                            fontSize: '13px'
                                        }}
                                    >
                                        {msg.isAi && !msg.isError && (
                                            <div className="d-flex align-items-center mb-1">
                                                <BsRobot size={12} className="me-1" />
                                                <small style={{ fontSize: '10px', opacity: 0.8 }}>
                                                    AI Assistant
                                                </small>
                                            </div>
                                        )}

                                        <div>{formatMessage(msg.content)}</div>

                                        {msg.recommendations && (
                                            <ProductRecommendations
                                                recommendations={msg.recommendations}
                                            />
                                        )}

                                        <small className="text-muted d-block mt-1" style={{ fontSize: '10px' }}>
                                            {msg.timestamp.toLocaleTimeString([], {
                                                hour: '2-digit',
                                                minute: '2-digit',
                                            })}
                                        </small>
                                    </div>
                                </ListGroup.Item>
                            ))}
                        </ListGroup>
                        <div ref={messagesEndRef} />
                    </>
                )}
            </div>

            {/* Message Input */}
            <Form onSubmit={handleSendMessage} className="p-2 border-top">
                <div className="d-flex">
                    <Form.Control
                        type="text"
                        placeholder="Ví dụ: Tôi có da dầu và bị mụn..."
                        value={message}
                        onChange={handleMessageChange}
                        disabled={loading || !aiEnabled}
                        className={`me-2 ${validationError ? 'is-invalid' : ''}`}
                        maxLength={500}
                        style={{ fontSize: '13px' }}
                    />
                    <Button
                        type="submit"
                        disabled={isSendDisabled()}
                        style={{
                            backgroundColor: '#eabbb7',
                            border: 'none',
                            minWidth: '40px'
                        }}
                    >
                        {loading ? (
                            <div className="spinner-border spinner-border-sm" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </div>
                        ) : (
                            <BsSend />
                        )}
                    </Button>
                </div>

                {validationError && (
                    <div className="text-danger mt-1" style={{ fontSize: '12px' }}>
                        {validationError}
                    </div>
                )}
            </Form>
        </div>
    );
};

const ProductRecommendations = ({ recommendations }) => {
    if (!recommendations || recommendations.length === 0) return null;

    return (
        <div className="mt-3">
            <div className="d-flex align-items-center mb-2">
                <BsLightbulb className="me-2 text-warning" />
                <small className="text-muted fw-bold">Sản phẩm được gợi ý:</small>
            </div>
            <Row>
                {recommendations.map((product) => (
                    <Col key={product.productId} md={12} className="mb-2">
                        <Card className="border-0 shadow-sm">
                            <Card.Body className="p-2">
                                <div className="d-flex">
                                    {product.imageUrl && (
                                        <img
                                            src={product.imageUrl}
                                            alt={product.productName}
                                            style={{ width: '50px', height: '50px', objectFit: 'cover' }}
                                            className="rounded me-2"
                                        />
                                    )}
                                    <div className="flex-grow-1">
                                        <h6 className="mb-1" style={{ fontSize: '12px' }}>
                                            {product.productName}
                                        </h6>
                                        <div className="text-primary fw-bold" style={{ fontSize: '11px' }}>
                                            {new Intl.NumberFormat('vi-VN', {
                                                style: 'currency',
                                                currency: 'VND'
                                            }).format(product.price)}
                                        </div>
                                        <small className="text-muted" style={{ fontSize: '10px' }}>
                                            {product.reason}
                                        </small>
                                    </div>
                                    <Button
                                        variant="outline-primary"
                                        size="sm"
                                        onClick={() => window.open(`/productdetail/${product.productId}`, '_blank')}
                                        style={{ fontSize: '10px', padding: '2px 6px' }}
                                    >
                                        <BsArrowRight />
                                    </Button>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </div>
    );
};

export default AiChat;
