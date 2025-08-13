import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import { Row, Col, Table, Button, Image, Form } from "react-bootstrap";
import MySpinner from "../layout/MySpinner";
import { useNavigate } from "react-router-dom";
import '../../styles/header.css'

const Checkout = () => {
    const [cart, setCart] = useState(null);
    const [address, setAddress] = useState(null);
    const [note, setNote] = useState("");
    const [paymentMethod, setPaymentMethod] = useState("COD");
    const [loading, setLoading] = useState(true);
    const nav = useNavigate();

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
            let payload = {
                shippingAddressId: address.id,
                note: note,
                paymentMethod: paymentMethod,
                items: cart.cartItems.map(item => ({
                    productId: item.productId,
                    quantity: item.quantity
                }))
            };

            let res = await authApis().post(endpoints["checkout"], payload);
            if (res.data?.success) {
                alert("✅ Đặt hàng thành công! Mã đơn: " + res.data.order.orderNumber);
                nav("/orders/" + res.data.order.id);
            } else {
                alert("❌ Đặt hàng thất bại!");
            }
        } catch (err) {
            console.error("Lỗi thanh toán:", err);
            alert("❌ Lỗi khi đặt hàng!");
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    if (loading) return <MySpinner animation="border" />;

    return (
        <div className="container marginTop">
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

                        {/* Chọn phương thức thanh toán */}
                        <Form.Group className="mt-3">
                            <Form.Label>Phương thức thanh toán</Form.Label>
                            <Form.Select
                                value={paymentMethod}
                                onChange={(e) => setPaymentMethod(e.target.value)}
                            >
                                <option value="COD">Thanh toán khi nhận hàng (COD)</option>
                                <option value="BANK">Chuyển khoản ngân hàng</option>
                                <option value="VNPAY">Thanh toán qua VNPAY</option>
                            </Form.Select>
                        </Form.Group>

                        <hr style={{ borderTop: "2px solid #eabbb7" }} />
                        <div className="d-flex justify-content-between">
                            <span style={{ fontWeight: "bold" }}>Tổng tiền</span>
                            <span style={{ fontWeight: "bold" }}>
                                {cart?.totalAmount?.toLocaleString()} ₫
                            </span>
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
                        >
                            XÁC NHẬN ĐẶT HÀNG
                        </Button>
                    </div>
                </Col>
            </Row>
        </div>
    );
};

export default Checkout;
