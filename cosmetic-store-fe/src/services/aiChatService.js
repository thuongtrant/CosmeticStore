import { useState, useEffect } from 'react';
import { authApis, endpoints } from '../configs/Apis';

export const useAiChatService = (userId) => {
    const [aiMessages, setAiMessages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [aiEnabled, setAiEnabled] = useState(false);
    const [quickResponses, setQuickResponses] = useState({});

    const [rateLimitInfo, setRateLimitInfo] = useState({
        remainingMessages: null,
        resetTime: null,
        isRateLimited: false
    });

    useEffect(() => {
        if (userId) {
            initializeAiChat();
        }
    }, [userId]);

    const initializeAiChat = async () => {
        try {
            const statusRes = await authApis().get(endpoints['ai-chat-status']);
            setAiEnabled(statusRes.data);

            if (statusRes.data) {
                const welcomeRes = await authApis().post(endpoints['ai-chat-new-conversation']);
                if (welcomeRes.data && welcomeRes.data.success) {
                    setAiMessages([{
                        id: 'welcome',
                        content: welcomeRes.data.message,
                        isAi: true,
                        timestamp: new Date(),
                        recommendations: []
                    }]);
                }

                const quickRes = await authApis().get(endpoints['ai-chat-quick-responses']);
                setQuickResponses(quickRes.data);
            }

            setError(null);
        } catch (err) {
            console.error('AI Chat initialization error:', err);
            setError('Không thể khởi tạo AI Assistant');
            setAiEnabled(false);
        }
    };

    const validateMessage = (message) => {
        if (!message || typeof message !== 'string') {
            throw new Error('Tin nhắn không hợp lệ');
        }

        const trimmed = message.trim();
        if (trimmed.length === 0) {
            throw new Error('Tin nhắn không được để trống');
        }

        if (trimmed.length > 500) {
            throw new Error('Tin nhắn phải từ 1 đến 500 ký tự');
        }

        return trimmed;
    };

    const sendAiMessage = async (message, skinType = null, concern = null) => {
        if (!userId) {
            const authError = new Error('Vui lòng đăng nhập để sử dụng tính năng này');
            authError.needAuth = true;
            throw authError;
        }

        const validatedMessage = validateMessage(message);

        setLoading(true);

        const userMessage = {
            id: Date.now() + '_user',
            content: validatedMessage,
            isAi: false,
            timestamp: new Date()
        };

        setAiMessages(prev => [...prev, userMessage]);

        try {
            const requestData = {
                message: validatedMessage,
                skinType,
                concern,
                chatRoomId: `ai_customer_${userId}`
            };

            console.log('Sending AI message:', requestData);

            const response = await authApis().post(endpoints['ai-chat-message'], requestData);

            if (response.data) {
                if (response.data.rateLimited) {
                    setRateLimitInfo({
                        remainingMessages: response.data.remainingMessages || 0,
                        resetTime: response.data.rateLimitResetTime,
                        isRateLimited: true
                    });

                    const rateLimitMessage = {
                        id: Date.now() + '_ratelimit',
                        content: `⏳ ${response.data.error}\n\n💡 **Mẹo**: Bạn có thể chat trực tiếp với nhân viên tư vấn trong lúc chờ!`,
                        isAi: true,
                        timestamp: new Date(),
                        isError: true,
                        isRateLimit: true
                    };

                    setAiMessages(prev => [...prev, rateLimitMessage]);
                    setError(response.data.error);

                    const rateLimitError = new Error(response.data.error);
                    rateLimitError.isRateLimit = true;
                    rateLimitError.rateLimitInfo = {
                        remainingMessages: response.data.remainingMessages,
                        resetTime: response.data.rateLimitResetTime
                    };
                    throw rateLimitError;
                }

                if (response.data.success === false) {
                    throw new Error(response.data.error || response.data.message || 'AI không thể phản hồi');
                }

                const aiMessage = {
                    id: Date.now() + '_ai',
                    content: response.data.message || 'AI đã phản hồi nhưng không có nội dung',
                    isAi: true,
                    timestamp: new Date(),
                    recommendations: response.data.recommendations || []
                };

                setAiMessages(prev => [...prev, aiMessage]);
                setError(null);

                setRateLimitInfo({
                    remainingMessages: null,
                    resetTime: null,
                    isRateLimited: false
                });

                return response.data;
            } else {
                throw new Error('Phản hồi từ server không hợp lệ');
            }
        } catch (error) {
            console.error('Error sending AI message:', error);

            let errorMessage = 'Có lỗi xảy ra khi gửi tin nhắn';

            if (error.needAuth) {
                errorMessage = 'Vui lòng đăng nhập để sử dụng tính năng này';
            } else if (error.isRateLimit) {
                const enhancedError = new Error(error.message);
                enhancedError.needAuth = false;
                enhancedError.isRateLimit = true;
                enhancedError.rateLimitInfo = error.rateLimitInfo;
                throw enhancedError;
            } else if (error.response) {
                const { status, data } = error.response;

                if (status === 401 || status === 403) {
                    errorMessage = 'Vui lòng đăng nhập để sử dụng tính năng n��y';
                    error.needAuth = true;
                } else if (status === 400) {
                    errorMessage = data.message || data.error || 'Dữ liệu không hợp lệ';
                } else if (status === 500) {
                    errorMessage = 'Lỗi server, vui lòng thử lại sau';
                } else {
                    errorMessage = data.message || data.error || `Lỗi ${status}`;
                }
            } else if (error.message) {
                errorMessage = error.message;
            }

            if (!error.isRateLimit) {
                const errorMsgObj = {
                    id: Date.now() + '_error',
                    content: `❌ ${errorMessage}`,
                    isAi: true,
                    timestamp: new Date(),
                    isError: true
                };

                setAiMessages(prev => [...prev, errorMsgObj]);
            }

            setError(errorMessage);

            const enhancedError = new Error(errorMessage);
            enhancedError.needAuth = error.needAuth;
            enhancedError.isRateLimit = error.isRateLimit;
            enhancedError.rateLimitInfo = error.rateLimitInfo;
            enhancedError.originalError = error;
            throw enhancedError;
        } finally {
            setLoading(false);
        }
    };

    const sendQuickResponse = async (responseKey) => {
        const message = quickResponses[responseKey];
        if (!message) {
            throw new Error('Phản hồi nhanh không hợp lệ');
        }
        return await sendAiMessage(message);
    };

    const getProductRecommendation = async (skinType, concern) => {
        if (!userId) {
            const authError = new Error('Vui lòng đăng nhập để s�� dụng tính năng này');
            authError.needAuth = true;
            throw authError;
        }

        if (!skinType || !concern) {
            throw new Error('Vui lòng cung cấp thông tin loại da và mối quan tâm');
        }

        try {
            const response = await authApis().get(endpoints['ai-chat-recommendation'], {
                params: {
                    skinType: skinType.trim(),
                    concern: concern.trim()
                }
            });
            return response.data;
        } catch (error) {
            console.error('Error getting recommendation:', error);

            if (error.response?.status === 401 || error.response?.status === 403) {
                const authError = new Error('Vui lòng đăng nhập để sử dụng tính năng này');
                authError.needAuth = true;
                throw authError;
            }

            throw new Error(error.response?.data?.message || 'Lỗi khi lấy gợi ý sản phẩm');
        }
    };

    const clearAiChat = () => {
        setAiMessages([]);
        setError(null);
        setRateLimitInfo({
            remainingMessages: null,
            resetTime: null,
            isRateLimited: false
        });
        startNewConversation().catch(error => {
            console.error('Error reloading welcome message:', error);
            setError('Không thể tải lại tin nhắn chào mừng');
        });
    };

    const startNewConversation = async () => {
        if (!userId) {
            const authError = new Error('Vui lòng đăng nhập để sử dụng tính năng này');
            authError.needAuth = true;
            throw authError;
        }

        try {
            setLoading(true);

            const response = await authApis().post(endpoints['ai-chat-new-conversation']);

            if (response.data && response.data.success) {
                const welcomeMessage = {
                    id: 'new_conversation_welcome',
                    content: response.data.message,
                    isAi: true,
                    timestamp: new Date(),
                    recommendations: []
                };

                setAiMessages([welcomeMessage]);
                setError(null);

                setRateLimitInfo({
                    remainingMessages: null,
                    resetTime: null,
                    isRateLimited: false
                });

                return response.data;
            } else {
                throw new Error(response.data?.error || 'Không thể bắt đầu cuộc trò chuyện mới');
            }
        } catch (error) {
            console.error('Error starting new conversation:', error);

            let errorMessage = 'Không thể bắt đầu cuộc trò chuyện mới';

            if (error.needAuth || error.response?.status === 401 || error.response?.status === 403) {
                errorMessage = 'Vui lòng đăng nhập để sử dụng tính năng này';
                error.needAuth = true;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.message) {
                errorMessage = error.message;
            }

            setError(errorMessage);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    return {
        aiMessages,
        sendAiMessage,
        sendQuickResponse,
        getProductRecommendation,
        clearAiChat,
        startNewConversation,
        loading,
        error,
        aiEnabled,
        quickResponses,
        rateLimitInfo
    };
};
