# Cơ chế lưu trữ và hoạt động của Chat System

## 1. Firebase Firestore Structure

### Chat Rooms Collection
```json
{
  "chatRooms": {
    "customer_7": {
      "customerId": "7",
      "customerName": "customer@gmail.com", 
      "lastMessage": "hello",
      "lastMessageTime": "2024-12-19T10:30:00Z",
      "isActive": true,
      "unreadCount": 0,
      "messages": {
        "msg_001": {
          "content": "hello",
          "senderId": "7",
          "senderName": "customer@gmail.com",
          "isAdmin": false,
          "timestamp": "2024-12-19T10:30:00Z"
        },
        "msg_002": {
          "content": "Hi! How can I help you?",
          "senderId": "admin", 
          "senderName": "Admin",
          "isAdmin": true,
          "timestamp": "2024-12-19T10:31:00Z"
        }
      }
    }
  }
}
```

## 2. Data Flow Architecture

### Customer Side (React):
1. User login → Get customerId from backend JWT
2. Open chat → Create/Find chatRoom: `customer_${customerId}`
3. Send message → Add to Firestore `chatRooms/{roomId}/messages`
4. Listen for responses → onSnapshot for real-time updates

### Admin Side (Thymeleaf):
1. Admin login → Access chat management page
2. Load chat list → Query all chatRooms ordered by lastMessageTime
3. Select chat → Load messages from specific chatRoom
4. Send reply → Add message with isAdmin: true

## 3. Real-time Synchronization

### React Frontend:
```javascript
// Listen for new messages
const messagesRef = collection(db, 'chatRooms', chatRoomId, 'messages');
const q = query(messagesRef, orderBy('timestamp', 'asc'));

onSnapshot(q, (snapshot) => {
  const messages = snapshot.docs.map(doc => ({
    id: doc.id,
    ...doc.data()
  }));
  setMessages(messages);
});
```

### Thymeleaf Admin:
```javascript
// Same real-time listening mechanism
onSnapshot(q, (snapshot) => {
  snapshot.forEach((doc) => {
    const message = { id: doc.id, ...doc.data() };
    displayMessage(message);
  });
});
```

## 4. Security Rules

### Firestore Rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /chatRooms/{chatRoomId} {
      allow read: if true;
      allow write: if request.resource.data.customerId is string;
      
      match /messages/{messageId} {
        allow read: if true;
        allow create: if request.resource.data.content is string;
      }
    }
  }
}
```

## 5. Data Persistence

### Advantages:
- ✅ Real-time updates across all devices
- ✅ Automatic scaling with Firebase
- ✅ Offline support (cached locally)
- ✅ No server maintenance for chat data
- ✅ Global CDN for fast access

### Data Location:
- 🌐 Google Cloud Firestore (multi-region)
- 📱 Local cache on devices
- 🔄 Automatic sync when online

## 6. Backup & Recovery

### Firebase handles:
- Automatic backups
- Point-in-time recovery
- Multi-region replication
- 99.99% uptime SLA

## 7. Cost Structure

### Firebase Pricing:
- Read operations: $0.06 per 100K
- Write operations: $0.18 per 100K  
- Storage: $0.18 per GB/month
- Network egress: $0.12 per GB

### Estimated monthly cost for 1000 active users:
- ~$5-10 USD depending on chat volume

## 8. Integration Points

### Spring Boot Backend:
- Provides user authentication
- Validates chat permissions via ApiChatController
- Can log chat analytics to MySQL if needed

### MySQL Database:
- Stores user accounts, orders, products
- Can optionally store chat metadata for reporting
- Does NOT store actual chat messages

## 9. Monitoring & Analytics

### Available metrics:
- Real-time active users
- Message volume per day
- Response times
- Popular chat times
- Customer satisfaction (if implemented)

## 10. Scalability

### Current capacity:
- Unlimited concurrent users
- 1M+ messages per day
- Real-time sync across continents
- Auto-scaling with Firebase

### Future enhancements possible:
- File/image sharing
- Voice messages
- Chat bots integration
- Advanced analytics
- Multi-language support
