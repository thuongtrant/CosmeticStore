import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import { Table, Button, Badge, Image } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const nav = useNavigate();

    useEffect(() => {
        const loadOrders = async () => {
            try {
                let res = await authApis().get(endpoints["orders"]);
                if (res.data?.success) {
                    setOrders(res.data.orders || []);
                }
            } catch (err) {
                console.error("Lỗi khi tải đơn hàng:", err);
            } finally {
                setLoading(false);
            }
        };
        loadOrders();
    }, []);

    if (loading) return <MySpinner animation="border" />;

    return (
        <div className="container mt-4">
            <h3 className="text-center mb-4" style={{ color: "#E0B7B3" }}>
                ĐƠN HÀNG CỦA BẠN
            </h3>

            {orders.length === 0 ? (
                <p className="text-center">Bạn chưa có đơn hàng nào.</p>
            ) : (
                <Table bordered hover responsive>
                    <thead>
                        <tr>
                            <th>Mã đơn hàng</th>
                            <th>Ngày đặt</th>
                            <th>Tổng tiền</th>
                            <th>Trạng thái</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((o) => (
                            <tr key={o.id}>
                                <td>{o.orderNumber}</td>
                                <td>{new Date(o.createdAt).toLocaleString('vi-VN')}</td>
                                <td>{o.totalAmount.toLocaleString()} ₫</td>
                                <td>{o.status}</td>
                                <td>
                                    <Button
                                        variant="light"
                                        style={{
                                            backgroundColor: "#E0B7B3",
                                            color: "#fff",
                                            border: "none",
                                            fontWeight: "bold",
                                        }}
                                        onClick={() => nav(`/orderDetail/${o.orderNumber}`)}
                                    >
                                        Xem chi tiết
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}
        </div>
    );
};

export default Orders;
