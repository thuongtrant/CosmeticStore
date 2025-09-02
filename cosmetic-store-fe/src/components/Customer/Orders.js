import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import { Table, Button, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import "../../styles/pagination.css";

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [initialLoading, setInitialLoading] = useState(true);
    const [pageLoading, setPageLoading] = useState(false);
    const [pagination, setPagination] = useState({
        currentPage: 0,
        totalPages: 0,
        totalElements: 0,
        hasNext: false,
        hasPrevious: false
    });
    const [pageSize] = useState(10);
    const nav = useNavigate();

    const loadOrders = async (page = 0, isInitial = false) => {
        if (isInitial) {
            setInitialLoading(true);
        } else {
            setPageLoading(true);
        }
        
        try {
            let res = await authApis().get(`${endpoints["orders"]}?page=${page}&size=${pageSize}`);
            if (res.data) {
                setOrders(res.data.products || []);
                setPagination({
                    currentPage: res.data.currentPage || 0,
                    totalPages: res.data.totalPages || 0,
                    totalElements: res.data.totalElements || 0,
                    hasNext: res.data.hasNext || false,
                    hasPrevious: res.data.hasPrevious || false
                });
            }
        } catch (err) {
            console.error("Lỗi khi tải đơn hàng:", err);
            setOrders([]);
        } finally {
            if (isInitial) {
                setInitialLoading(false);
            } else {
                setPageLoading(false);
            }
        }
    };

    useEffect(() => {
        loadOrders(0, true);
    }, []);

    const handlePageChange = (newPage) => {
        loadOrders(newPage, false);
    };

    const getStatusBadge = (status) => {
        const statusMap = {
            'PENDING': { variant: 'warning', text: 'Chờ xử lý' },
            'CONFIRMED': { variant: 'info', text: 'Đã xác nhận' },
            'PROCESSING': { variant: 'primary', text: 'Đang xử lý' },
            'SHIPPED': { variant: 'secondary', text: 'Đã giao' },
            'DELIVERED': { variant: 'success', text: 'Hoàn thành' },
            'CANCELLED': { variant: 'danger', text: 'Đã hủy' }
        };
        
        const statusInfo = statusMap[status] || { variant: 'secondary', text: status };
        return <Badge bg={statusInfo.variant}>{statusInfo.text}</Badge>;
    };

    const renderPagination = () => (
        <div className="pagination-container">
            <button
                className="pagination-btn"
                disabled={pagination.currentPage === 0 || pageLoading}
                onClick={() => handlePageChange(0)}
            >
                «
            </button>
        

            {[...Array(pagination.totalPages)].map((_, index) => (
                <button
                    key={index}
                    className={`pagination-btn ${index === pagination.currentPage ? "active" : ""}`}
                    disabled={pageLoading}
                    onClick={() => handlePageChange(index)}
                >
                    {index + 1}
                </button>
            ))}

            
            <button
                className="pagination-btn"
                disabled={pagination.currentPage === pagination.totalPages - 1 || pageLoading}
                onClick={() => handlePageChange(pagination.totalPages - 1)}
            >
                »
            </button>
        </div>
    );

    if (initialLoading) return <MySpinner animation="border" />;

    return (
        <div className="container mt-4">
            <h3 className="text-center mb-4" style={{ color: "#E0B7B3" }}>
                ĐƠN HÀNG CỦA BẠN
            </h3>

            {orders.length === 0 ? (
                <div className="text-center">
                    <p>Bạn chưa có đơn hàng nào.</p>
                    <Button 
                        variant="outline-primary"
                        onClick={() => nav('/products')}
                    >
                        Mua sắm ngay
                    </Button>
                </div>
            ) : (
                <>
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
                                    <td className="fw-bold">{o.orderNumber}</td>
                                    <td>{new Date(o.createdAt).toLocaleString('vi-VN')}</td>
                                    <td className="fw-bold text-success">
                                        {o.totalAmount.toLocaleString()} ₫
                                    </td>
                                    <td>{getStatusBadge(o.status)}</td>
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

                    {pagination.totalPages > 1 && (
                        <div className="position-relative">
                            {pageLoading && (
                                <div className="position-absolute w-100 h-100 d-flex justify-content-center align-items-center" style={{
                                    backgroundColor: 'rgba(255,255,255,0.8)',
                                    zIndex: 10
                                }}>
                                    <div className="spinner-border spinner-border-sm text-primary" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            )}
                            <div className={pageLoading ? 'opacity-50' : ''}>
                                {renderPagination()}
                            </div>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default Orders;