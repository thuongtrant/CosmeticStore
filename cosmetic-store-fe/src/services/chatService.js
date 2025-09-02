import { useState, useEffect } from 'react';
import {
    collection,
    addDoc,
    query,
    orderBy,
    onSnapshot,
    serverTimestamp,
    doc,
    setDoc
} from 'firebase/firestore';
import { db } from '../configs/firebase';
import { authApis, endpoints } from '../configs/Apis';

export const useChatService = (customerId) => {
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Set up Firebase listener
    useEffect(() => {
        if (!customerId) return;

        console.log('Setting up chat listener for customer:', customerId);
        const chatRoomId = `customer_${customerId}`;
        const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');
        const q = query(messagesRef, orderBy('timestamp', 'asc'));

        const unsubscribe = onSnapshot(
            q,
            (snapshot) => {
                console.log('Messages snapshot received:', snapshot.size, 'messages');
                const messagesList = snapshot.docs.map(doc => ({
                    id: doc.id,
                    ...doc.data()
                }));
                setMessages(messagesList);
                setError(null);
            },
            (error) => {
                console.error('Error listening to messages:', error);
                setError('Lỗi kết nối chat: ' + error.message);
            }
        );

        return () => unsubscribe();
    }, [customerId]);

    const sendMessage = async (content, customerName) => {
        if (!customerId || !content?.trim()) {
            throw new Error('Thông tin không hợp lệ');
        }

        setLoading(true);
        try {
            console.log('Sending message:', content, 'from customer:', customerName);
            const chatRoomId = `customer_${customerId}`;
            const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');

            // Add message to Firebase
            await addDoc(messagesRef, {
                content: String(content),
                senderId: String(customerId),
                senderName: String(customerName),
                isAdmin: false,
                timestamp: serverTimestamp(),
                createdAt: new Date()
            });

            console.log('Message added to Firebase successfully');

            // Update chat room
            const chatRoomRef = doc(db, 'chatRooms', chatRoomId);
            await setDoc(chatRoomRef, {
                customerId: String(customerId),
                customerName: String(customerName),
                lastMessage: String(content),
                lastMessageTime: serverTimestamp(),
                unreadCount: 1, // Admin chưa đọc
                createdAt: serverTimestamp()
            }, { merge: true });

            console.log('Chat room updated successfully');

            // Notify backend (optional - for future features like notifications)
            try {
                await authApis().post(endpoints['chat-send'], {
                    message: content
                });
                console.log('Backend notification sent successfully');
            } catch (backendError) {
                console.warn('Backend notification failed (non-critical):', backendError);
            }

        } catch (error) {
            console.error('Error sending message:', error);
            throw new Error('Không thể gửi tin nhắn: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    return { messages, sendMessage, loading, error };
};