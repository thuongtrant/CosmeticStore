import { useState, useEffect } from 'react';
import { 
    collection, 
    addDoc, 
    query, 
    orderBy, 
    onSnapshot,
    serverTimestamp,
    doc,
    setDoc,
    getDoc,
    enableNetwork,
    disableNetwork
} from 'firebase/firestore';
import { db } from '../configs/firebase';

export const useChatService = (customerId) => {
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!customerId) {
            console.warn('No customerId provided to chat service');
            return;
        }

        console.log('Setting up chat listener for customer:', customerId);
        setError(null);

        // Thử cả chatRooms và conversations collections
        const chatRoomId = `customer_${customerId}`;
        
        // Thử chatRooms trước
        let messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');
        let q = query(messagesRef, orderBy('timestamp', 'asc'));

        const unsubscribe = onSnapshot(
            q,
            (snapshot) => {
                console.log('Messages snapshot received from chatRooms:', snapshot.size, 'messages');
                const messagesList = snapshot.docs.map(doc => ({
                    id: doc.id,
                    ...doc.data()
                }));
                setMessages(messagesList);
                setError(null);
            },
            (error) => {
                console.error('Error listening to chatRooms messages:', error);
                
                // Nếu chatRooms không hoạt động, thử conversations
                console.log('Trying conversations collection...');
                const conversationId = `test-conversation-001`; // Dựa vào cấu trúc Firebase hiện tại
                const conversationsRef = collection(db, 'conversations', conversationId, 'messages');
                const conversationsQuery = query(conversationsRef, orderBy('timestamp', 'asc'));
                
                const conversationsUnsubscribe = onSnapshot(
                    conversationsQuery,
                    (snapshot) => {
                        console.log('Messages snapshot received from conversations:', snapshot.size, 'messages');
                        const messagesList = snapshot.docs.map(doc => ({
                            id: doc.id,
                            ...doc.data()
                        }));
                        setMessages(messagesList);
                        setError(null);
                    },
                    (conversationsError) => {
                        console.error('Error listening to conversations messages:', conversationsError);
                        setError('Không thể tải tin nhắn. Lỗi: ' + (conversationsError.message || conversationsError.code));

                        // Thử kết nối lại
                        setTimeout(() => {
                            console.log('Attempting to reconnect...');
                            enableNetwork(db).then(() => {
                                console.log('Network re-enabled successfully');
                            }).catch(err => {
                                console.error('Failed to re-enable network:', err);
                            });
                        }, 3000);
                    }
                );

                return conversationsUnsubscribe;
            }
        );

        return () => {
            console.log('Cleaning up chat listener');
            unsubscribe();
        };
    }, [customerId]);

    const sendMessage = async (content, customerName, isRetry = false) => {
        if (!customerId || !content?.trim()) {
            console.warn('Invalid parameters for sendMessage:', { customerId, content });
            return;
        }

        setLoading(true);
        setError(null);

        try {
            console.log('Sending message:', { customerId, content, customerName });

            // Thử gửi vào cả chatRooms và conversations
            const chatRoomId = `customer_${customerId}`;
            const chatRoomRef = doc(db, 'chatRooms', chatRoomId);
            const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');

            // Thêm tin nhắn trước
            const messageData = {
                content: String(content),
                senderId: String(customerId),
                senderName: String(customerName || 'Khách hàng'),
                isAdmin: false,
                timestamp: serverTimestamp(),
                createdAt: new Date()
            };

            await addDoc(messagesRef, messageData);
            console.log('Message sent to chatRooms successfully');

            // Cập nhật hoặc tạo chatRoom
            await setDoc(chatRoomRef, {
                customerId: String(customerId),
                customerName: String(customerName || 'Khách hàng'),
                lastMessage: String(content),
                lastMessageTime: serverTimestamp(),
                unreadCount: 0,
                createdAt: serverTimestamp()
            }, { merge: true });

            console.log('ChatRoom updated successfully');

        } catch (error) {
            console.error('Error sending message to chatRooms:', error);

            // Nếu chatRooms thất bại, thử conversations
            try {
                console.log('Trying to send to conversations collection...');
                const conversationId = `test-conversation-001`;
                const conversationRef = doc(db, 'conversations', conversationId);
                const conversationMessagesRef = collection(db, 'conversations', conversationId, 'messages');

                const messageData = {
                    content: String(content),
                    senderId: String(customerId),
                    senderName: String(customerName || 'Khách hàng'),
                    isAdmin: false,
                    timestamp: serverTimestamp(),
                    createdAt: new Date()
                };

                await addDoc(conversationMessagesRef, messageData);
                console.log('Message sent to conversations successfully');

                // Cập nhật conversation
                await setDoc(conversationRef, {
                    lastMessage: {
                        senderId: String(customerId),
                        text: String(content),
                        timestamp: serverTimestamp()
                    },
                    customerInfo: {
                        id: String(customerId),
                        name: String(customerName || 'Khách hàng')
                    },
                    participants: {
                        status: 'active'
                    },
                    updatedAt: serverTimestamp()
                }, { merge: true });

            } catch (conversationError) {
                console.error('Error sending to conversations:', conversationError);
                setError('Không thể gửi tin nhắn: ' + (conversationError.message || conversationError.code));
                throw conversationError;
            }
        } finally {
            setLoading(false);
        }
    };

    const clearError = () => setError(null);

    return { messages, sendMessage, loading, error, clearError };
};

