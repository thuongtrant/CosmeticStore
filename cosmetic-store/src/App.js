import { BrowserRouter, Route, Routes, useLocation, Navigate } from "react-router-dom";
import Footer from "./components/layout/Footer";
import Header from "./components/layout/Header";
import Home from "./components/Home";
import 'bootstrap/dist/css/bootstrap.min.css';
import Register from "./components/Register";
import Login from "./components/Login";
import { MyDispatchContext, MyUserContext } from "./configs/MyContexts";
import { useReducer, useEffect } from "react";
import MyUserReducer from "./reducers/MyUserReducer";
import { authApis, endpoints } from "./configs/Apis";
import cookie from "react-cookies";
import { CartContext, CartDispatchContext } from "./configs/CartContext";
import cartReducer from "./reducers/CartReducer";
import ProductDetail from "./components/Customer/ProductDetail";
import Cart from "./components/Customer/Cart";
import Checkout from "./components/Customer/Checkout";
import ShippingAddress  from "./components/Customer/ShippingAddress";
function AppLayout() {
  const location = useLocation();

  // Không hiển thị header/footer nếu ở login hoặc register
  const hideHeaderFooter = ["/login", "/register"].includes(location.pathname);

  return (
    <>
      {!hideHeaderFooter && <Header />}
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/home" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/productdetail/:productId" element={<ProductDetail />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/shippingAddress" element={<ShippingAddress />} />
      </Routes>
      {!hideHeaderFooter && <Footer />}
    </>
  );
}

const App = () => {
  const [cartCount, cartDispatch] = useReducer(cartReducer, 0);
  const [user, dispatch] = useReducer(MyUserReducer, null);

  useEffect(() => {
    const loadUser = async () => {
      const token = cookie.load("token");
      if (token !== undefined) {
        try {
          const res = await authApis().get(endpoints['my-profile']);
          dispatch({ type: "login", payload: res.data });

          // Lấy số lượng giỏ hàng ban đầu
          const cartRes = await authApis().get(endpoints["cartCount"]);
          cartDispatch({ type: "set", payload: cartRes.data });
        } catch (err) {
          cookie.remove("token");
          dispatch({ type: "logout" });
        }
      }
    };
    loadUser();
  }, []);

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatchContext.Provider value={dispatch}>
        <CartContext.Provider value={cartCount}>
          <CartDispatchContext.Provider value={cartDispatch}>
            <BrowserRouter>
              <AppLayout />
            </BrowserRouter>
          </CartDispatchContext.Provider>
        </CartContext.Provider>
      </MyDispatchContext.Provider>
    </MyUserContext.Provider>
  );
};

export default App;
