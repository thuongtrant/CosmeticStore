import React, { useState, useContext } from "react";
import { FloatingLabel, Form, Button } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import Apis from "../configs/Apis";
import cookie from "react-cookies";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { MyDispatchContext } from "./../configs/MyContexts";
import { CartDispatchContext } from "../configs/CartContext";
const Login = () => {
    const dispatch = useContext(MyDispatchContext);
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();
    const [q] = useSearchParams();
    const cartDispatch = useContext(CartDispatchContext);
    const info = [
        { label: "Tên đăng nhập", type: "text", field: "username" },
        { label: "Mật khẩu", type: "password", field: "password" }
    ];

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const login = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);
            let res = await Apis.post(endpoints['login'], { ...user });
            if (!res.data.accessToken) {
                alert("Không nhận được token từ server!");
                return;
            }
            cookie.save('token', res.data.accessToken, { path: '/' });
            let userInfo = await authApis().get(endpoints['my-profile']);
            dispatch({ type: "login", payload: userInfo.data });
            let cartRes = await authApis().get(endpoints["cartCount"]);
            cartDispatch({ type: "set", payload: cartRes.data });
            let next = q.get('next');
            nav(next ? next : '/home');
        } catch (e) {
            alert("Đăng nhập thất bại!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div
            style={{
                height: "100vh",
                backgroundImage: `url('/images/login-bg1.png')`,
                backgroundSize: "cover",
                backgroundPosition: "center",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                position: "relative"
            }}
        >
            {/* Lớp overlay mờ nhẹ */}
            <div
                style={{
                    position: "absolute",
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    backgroundColor: "rgba(255, 255, 255, 0.12)",
                    backdropFilter: "blur(1.5px)",
                    pointerEvents: "none",
                    zIndex: 1
                }}
            ></div>

            {/* Form login */}
            <div
                style={{
                    width: "400px",
                    height: "500px",
                    backgroundColor: "white",
                    borderRadius: "12px",
                    boxShadow: "0 4px 25px rgba(0,0,0,0.15)",
                    padding: "40px 30px",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "center",
                    zIndex: 2
                }}
            >
                <h4
                    style={{
                        color: "#E0B7B3",
                        textAlign: "center",
                        marginBottom: "30px",
                        fontWeight: "bold"
                    }}
                >
                    ĐĂNG NHẬP
                </h4>

                <Form onSubmit={login}>
                    {info.map(f => (
                        <FloatingLabel
                            key={f.field}
                            controlId={f.field}
                            label={f.label}
                            className="mb-3"
                        >
                            <Form.Control
                                type={f.type}
                                placeholder={f.label}
                                value={user[f.field] || ""}
                                onChange={e => setState(e.target.value, f.field)}
                                style={{
                                    background: "#fff9f9",
                                    borderColor: "#E0B7B3",
                                    height: "48px"
                                }}
                            />
                        </FloatingLabel>
                    ))}

                    <div style={{ textAlign: "right", marginBottom: "20px" }}>
                        <Link
                            to="/forgot-password"
                            style={{ fontSize: "13px", color: "#E0B7B3" }}
                        >
                            Quên mật khẩu?
                        </Link>
                    </div>

                    <Button
                        type="submit"
                        disabled={loading}
                        className="w-100"
                        style={{
                            backgroundColor: "#E0B7B3",
                            border: "none",
                            height: "45px",
                            fontWeight: "bold",
                            fontSize: "16px"
                        }}
                    >
                        {loading ? "Đang đăng nhập..." : "Đăng nhập"}
                    </Button>
                </Form>

                <div
                    style={{
                        textAlign: "center",
                        fontSize: "14px",
                        marginTop: "25px"
                    }}
                >
                    Bạn mới biết đến BFY?{" "}
                    <Link
                        to="/register"
                        style={{ color: "#E0B7B3", fontWeight: "bold" }}
                    >
                        Đăng ký
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Login;
