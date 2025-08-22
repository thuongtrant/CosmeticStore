
import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import { Row, Col, Table, Button, Image, Form, Alert, Modal } from "react-bootstrap";
import MySpinner from "../layout/MySpinner";
import { useNavigate } from "react-router-dom";

const Checkout = () => {
    const [cart, setCart] = useState(null);
    const [address, setAddress] = useState(null);
    const [note, setNote] = useState("");
    const [paymentMethods, setPaymentMethods] = useState([]);
    const [paymentMethod, setPaymentMethod] = useState("COD");
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState(false);
    const nav = useNavigate();
    const [momoPaymentType, setMomoPaymentType] = useState("captureWallet");

    const loadData = async () => {
        try {
            let cartRes = await authApis().get(endpoints["cart"]);
            setCart(cartRes.data);

            let addressRes = await authApis().get(endpoints["defaultAddress"]);
            if (addressRes.data?.success && addressRes.data.address) {
                setAddress(addressRes.data.address);
            } else {
                setAddress(null);
            }
            let paymentRes = await authApis().get(endpoints["paymentMethods"]);
            if (paymentRes.data?.success)
                setPaymentMethods(paymentRes.data.methods)
            if (paymentRes.data.methods.length > 0)
                setPaymentMethod(paymentRes.data.methods[0].id)
        } catch (err) {
            console.error("Lỗi tải dữ liệu:", err);
        } finally {
            setLoading(false);
        }
    };
    const handleCheckout = async () => {
        if (!address) {
            alert("Vui lòng thêm địa chỉ giao hàng trước khi đặt hàng!");
            return;
        }

        try {
            setProcessing(true);

            const payload = {
                shippingAddressId: address.id,
                note: note,
                paymentMethod: paymentMethod,
                momoRequestType: momoPaymentType,
                items: cart.cartItems.map(item => ({
                    productId: item.productId,
                    quantity: item.quantity
                }))
            };

            console.log("Payload gửi đi:", payload);

            const res = await authApis().post(endpoints["checkout"], payload);

            if (res.data?.success) {
                if (paymentMethod === "MOMO") {
                    // MoMo: kiểm tra có payUrl không
                    if (res.data.payUrl) {
                        localStorage.setItem("momoSessionId", res.data.sessionId);
                        window.location.href = res.data.payUrl;
                        return;
                    } else {
                        alert("❌ Không nhận được link thanh toán MoMo");
                        return;
                    }
                } else {
                    // COD: kiểm tra có order không
                    if (res.data.order && res.data.order.orderNumber) {
                        const orderNumber = res.data.order.orderNumber;
                        alert("✅ Đặt hàng thành công! Mã đơn: " + orderNumber);
                        nav("/orderDetail/" + orderNumber);
                    } else {
                        alert("❌ Đặt hàng thành công nhưng không nhận được mã đơn");
                    }
                }
            } else {
                // Xử lý lỗi
                if (res.data?.suggestCOD) {
                    const confirmCOD = window.confirm(
                        res.data.message + " Bạn có muốn chuyển sang thanh toán COD không?"
                    );
                    if (confirmCOD) {
                        setPaymentMethod("COD");
                        return;
                    }
                }

                alert("❌ " + (res.data?.message || "Đặt hàng thất bại!"));
            }
        } catch (err) {
            console.error("Lỗi chi tiết:", err.response?.data || err.message);

            if (err.response?.data?.suggestCOD) {
                const confirmCOD = window.confirm(
                    err.response.data.message + " Bạn có muốn chuyển sang thanh toán COD không?"
                );
                if (confirmCOD) {
                    setPaymentMethod("COD");
                    return;
                }
            }

            alert("❌ Lỗi khi đặt hàng: " + (err.response?.data?.message || err.message));
        } finally {
            setProcessing(false);
        }
    };


    const processMoMoPayment = async (orderNumber) => {
        try {
            const paymentRes = await authApis().post(
                endpoints["processPayment"](orderNumber) +
                `?paymentMethod=MOMO&requestType=${momoPaymentType}`
            );

            if (paymentRes.data?.success && paymentRes.data.payUrl) {
                window.location.href = paymentRes.data.payUrl;
            } else {
                alert("❌ Không thể tạo liên kết thanh toán MoMo: " + paymentRes.data?.message);
            }
        } catch (err) {
            console.error("Lỗi tạo thanh toán MoMo:", err);
            alert("❌ Lỗi khi tạo thanh toán MoMo!");
        }
    };

    const processOtherPayment = async (orderNumber) => {
        try {
            await authApis().post(
                endpoints["processPayment"](orderNumber) + `?paymentMethod=${paymentMethod}`
            );
        } catch (err) {
            console.error("Lỗi xử lý thanh toán:", err);
            throw err;
        }
    };



    useEffect(() => {
        loadData();
    }, []);

    if (loading) return <MySpinner animation="border" />;

    return (
        <div className="container mt-4">
            <h3 className="text-center mb-4" style={{ color: "#E0B7B3" }}>THANH TOÁN</h3>
            <Row>
                {/* Danh sách sản phẩm */}
                <Col md={8}>
                    <Table hover responsive className="align-middle">
                        <thead>
                            <tr>
                                <th>SẢN PHẨM</th>
                                <th>GIÁ</th>
                                <th>SỐ LƯỢNG</th>
                                <th>TỔNG CỘNG</th>
                            </tr>
                        </thead>
                        <tbody>
                            {cart?.cartItems?.map((item) => (
                                <tr key={item.id}>
                                    <td>
                                        <div className="d-flex align-items-center">
                                            <Image
                                                src={item.productImageUrl || "/images/no-image.png"}
                                                alt={item.productName}
                                                style={{
                                                    width: "70px",
                                                    height: "70px",
                                                    objectFit: "cover",
                                                    marginRight: "10px",
                                                }}
                                                rounded
                                            />
                                            <span>{item.productName}</span>
                                        </div>
                                    </td>
                                    <td>{item.productPrice.toLocaleString()} ₫</td>
                                    <td>{item.quantity}</td>
                                    <td>{item.subtotal.toLocaleString()} ₫</td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </Col>

                {/* Địa chỉ + Tổng tiền */}
                <Col md={4}>
                    <div style={{
                        border: "1px solid #eee",
                        padding: "20px",
                        borderRadius: "8px",
                        backgroundColor: "#fafafa"
                    }}>
                        <div className="d-flex justify-content-between align-items-center mb-2">
                            <h5 style={{ color: "#E0B7B3", margin: 0 }}>ĐỊA CHỈ GIAO HÀNG</h5>
                            <Button
                                variant="link"
                                style={{ color: "#E0B7B3", padding: 0, textDecoration: "none" }}
                                onClick={() => nav("/shippingAddress")}
                            >
                                {address ? "Thay đổi" : "Thêm địa chỉ giao hàng"}
                            </Button>
                        </div>

                        {address ? (
                            <div>
                                <strong>{address.recipientName}</strong>
                                <p>{address.phoneNumber}</p>
                                <p>{address.addressLine}, {address.ward}, {address.district}, {address.province}</p>
                            </div>
                        ) : (
                            <p>Chưa có địa chỉ giao hàng</p>
                        )}

                        {/* Ghi chú */}
                        <Form.Group className="mt-3">
                            <Form.Label>Ghi chú đơn hàng</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={2}
                                value={note}
                                onChange={(e) => setNote(e.target.value)}
                            />
                        </Form.Group>

                        <Form.Group className="mt-3">
                            <Form.Label>Phương thức thanh toán</Form.Label>
                            <Form.Select
                                value={paymentMethod}
                                onChange={(e) => setPaymentMethod(e.target.value)}
                            >
                                {paymentMethods.map((pm) => (
                                    <option key={pm.id} value={pm.id}>
                                        {pm.name} - {pm.description}
                                    </option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        {paymentMethod === "MOMO" && (
                            <Form.Group className="mt-3">
                                <Form.Label>Loại thanh toán MoMo</Form.Label>
                                <Form.Select
                                    value={momoPaymentType}
                                    onChange={(e) => setMomoPaymentType(e.target.value)}
                                >
                                    <option value="captureWallet">Ví MoMo (QR Code)</option>
                                    <option value="payWithATM">Thẻ ATM/Visa/Master</option>
                                </Form.Select>
                            </Form.Group>
                        )}

                        <hr style={{ borderTop: "2px solid #eabbb7" }} />
                        <div className="d-flex justify-content-between">
                            <span style={{ fontWeight: "bold" }}>Tổng tiền sản phẩm</span>
                            <span style={{ fontWeight: "bold" }}>
                                {cart?.totalAmount?.toLocaleString()} ₫
                            </span>
                        </div>
                        <div className="d-flex justify-content-between mt-2">
                            <span>Miễn phí giao hàng</span>
                        </div>
                        <div className="d-flex justify-content-between mt-2" style={{ fontWeight: "bold", color: "#E0B7B3" }}>
                            <span>Tổng thanh toán</span>
                            <span>{((cart?.totalAmount || 0)).toLocaleString()} ₫</span>
                        </div>

                        <Button
                            variant="light"
                            style={{
                                backgroundColor: "#E0B7B3",
                                border: "none",
                                color: "#fff",
                                fontWeight: "bold",
                                width: "100%",
                                marginTop: "15px"
                            }}
                            onClick={handleCheckout}
                            disabled={processing}
                        >
                            {processing ? (
                                <>
                                    <MySpinner size="sm" className="me-2" />
                                    {paymentMethod === "MOMO" ? "Đang tạo liên kết thanh toán..." : "Đang xử lý..."}
                                </>
                            ) : (
                                paymentMethod === "MOMO" ? "THANH TOÁN QUA MOMO" : "XÁC NHẬN ĐẶT HÀNG"
                            )}
                        </Button>
                    </div>
                </Col>
            </Row>
        </div>
    );
};

export default Checkout;