import React, { useState, useContext } from "react";
import { FloatingLabel, Form, Button, Alert } from "react-bootstrap";
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
    const [errors, setErrors] = useState({});
    const [msg, setMsg] = useState("");
    const [alertType, setAlertType] = useState("danger");
    const nav = useNavigate();
    const [q] = useSearchParams();
    const cartDispatch = useContext(CartDispatchContext);
    
    const info = [
        { label: "Tên đăng nhập", type: "text", field: "username" },
        { label: "Mật khẩu", type: "password", field: "password" }
    ];

    // Kiểm tra OAuth2 errors từ URL parameters
    React.useEffect(() => {
        const oauthError = q.get('error');
        const oauthMessage = q.get('message');

        if (oauthError) {
            let errorMessage = "";
            switch(oauthError) {
                case 'oauth2_failed':
                    errorMessage = "Đăng nhập OAuth2 thất bại: " + (oauthMessage || "Vui lòng thử lại");
                    break;
                case 'email_required':
                    errorMessage = "Email không có sẵn từ nhà cung cấp. Vui lòng sử dụng phương thức đăng nhập khác.";
                    break;
                case 'no_token':
                    errorMessage = "Không nhận được token xác thực. Vui lòng thử lại.";
                    break;
                case 'callback_failed':
                    errorMessage = "Xử lý đăng nhập thất bại. Vui lòng thử lại.";
                    break;
                default:
                    errorMessage = "Đăng nhập thất bại. Vui lòng thử lại.";
            }
            setMsg(errorMessage);
            setAlertType("danger");
        }
    }, [q]);

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
        // Xóa error khi user bắt đầu nhập
        if (errors[field]) {
            setErrors({ ...errors, [field]: null });
        }
        if (msg) {
            setMsg("");
        }
    };

    // Frontend validation functions
    const validateUsername = (username) => {
        if (!username) return "Tên đăng nhập không được để trống";
        if (username.length < 3 || username.length > 20) return "Tên đăng nhập phải từ 3-20 ký tự";
        return null;
    };

    const validatePassword = (password) => {
        if (!password) return "Mật khẩu không được để trống";
        return null;
    };

    const validateForm = () => {
        const newErrors = {};

        newErrors.username = validateUsername(user.username);
        newErrors.password = validatePassword(user.password);

        // Lọc bỏ các error null
        const filteredErrors = Object.keys(newErrors).reduce((acc, key) => {
            if (newErrors[key]) acc[key] = newErrors[key];
            return acc;
        }, {});

        setErrors(filteredErrors);
        return Object.keys(filteredErrors).length === 0;
    };

    const login = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            setMsg("Vui lòng kiểm tra lại thông tin đã nhập");
            setAlertType("danger");
            return;
        }

        try {
            setLoading(true);
            setMsg("");
            setErrors({});
            
            let res = await Apis.post(endpoints['login'], { ...user });
            
            if (!res.data.accessToken) {
                setMsg("Không nhận được token từ server!");
                setAlertType("danger");
                return;
            }
            
            cookie.save('token', res.data.accessToken, { path: '/' });
            let userInfo = await authApis().get(endpoints['my-profile']);
            dispatch({ type: "login", payload: userInfo.data });
            let cartRes = await authApis().get(endpoints["cartCount"]);
            cartDispatch({ type: "set", payload: cartRes.data });
            
            let next = q.get('next');
            nav(next ? next : '/homepage');
        } catch (error) {
            if (error.response?.data) {
                const backendErrors = error.response.data;
                if (typeof backendErrors === 'object' && !backendErrors.message) {
                    setErrors(backendErrors);
                    setMsg("Vui lòng kiểm tra lại thông tin đã nhập");
                } else {
                    setMsg(backendErrors.message || "Đăng nhập thất bại!");
                }
            } else {
                setMsg("Đăng nhập thất bại!");
            }
            setAlertType("danger");
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
                    width: "500px",
                    height: "600px",
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

                {msg && (
                    <Alert variant={alertType} className="py-2 mb-3">
                        {msg}
                    </Alert>
                )}

                <Form onSubmit={login}>
                    {info.map(f => (
                        <div key={f.field}>
                            <FloatingLabel
                                controlId={f.field}
                                label={f.label}
                                className="mb-2"
                            >
                                <Form.Control
                                    type={f.type}
                                    placeholder={f.label}
                                    value={user[f.field] || ""}
                                    onChange={e => setState(e.target.value, f.field)}
                                    style={{
                                        background: "#fff9f9",
                                        borderColor: errors[f.field] ? "#dc3545" : "#E0B7B3",
                                        height: "48px"
                                    }}
                                />
                            </FloatingLabel>
                            {errors[f.field] && (
                                <div className="text-danger mb-2" style={{ fontSize: "12px" }}>
                                    {errors[f.field]}
                                </div>
                            )}
                        </div>
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

                {/* Divider */}
                <div style={{
                    display: "flex",
                    alignItems: "center",
                    margin: "20px 0",
                    color: "#999"
                }}>
                    <hr style={{ flex: 1, border: "none", height: "1px", backgroundColor: "#ddd" }} />
                    <span style={{ padding: "0 15px", fontSize: "14px" }}>hoặc</span>
                    <hr style={{ flex: 1, border: "none", height: "1px", backgroundColor: "#ddd" }} />
                </div>

                {/* OAuth2 Login Buttons */}
                <div style={{ marginBottom: "20px" }}>
                    {/* Google Login Button */}
                    <Button
                        variant="outline-secondary"
                        className="w-100 mb-2"
                        style={{
                            height: "45px",
                            border: "1px solid #ddd",
                            backgroundColor: "#fff",
                            color: "#333",
                            fontWeight: "500",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            gap: "10px"
                        }}
                        onClick={() => window.location.href = "http://localhost:8080/oauth2/authorization/google"}
                    >
                        <img
                            src="https://developers.google.com/identity/images/g-logo.png"
                            alt="Google"
                            style={{ width: "18px", height: "18px" }}
                        />
                        Đăng nhập bằng Google
                    </Button>

                    {/* Facebook Login Button */}
                    <Button
                        variant="outline-primary"
                        className="w-100"
                        style={{
                            height: "45px",
                            backgroundColor: "#1877f2",
                            border: "1px solid #1877f2",
                            color: "#fff",
                            fontWeight: "500",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            gap: "10px"
                        }}
                        onClick={() => window.location.href = "http://localhost:8080/oauth2/authorization/facebook"}
                    >
                        <i className="fab fa-facebook-f" style={{ fontSize: "18px" }}></i>
                        Đăng nhập bằng Facebook
                    </Button>
                </div>

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