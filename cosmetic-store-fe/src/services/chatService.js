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
    enableNetwork
} from 'firebase/firestore';
import { db } from '../configs/firebase';

export const useChatService = (customerId) => {
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!customerId) return;

        const chatRoomId = `customer_${customerId}`;
        const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');
        const q = query(messagesRef, orderBy('timestamp', 'asc'));

        const unsubscribe = onSnapshot(
            q,
            (snapshot) => {
                const messagesList = snapshot.docs.map(doc => ({
                    id: doc.id,
                    ...doc.data()
                }));
                setMessages(messagesList);
            },
            (error) => {
                console.error('Error listening to messages:', error);
            }
        );

        return () => unsubscribe();
    }, [customerId]);

    const sendMessage = async (content, customerName, isRetry = false) => {
        if (!customerId || !content.trim()) return;

        setLoading(true);
        const chatRoomId = `customer_${customerId}`;
        const chatRoomRef = doc(db, 'chatRooms', chatRoomId);
        const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');

        try {
            // kiểm tra chatRoom đã tồn tại chưa
            const chatRoomSnap = await getDoc(chatRoomRef);
            if (!chatRoomSnap.exists()) {
                console.log('Creating new chat room');
                await setDoc(chatRoomRef, {
                    customerId: String(customerId),
                    customerName: String(customerName),
                    lastMessage: String(content),
                    lastMessageTime: serverTimestamp(),
                    unreadCount: 0
                });
            } else {
                console.log('Updating existing chat room');
                await setDoc(chatRoomRef, {
                    lastMessage: String(content),
                    lastMessageTime: serverTimestamp(),
                    unreadCount: 0
                }, { merge: true });
            }

            console.log('Adding message to collection');
            await addDoc(messagesRef, {
                content: String(content),
                senderId: String(customerId),
                senderName: String(customerName),
                isAdmin: false,
                timestamp: serverTimestamp()
            });

        } catch (error) {
            console.error('Error sending message:', error);

            if (error.code === 'permission-denied') {
                alert('Không có quyền gửi tin nhắn. Vui lòng kiểm tra cấu hình Firebase Security Rules.');
            } else if (error.code === 'unavailable' || (error.message && error.message.includes('offline'))) {
                if (!isRetry) {
                    try {
                        console.log('Attempting to re-enable Firebase network...');
                        await enableNetwork(db);
                        console.log('Network re-enabled, retrying message send...');
                        return await sendMessage(content, customerName, true);
                    } catch (networkError) {
                        console.error('Network re-enable failed:', networkError);
                        alert('Không thể kết nối đến Firebase. Vui lòng:\n1. Kiểm tra kết nối internet\n2. Refresh trang web\n3. Thử lại');
                    }
                } else {
                    alert('Vẫn không thể kết nối sau khi thử lại. Vui lòng kiểm tra internet và refresh trang.');
                }
            } else {
                alert('Lỗi khi gửi tin nhắn: ' + (error.message || error.code || 'Unknown error'));
            }
        } finally {
            setLoading(false);
        }
    };

    return { messages, sendMessage, loading };
};

export const chatService = {
    getAllChatRooms: async () => {
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
    },

    sendAdminMessage: async (chatRoomId, content, adminName = 'Admin') => {
        try {
            const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');
            await addDoc(messagesRef, {
                content: String(content),
                senderId: 'admin',
                senderName: String(adminName),
                isAdmin: true,
                timestamp: serverTimestamp()
            });

            const chatRoomRef = doc(db, 'chatRooms', chatRoomId);
            await setDoc(chatRoomRef, {
                lastMessage: String(content),
                lastMessageTime: serverTimestamp(),
                unreadCount: 1
            }, { merge: true });

        } catch (error) {
            console.error('Error sending admin message:', error);
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
    }
};
