import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = "http://localhost:8080";

export const endpoints = {
    'register': '/api/auth/signup',
    'login': '/api/auth/signin',
    'my-profile': '/api/secure/user/my-profile',

    'listProduct': '/api/products/list',
    'productDetail':productId => `/api/products/${productId}/detail`,

    'addToCart': '/api/cart/add',
    'cartCount':'/api/cart/count',
    'cart':'/api/cart',

    'categories' : '/api/filters/categories',
    'ingredients':'/api/filters/ingredients',
    'skin-types':'/api/filters/skin-types',
    'search':'/api/products/search',
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