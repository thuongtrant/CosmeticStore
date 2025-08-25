import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = "http://localhost:8080";

export const endpoints = {
    'register': '/api/auth/signup',
    'login': '/api/auth/signin',
    'my-profile': '/api/secure/user/my-profile',

    // 'listProduct': '/api/products/list',
    // 'productsByType': (type, limit) => `/api/products/by-type?productType=${type}&limit=${limit}`,
    // 'productsByType': (type, limit) => `/api/products/by-type?productType=${type}&limit=${limit}`,

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

    'checkout': '/api/payment/checkout',
    'checkoutSid':sid=> `/api/payment/momo/check-order/${sid}`,
    'processPayment': orderNumber => `/api/payment/process/${orderNumber}`,
    'orders': '/api/payment/orders',
    'orderDetail': orderNumber => `/api/payment/order/${orderNumber}`,
    'paymentMethods': '/api/payment/methods',
};
export const authApis = () => {
    const token = cookie.load('token');
    console.log("Token from cookie:", token); // Debug

    if (!token) {
        console.error("No token found in cookie!");
        throw new Error("No authentication token found");
    }

    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${token}`
        }

    });

}
export default axios.create({
    baseURL: BASE_URL,
})