export const chatService = {
    getAllChatRooms: async () => {
        try {
            const chatRoomsRef = collection(db, 'chatRooms');
            const q = query(chatRoomsRef, orderBy('lastMessageTime', 'desc'));

            return new Promise((resolve) => {
                const unsubscribe = onSnapshot(
                    q,
                    (snapshot) => {
                        const chatRooms = snapshot.docs.map(doc => ({
                            id: doc.id,
                            ...doc.data()
                        }));
                        resolve({ chatRooms, unsubscribe });
                    },
                    (error) => {
                        console.error('Error getting chat rooms:', error);
                        resolve({ chatRooms: [], unsubscribe: () => {} });
                    }
                );
            });
        } catch (error) {
            console.error('Error in getAllChatRooms:', error);
            return { chatRooms: [], unsubscribe: () => {} };
        }
    },

    sendAdminMessage: async (chatRoomId, content, adminName = 'Admin') => {
        try {
            const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');
            await addDoc(messagesRef, {
                content: String(content),
                senderId: 'admin',
                senderName: String(adminName),
                isAdmin: true,
                timestamp: serverTimestamp(),
                createdAt: new Date()
            });

            const chatRoomRef = doc(db, 'chatRooms', chatRoomId);
            await setDoc(chatRoomRef, {
                lastMessage: String(content),
                lastMessageTime: serverTimestamp(),
                unreadCount: 1
            }, { merge: true });

        } catch (error) {
            console.error('Error sending admin message:', error);
            throw error;
        }
    },

    markAsRead: async (chatRoomId) => {
        try {
            const chatRoomRef = doc(db, 'chatRooms', chatRoomId);
            await setDoc(chatRoomRef, {
                unreadCount: 0
            }, { merge: true });
        } catch (error) {
            console.error('Error marking as read:', error);
        }
    },

    // Thêm function test connection
    testConnection: async () => {
        try {
            console.log('Testing Firebase connection...');
            const testRef = collection(db, 'test');
            const q = query(testRef);

            return new Promise((resolve, reject) => {
                const unsubscribe = onSnapshot(
                    q,
                    (snapshot) => {
                        console.log('Firebase connection test successful');
                        unsubscribe();
                        resolve(true);
                    },
                    (error) => {
                        console.error('Firebase connection test failed:', error);
                        unsubscribe();
                        reject(error);
                    }
                );

                // Timeout after 10 seconds
                setTimeout(() => {
                    unsubscribe();
                    reject(new Error('Connection test timeout'));
                }, 10000);
            });
        } catch (error) {
            console.error('Error in connection test:', error);
            throw error;
        }
    }
};
