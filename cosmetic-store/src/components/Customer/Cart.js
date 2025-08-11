import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import { Row, Col, Table, Button, Image, Form } from "react-bootstrap";
import MySpinner from "../layout/MySpinner";
import { Link } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import '../../styles/cardProduct.css';

const Cart = () => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);

    const loadCart = async () => {
        try {
            let res = await authApis().get(endpoints["cart"]);
            setCart(res.data);
        } catch (err) {
            console.error("Lỗi tải giỏ hàng:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCart();
    }, []);

    if (loading) return <MySpinner animation="border" />;

    if (!cart || cart.cartItems.length === 0) {
        return (
            <div className="text-center mt-5">
                <h4>Giỏ hàng của bạn đang trống</h4>
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <h3 className="text-center mb-4" style={{ color: "#E0B7B3" }}>GIỎ HÀNG</h3>
            <Row>
                {/* Danh sách sản phẩm - 2/3 màn hình */}
                <Col md={8}>
                    <Table hover responsive className="align-middle custom-cart-table">
                        <thead>
                            <tr>
                                <th>SẢN PHẨM</th>
                                <th>GIÁ</th>
                                <th>SỐ LƯỢNG</th>
                                <th>TỔNG CỘNG</th>
                            </tr>
                        </thead>
                        <tbody>
                            {cart.cartItems.map((item) => (
                                <tr key={item.id}>
                                    <td>
                                        <div className="d-flex align-items-center">
                                            <Image
                                                src={item.productImageUrl || "/images/no-image.png"}
                                                alt={item.productName}
                                                style={{
                                                    width: "80px",
                                                    height: "80px",
                                                    objectFit: "cover",
                                                    marginRight: "10px",
                                                }}
                                                rounded
                                            />
                                            <span>{item.productName}</span>
                                        </div>
                                    </td>
                                    <td>{item.productPrice.toLocaleString()} ₫</td>
                                    <td>
                                        <div className="d-flex align-items-center justify-content-center">
                                            <Button size="sm" variant="outline-secondary">-</Button>
                                            <span className="mx-2">{item.quantity}</span>
                                            <Button size="sm" variant="outline-secondary">+</Button>
                                        </div>
                                    </td>
                                    <td>{item.subtotal.toLocaleString()} ₫</td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>

                    <Link to="/home" className="btn btn-comeback">
                        ← TRỞ VỀ
                    </Link>
                </Col>

                {/* Tổng giá trị giỏ hàng - 1/3 màn hình */}
                <Col md={4}>
                    <div
                    // style={{
                    //     border: "1px solid #eee",
                    //     padding: "20px",
                    //     borderRadius: "8px",
                    //     backgroundColor: "#fafafa",
                    // }}
                    >
                        <h5 className="mb-3 text-center" style={{ color: "#E0B7B3" }}>TỔNG GIỎ HÀNG</h5>
                        <hr style={{ borderTop: "2px solid #eabbb7" }} />
                        <div className="d-flex justify-content-between mb-3">
                            <span style={{ fontWeight: "bold" }}>Tổng</span>
                            <span style={{ fontWeight: "bold" }}>
                                {cart.totalAmount.toLocaleString()} ₫
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
                            }}
                        >
                            THANH TOÁN
                        </Button>
                    </div>
                </Col>
            </Row>
        </div>
    );
};

export default Cart;
