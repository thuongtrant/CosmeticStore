import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import { Row, Col, Card, ListGroup, Image, Badge } from "react-bootstrap";
import { FaFile, FaCheckCircle, FaShippingFast, FaTruck } from "react-icons/fa";
import "../../styles/OrderDetail.css";

const OrderDetail = () => {
    const { orderNumber } = useParams();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadOrder = async () => {
            try {
                let res = await authApis().get(endpoints["orderDetail"](orderNumber));
                if (res.data?.success) {
                    console.log("Order data:", res.data.order); // Debug log
                    console.log("Order status:", res.data.order?.status); // Debug log
                    setOrder(res.data.order || null);
                } else {
                    alert("Không tìm thấy đơn hàng!");
                }
            } catch (err) {
                console.error("Lỗi khi tải đơn hàng:", err);
            } finally {
                setLoading(false);
            }
        };
        loadOrder();
    }, [orderNumber]);

    if (loading) return <MySpinner animation="border" />;
    if (!order) return <p className="text-center mt-4">Không tìm thấy đơn hàng</p>;

    // Ánh xạ trạng thái từ backend (tiếng Việt) sang các bước hiển thị (tiếng Anh)
    const statusSteps = {
        "Chờ xử lý": "Pending Order",
        "Đã xác nhận": "Order Confirmed",
        "Đã giao hàng": "Order Shipped",
        "Đã nhận hàng": "Order Delivered",
    };

    // Xác định trạng thái hiện tại
    const currentStatus = order.status || "Chờ xử lý";
    const statusOrder = ["Chờ xử lý", "Đã xác nhận", "Đã giao hàng", "Đã nhận hàng"];

    // Mapping icons cho status tiếng Việt
    const getIcon = (status) => {
        const iconMap = {
            "Chờ xử lý": FaFile,
            "Đã xác nhận": FaCheckCircle,
            "Đã giao hàng": FaShippingFast,
            "Đã nhận hàng": FaTruck,
        };
        return iconMap[status] || FaFile;
    };

    return (
        <div className="container mt-4">
            <h3 className="text-center mb-4" style={{ color: "#E0B7B3" }}>
                Chi tiết đơn hàng <Badge bg="secondary">{order.orderNumber}</Badge>
            </h3>

            {/* Thanh tiến trình trạng thái với icon */}
            <div className="order-progress mb-4">
                {statusOrder.map((status, index) => {
                    const currentIndex = statusOrder.indexOf(currentStatus);
                    const isCurrent = status === currentStatus;
                    const isCompleted = index <= currentIndex; // Include current status as completed
                    const Icon = getIcon(status);

                    return (
                        <div key={status} className="progress-step">
                            <div
                                className={`step-icon ${isCurrent ? "current" : isCompleted ? "completed" : ""}`}
                            >
                                <Icon size={20} />
                            </div>
                            <div
                                className={`step-label ${isCurrent ? "current" : isCompleted ? "completed" : ""}`}
                            >
                                {statusSteps[status]}
                            </div>
                            {index < statusOrder.length - 1 && (
                                <div
                                    className={`progress-line ${index < currentIndex ? "completed" : ""}`}
                                ></div>
                            )}
                        </div>
                    );
                })}
            </div>

            {/* Thông tin đơn hàng */}
            <Row className="mb-4">
                <Col md={6}>
                    <Card className="p-3 shadow-sm">
                        <h5 className="mb-3" style={{ color: "#E0B7B3" }}>Thông tin giao hàng</h5>
                        <p><strong>Địa chỉ:</strong> {order.shippingAddress}</p>
                        <p><strong>SĐT:</strong> {order.phoneNumber}</p>
                        <p><strong>Ghi chú:</strong> {order.note || "Không có"}</p>
                    </Card>
                </Col>
                <Col md={6}>
                    <Card className="p-3 shadow-sm">
                        <h5 className="mb-3" style={{ color: "#E0B7B3" }}>Thông tin thanh toán</h5>
                        <div className="d-flex align-items-center mb-2">
                            <strong>Phương thức:</strong>
                            {order.payment?.paymentMethod === "MOMO" && (
                                <Image
                                    src="/images/momo-logo.png"
                                    alt="MoMo"
                                    width="25"
                                    height="25"
                                    className="ms-2 me-1"
                                    onError={(e) => {
                                        e.target.style.display = 'none';
                                    }}
                                />
                            )}
                            <span className="ms-1">{order.payment?.paymentMethod}</span>
                        </div>
                        <p><strong>Số tiền:</strong> {order.payment?.amount.toLocaleString()} ₫</p>
                        <p><strong>Phí giao hàng:</strong>  0₫</p>
                    </Card>
                </Col>
            </Row>

            {/* Danh sách sản phẩm */}
            <Card className="p-3 shadow-sm">
                <h5 className="mb-3" style={{ color: "#E0B7B3" }}>Sản phẩm</h5>
                <ListGroup variant="flush">
                    {order.items?.map((item, idx) => (
                        <ListGroup.Item key={idx} className="d-flex align-items-center">
                            <Image
                                src={item.mainImage || "/images/no-image.png"}
                                alt={item.productName}
                                style={{
                                    width: "80px",
                                    height: "80px",
                                    objectFit: "cover",
                                    borderRadius: "8px",
                                    marginRight: "15px"
                                }}
                            />
                            <div className="flex-grow-1">
                                <h6 style={{ fontWeight: "bold" }}>{item.productName}</h6>
                                <p className="mb-1">Số lượng: {item.quantity}</p>
                                <p className="mb-0 text-muted">
                                    {item.unitPrice.toLocaleString()} ₫ x {item.quantity} ={" "}
                                    <strong style={{ color: "#d9534f" }}>{item.totalPrice.toLocaleString()} ₫</strong>
                                </p>
                            </div>
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            </Card>

            {/* Tổng cộng */}
            <div className="text-end mt-4">
                <h4 style={{ fontWeight: "bold", color: "#E0B7B3" }}>
                    Tổng cộng: {((order.totalAmount || 0) + (order.shippingFee || 0)).toLocaleString()} ₫
                </h4>
            </div>
        </div>
    );
};

export default OrderDetail;