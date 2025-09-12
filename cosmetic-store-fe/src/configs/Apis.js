import axios from "axios";
import cookie from "react-cookies";

// const BASE_URL = "http://localhost:8080";
const BASE_URL = `http://${window.location.hostname}:8080`;

export const endpoints = {
    'register': '/api/auth/signup',
    'login': '/api/auth/signin',
    'my-profile': '/api/secure/user/my-profile',

    'productDetail': productId => `/api/products/${productId}/detail`,
    'productsAllPaged': (page, size) => `/api/products/paged?page=${page}&size=${size}`,
    'productsByTypePaged': (type, page, size) => `/api/products/by-type/paged?productType=${type}&page=${page}&size=${size}`,
    'search': '/api/products/search',

    'addToCart': '/api/cart/add',
    'cartCount': '/api/cart/count',
    'cart': '/api/cart',
    'cartRemove': productId => `/api/cart/remove/${productId}`,
    'cartUpdate': (productId, quantity) => `/api/cart/update/${productId}?quantity=${quantity}`,
    'categories': '/api/filters/categories',
    'ingredients': '/api/filters/ingredients',
    'skin-types': '/api/filters/skin-types',
    'shippingAddress': '/api/shipping-address',
    'defaultAddress': '/api/shipping-address/default',

    'checkout': '/api/checkout/orders',
    'checkoutSid': sid => `/api/payment/momo/check-order/${sid}`,
    'processPayment': orderNumber => `/api/checkout/process/${orderNumber}`,
    'orders': '/api/checkout/orders',
    'orderDetail': orderNumber => `/api/checkout/orders/${orderNumber}`,
    'paymentMethods': '/api/checkout/methods',

    'chat-init': '/api/chat/init',
    'chat-validate': '/api/chat/validate-user',
    'chat-send': '/api/chat/send',
    'firebase-config': '/api/firebase/config',

    'ai-chat-message': '/api/ai-chat/message',
    'ai-chat-status': '/api/ai-chat/status',
    'ai-chat-quick-responses': '/api/ai-chat/quick-responses',
    'ai-chat-recommendation': '/api/ai-chat/recommendation',
    'ai-chat-new-conversation': '/api/ai-chat/new-conversation',
};
// export const authApis = () => {
//     const token = cookie.load('token');
//     console.log("Token from cookie:", token); // Debug

//     if (!token) {
//         console.error("No token found in cookie!");
//         throw new Error("No authentication token found");
//     }

//     return axios.create({
//         baseURL: BASE_URL,
//         headers: {
//             'Authorization': `Bearer ${token}`
//         }

//     });

// }
export const authApis = () => {
    const token = cookie.load('token');
    
    const instance = axios.create({
        baseURL: BASE_URL,
        headers: token ? {
            'Authorization': `Bearer ${token}`
        } : {}
    });

    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response?.status === 401 || error.response?.status === 403) {
                const customError = new Error("Vui lòng đăng nhập để sử dụng tính năng này");
                customError.needAuth = true;
                customError.originalError = error;
                throw customError;
            }
            throw error;
        }
    );

    return instance;
}
export default axios.create({
    baseURL: BASE_URL,
})