import React, { useState } from "react";
import { FloatingLabel, Form, Button, Alert } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import { useNavigate, Link } from "react-router-dom";
import MySpinner from "./layout/MySpinner";

const Register = () => {
    const info = [
        { label: "Tên đăng nhập", type: "text", field: "username" },
        { label: "Email address", type: "email", field: "email" },
        { label: "Mật khẩu", type: "password", field: "password" },
        { label: "Xác nhận mật khẩu", type: "password", field: "confirmPassword" },
        { label: "Số điện thoại", type: "text", field: "phone" },
        {
            label: "Giới tính",
            type: "select",
            field: "gender",
            options: [
                { value: "Nam", label: "Nam" },
                { value: "Nữ", label: "Nữ" }
            ]
        }
    ];

    const nav = useNavigate();
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState("");
    const [alertType, setAlertType] = useState("danger");

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const register = async (e) => {
        e.preventDefault();

        if (user.password !== user.confirmPassword) {
            setMsg("Mật khẩu không khớp");
            setAlertType("danger");
            return;
        }

        if (!user.username || !user.email || !user.password || !user.phone || !user.gender) {
            setMsg("Vui lòng điền đầy đủ thông tin");
            setAlertType("danger");
            return;
        }

        try {
            setLoading(true);
            setMsg("");

            const registerData = {
                username: user.username,
                email: user.email,
                password: user.password,
                phone: user.phone,
                gender: user.gender
            };

            let res = await Apis.post(endpoints['register'], registerData, {
                headers: { 'Content-Type': 'application/json' }
            });

            if (res.status === 200) {
                setMsg("Đăng ký thành công! Đang chuyển hướng...");
                setAlertType("success");
                setTimeout(() => nav("/login"), 1500);
            }
        } catch (error) {
            setMsg(error.response?.data?.message || "Đăng ký thất bại");
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

            <div
                style={{
                    width: "450px",
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
                        marginBottom: "20px",
                        fontWeight: "bold"
                    }}
                >
                    ĐĂNG KÝ
                </h4>

                {msg && (
                    <Alert variant={alertType} className="py-2">
                        {msg}
                    </Alert>
                )}

                <Form onSubmit={register}>
                    {info.map(f => {
                        if (f.type === "select") {
                            return (
                                <FloatingLabel
                                    key={f.field}
                                    controlId={`floating-${f.field}`}
                                    label={f.label}
                                    className="mb-3"
                                >
                                    <Form.Select
                                        required
                                        value={user[f.field] || ""}
                                        onChange={e => setState(e.target.value, f.field)}
                                        style={{
                                            background: "#fff9f9",
                                            borderColor: "#E0B7B3",
                                            height: "48px"
                                        }}
                                    >
                                        <option value="">Chọn</option>
                                        {f.options.map(opt => (
                                            <option key={opt.value} value={opt.value}>
                                                {opt.label}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </FloatingLabel>
                            );
                        }
                        return (
                            <FloatingLabel
                                key={f.field}
                                controlId={`floating-${f.field}`}
                                label={f.label}
                                className="mb-3"
                            >
                                <Form.Control
                                    type={f.type}
                                    placeholder={f.label}
                                    required
                                    value={user[f.field] || ""}
                                    onChange={e => setState(e.target.value, f.field)}
                                    style={{
                                        background: "#fff9f9",
                                        borderColor: "#E0B7B3",
                                        height: "48px"
                                    }}
                                />
                            </FloatingLabel>
                        );
                    })}

                    {loading ? (
                        <MySpinner />
                    ) : (
                        <Button
                            type="submit"
                            className="w-100 mt-2"
                            style={{
                                backgroundColor: "#E0B7B3",
                                border: "none",
                                height: "45px",
                                fontWeight: "bold",
                                fontSize: "16px"
                            }}
                        >
                            Đăng ký
                        </Button>
                    )}
                </Form>

                <div
                    style={{
                        textAlign: "center",
                        fontSize: "14px",
                        marginTop: "20px"
                    }}
                >
                    Bạn đã có tài khoản?{" "}
                    <Link to="/login" style={{ color: "#E0B7B3", fontWeight: "bold" }}>
                        Đăng nhập
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Register;
