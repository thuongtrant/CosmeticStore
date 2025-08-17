import React, { useEffect, useContext, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "../configs/Apis";
import { MyDispatchContext } from "../configs/MyContexts";
import { CartDispatchContext } from "../configs/CartContext";

const OAuth2Redirect = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const dispatch = useContext(MyDispatchContext);
    const cartDispatch = useContext(CartDispatchContext);
    const [status, setStatus] = useState("processing");
    const [errorMsg, setErrorMsg] = useState("");

    useEffect(() => {
        const handleOAuth2Callback = async () => {
            try {

                const token = searchParams.get('token');
                const role = searchParams.get('role');
                const error = searchParams.get('error');
                
                if (error) {
                    const errorMessage = searchParams.get('message') || 'OAuth2 authentication failed';
                    setStatus("error");
                    setErrorMsg(`OAuth2 Error: ${error}`);
                    setTimeout(() => {
                        navigate(`/login?error=oauth2_failed&message=${encodeURIComponent(errorMessage)}`);
                    }, 2000);
                    return;
                }

                if (!token) {
                    console.error('❌ No token received from OAuth2 callback');
                    setStatus("error");
                    setErrorMsg("No authentication token received");
                    setTimeout(() => {
                        navigate('/login?error=no_token');
                    }, 2000);
                    return;
                }

                cookie.save('token', token, { path: '/' });

                setStatus("fetching_profile");

                // Lấy thông tin user profile
                const userInfo = await authApis().get(endpoints['my-profile']);
                dispatch({ type: "login", payload: userInfo.data });

                setStatus("fetching_cart");

                // Lấy số lượng cart
                const cartRes = await authApis().get(endpoints["cartCount"]);
                cartDispatch({ type: "set", payload: cartRes.data });

                setStatus("redirecting");

                // Redirect dựa trên role
                setTimeout(() => {
                        navigate('/homepage');
                    
                }, 1000);

            } catch (error) {
                setStatus("error");
                setErrorMsg(`Callback processing failed: ${error.message}`);

                setTimeout(() => {
                    navigate('/login?error=callback_failed');
                }, 3000);
            }
        };

        handleOAuth2Callback();
    }, [searchParams, navigate, dispatch, cartDispatch]);

    const getStatusMessage = () => {
        switch(status) {
            case "processing":
                return "Đang xử lý đăng nhập...";
            case "fetching_profile":
                return "Đang tải thông tin người dùng...";
            case "fetching_cart":
                return "Đang tải giỏ hàng...";
            case "redirecting":
                return "Đang chuyển hướng...";
            case "error":
                return errorMsg;
            default:
                return "Đang xử lý...";
        }
    };

    return (
        <div
            style={{
                height: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                backgroundColor: "#f8f9fa"
            }}
        >
            <div style={{ textAlign: "center", maxWidth: "400px", padding: "20px" }}>
                {status !== "error" ? (
                    <div
                        className="spinner-border text-primary"
                        role="status"
                        style={{ width: "3rem", height: "3rem" }}
                    >
                        <span className="visually-hidden">Loading...</span>
                    </div>
                ) : (
                    <div
                        style={{
                            color: "#dc3545",
                            fontSize: "48px",
                            marginBottom: "20px"
                        }}
                    >
                        ❌
                    </div>
                )}
                <p style={{
                    marginTop: "20px",
                    color: status === "error" ? "#dc3545" : "#6c757d",
                    fontSize: "16px"
                }}>
                    {getStatusMessage()}
                </p>
                {status === "error" && (
                    <p style={{ color: "#6c757d", fontSize: "14px", marginTop: "10px" }}>
                        Đang chuyển hướng về trang đăng nhập...
                    </p>
                )}
            </div>
        </div>
    );
};

export default OAuth2Redirect;
