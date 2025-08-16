// import React, { useState } from "react";
// import { FloatingLabel, Form, Button, Alert } from "react-bootstrap";
// import Apis, { endpoints } from "../configs/Apis";
// import { useNavigate, Link } from "react-router-dom";
// import MySpinner from "./layout/MySpinner";

// const Register = () => {
//     const info = [
//         { label: "Tên đăng nhập", type: "text", field: "username" },
//         { label: "Email address", type: "email", field: "email" },
//         { label: "Mật khẩu", type: "password", field: "password" },
//         { label: "Xác nhận mật khẩu", type: "password", field: "confirmPassword" },
//         { label: "Số điện thoại", type: "text", field: "phone" },
//         {
//             label: "Giới tính",
//             type: "select",
//             field: "gender",
//             options: [
//                 { value: "Nam", label: "Nam" },
//                 { value: "Nữ", label: "Nữ" }
//             ]
//         }
//     ];

//     const nav = useNavigate();
//     const [user, setUser] = useState({});
//     const [loading, setLoading] = useState(false);
//     const [msg, setMsg] = useState("");
//     const [alertType, setAlertType] = useState("danger");

//     const setState = (value, field) => {
//         setUser({ ...user, [field]: value });
//     };

//     const register = async (e) => {
//         e.preventDefault();

//         if (user.password !== user.confirmPassword) {
//             setMsg("Mật khẩu không khớp");
//             setAlertType("danger");
//             return;
//         }

//         if (!user.username || !user.email || !user.password || !user.phone || !user.gender) {
//             setMsg("Vui lòng điền đầy đủ thông tin");
//             setAlertType("danger");
//             return;
//         }

//         try {
//             setLoading(true);
//             setMsg("");

//             const registerData = {
//                 username: user.username,
//                 email: user.email,
//                 password: user.password,
//                 phone: user.phone,
//                 gender: user.gender
//             };

//             let res = await Apis.post(endpoints['register'], registerData, {
//                 headers: { 'Content-Type': 'application/json' }
//             });

//             if (res.status === 200) {
//                 setMsg("Đăng ký thành công! Đang chuyển hướng...");
//                 setAlertType("success");
//                 setTimeout(() => nav("/login"), 1500);
//             }
//         } catch (error) {
//             setMsg(error.response?.data?.message || "Đăng ký thất bại");
//             setAlertType("danger");
//         } finally {
//             setLoading(false);
//         }
//     };

//     return (
//         <div
//             style={{
//                 height: "100vh",
//                 backgroundImage: `url('/images/login-bg1.png')`,
//                 backgroundSize: "cover",
//                 backgroundPosition: "center",
//                 display: "flex",
//                 alignItems: "center",
//                 justifyContent: "center",
//                 position: "relative"
//             }}
//         >
//             <div
//                 style={{
//                     position: "absolute",
//                     top: 0,
//                     left: 0,
//                     right: 0,
//                     bottom: 0,
//                     backgroundColor: "rgba(255, 255, 255, 0.12)",
//                     backdropFilter: "blur(1.5px)",
//                     pointerEvents: "none",
//                     zIndex: 1
//                 }}
//             ></div>

//             <div
//                 style={{
//                     width: "450px",
//                     backgroundColor: "white",
//                     borderRadius: "12px",
//                     boxShadow: "0 4px 25px rgba(0,0,0,0.15)",
//                     padding: "40px 30px",
//                     display: "flex",
//                     flexDirection: "column",
//                     justifyContent: "center",
//                     zIndex: 2
//                 }}
//             >
//                 <h4
//                     style={{
//                         color: "#E0B7B3",
//                         textAlign: "center",
//                         marginBottom: "20px",
//                         fontWeight: "bold"
//                     }}
//                 >
//                     ĐĂNG KÝ
//                 </h4>

//                 {msg && (
//                     <Alert variant={alertType} className="py-2">
//                         {msg}
//                     </Alert>
//                 )}

//                 <Form onSubmit={register}>
//                     {info.map(f => {
//                         if (f.type === "select") {
//                             return (
//                                 <FloatingLabel
//                                     key={f.field}
//                                     controlId={`floating-${f.field}`}
//                                     label={f.label}
//                                     className="mb-3"
//                                 >
//                                     <Form.Select
//                                         required
//                                         value={user[f.field] || ""}
//                                         onChange={e => setState(e.target.value, f.field)}
//                                         style={{
//                                             background: "#fff9f9",
//                                             borderColor: "#E0B7B3",
//                                             height: "48px"
//                                         }}
//                                     >
//                                         <option value="">Chọn</option>
//                                         {f.options.map(opt => (
//                                             <option key={opt.value} value={opt.value}>
//                                                 {opt.label}
//                                             </option>
//                                         ))}
//                                     </Form.Select>
//                                 </FloatingLabel>
//                             );
//                         }
//                         return (
//                             <FloatingLabel
//                                 key={f.field}
//                                 controlId={`floating-${f.field}`}
//                                 label={f.label}
//                                 className="mb-3"
//                             >
//                                 <Form.Control
//                                     type={f.type}
//                                     placeholder={f.label}
//                                     required
//                                     value={user[f.field] || ""}
//                                     onChange={e => setState(e.target.value, f.field)}
//                                     style={{
//                                         background: "#fff9f9",
//                                         borderColor: "#E0B7B3",
//                                         height: "48px"
//                                     }}
//                                 />
//                             </FloatingLabel>
//                         );
//                     })}

//                     {loading ? (
//                         <MySpinner />
//                     ) : (
//                         <Button
//                             type="submit"
//                             className="w-100 mt-2"
//                             style={{
//                                 backgroundColor: "#E0B7B3",
//                                 border: "none",
//                                 height: "45px",
//                                 fontWeight: "bold",
//                                 fontSize: "16px"
//                             }}
//                         >
//                             Đăng ký
//                         </Button>
//                     )}
//                 </Form>

//                 <div
//                     style={{
//                         textAlign: "center",
//                         fontSize: "14px",
//                         marginTop: "20px"
//                     }}
//                 >
//                     Bạn đã có tài khoản?{" "}
//                     <Link to="/login" style={{ color: "#E0B7B3", fontWeight: "bold" }}>
//                         Đăng nhập
//                     </Link>
//                 </div>
//             </div>
//         </div>
//     );
// };

// export default Register;

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
    const [errors, setErrors] = useState({});

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
        // Xóa error khi user bắt đầu nhập
        if (errors[field]) {
            setErrors({ ...errors, [field]: null });
        }
    };

    // Frontend validation functions
    const validateUsername = (username) => {
        if (!username) return "Tên đăng nhập không được để trống";
        if (username.length < 3 || username.length > 20) return "Tên đăng nhập phải từ 3-20 ký tự";
        if (!/^[a-zA-Z0-9_]+$/.test(username)) return "Tên đăng nhập chỉ chứa chữ cái, số và dấu gạch dưới";
        return null;
    };

    const validateEmail = (email) => {
        if (!email) return "Email không được để trống";
        if (email.length > 50) return "Email không được vượt quá 50 ký tự";
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) return "Email không đúng định dạng";
        return null;
    };

    const validatePassword = (password) => {
        if (!password) return "Mật khẩu không được để trống";
        if (password.length < 8) return "Mật khẩu phải có ít nhất 8 ký tự";
        if (!/(?=.*[a-z])/.test(password)) return "Mật khẩu phải chứa ít nhất một chữ cái viết thường";
        if (!/(?=.*[A-Z])/.test(password)) return "Mật khẩu phải chứa ít nhất một chữ cái viết hoa";
        if (!/(?=.*[0-9])/.test(password)) return "Mật khẩu phải chứa ít nhất một chữ số";
        if (!/(?=.*[!@#$%^&*()_+\-=\[\]{};':\"\\|,.<>\/?])/.test(password)) return "Mật khẩu phải chứa ít nhất một ký tự đặc biệt";
        return null;
    };

    const validatePhone = (phone) => {
        if (!phone) return "Số điện thoại không được để trống";
        const phoneRegex = /^(0[3|5|7|8|9])+([0-9]{8})$/;
        if (!phoneRegex.test(phone)) return "Số điện thoại phải đúng định dạng Việt Nam (10 số, bắt đầu bằng 03, 05, 07, 08, 09)";
        return null;
    };

    const validateGender = (gender) => {
        if (!gender) return "Giới tính không được để trống";
        if (!["Nam", "Nữ"].includes(gender)) return "Giới tính phải là 'Nam' hoặc 'Nữ'";
        return null;
    };

    const validateForm = () => {
        const newErrors = {};

        newErrors.username = validateUsername(user.username);
        newErrors.email = validateEmail(user.email);
        newErrors.password = validatePassword(user.password);
        newErrors.phone = validatePhone(user.phone);
        newErrors.gender = validateGender(user.gender);

        if (user.password !== user.confirmPassword) {
            newErrors.confirmPassword = "Mật khẩu xác nhận không khớp";
        }

        // Lọc bỏ các error null
        const filteredErrors = Object.keys(newErrors).reduce((acc, key) => {
            if (newErrors[key]) acc[key] = newErrors[key];
            return acc;
        }, {});

        setErrors(filteredErrors);
        return Object.keys(filteredErrors).length === 0;
    };

    const register = async (e) => {
        e.preventDefault();

        // Frontend validation
        if (!validateForm()) {
            setMsg("Vui lòng kiểm tra lại thông tin đã nhập");
            setAlertType("danger");
            return;
        }

        try {
            setLoading(true);
            setMsg("");
            setErrors({});

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
            if (error.response?.data) {
                const backendErrors = error.response.data;
                if (typeof backendErrors === 'object' && !backendErrors.message) {
                    // Backend trả về object errors từ validation
                    setErrors(backendErrors);
                    setMsg("Vui lòng kiểm tra lại thông tin đã nhập");
                } else {
                    // Backend trả về MessageResponse
                    setMsg(backendErrors.message || "Đăng ký thất bại");
                }
            } else {
                setMsg("Đăng ký thất bại");
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
                                <div key={f.field}>
                                    <FloatingLabel
                                        controlId={`floating-${f.field}`}
                                        label={f.label}
                                        className="mb-2"
                                    >
                                        <Form.Select
                                            required
                                            value={user[f.field] || ""}
                                            onChange={e => setState(e.target.value, f.field)}
                                            style={{
                                                background: "#fff9f9",
                                                borderColor: errors[f.field] ? "#dc3545" : "#E0B7B3",
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
                                    {errors[f.field] && (
                                        <div className="text-danger mb-2" style={{ fontSize: "12px" }}>
                                            {errors[f.field]}
                                        </div>
                                    )}
                                </div>
                            );
                        }
                        return (
                            <div key={f.field}>
                                <FloatingLabel
                                    controlId={`floating-${f.field}`}
                                    label={f.label}
                                    className="mb-2"
                                >
                                    <Form.Control
                                        type={f.type}
                                        placeholder={f.label}
                                        required
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