import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import { Button, Form, Modal, Table, Badge } from "react-bootstrap";
import MySpinner from "../layout/MySpinner";

const ShippingAddress = () => {
    const [addresses, setAddresses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editAddress, setEditAddress] = useState(null);

    const [form, setForm] = useState({
        recipientName: "",
        phoneNumber: "",
        addressLine: "",
        ward: "",
        district: "",
        province: "",
        postalCode: "",
        label: ""
    });

    // Load danh sách địa chỉ
    const loadAddresses = async () => {
    try {
        let res = await authApis().get(endpoints["shippingAddress"]);
        setAddresses(res.data.addresses || []);
    } catch (err) {
        console.error("Lỗi tải địa chỉ:", err);
    } finally {
        setLoading(false);
    }
};


    useEffect(() => {
        loadAddresses();
    }, []);

    // Mở modal thêm/sửa
    const openModal = (address = null) => {
        if (address) {
            setEditAddress(address);
            setForm(address);
        } else {
            setEditAddress(null);
            setForm({
                recipientName: "",
                phoneNumber: "",
                addressLine: "",
                ward: "",
                district: "",
                province: "",
                postalCode: "",
                label: ""
            });
        }
        setShowModal(true);
    };

    // Lưu địa chỉ
    const saveAddress = async () => {
        try {
            if (editAddress) {
                await authApis().put(`${endpoints["shippingAddress"]}/${editAddress.id}`, form);
            } else {
                await authApis().post(endpoints["shippingAddress"], form);
            }
            setShowModal(false);
            loadAddresses();
        } catch (err) {
            console.error("Lỗi lưu địa chỉ:", err);
        }
    };

    // Xóa địa chỉ
    const deleteAddress = async (id) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa địa chỉ này?")) return;
        try {
            await authApis().delete(`${endpoints["shippingAddress"]}/${id}`);
            loadAddresses();
        } catch (err) {
            console.error("Lỗi xóa địa chỉ:", err);
        }
    };

    // Đặt làm mặc định
    const setDefault = async (id) => {
        try {
            await authApis().put(`${endpoints["shippingAddress"]}/${id}/set-default`);
            loadAddresses();
        } catch (err) {
            console.error("Lỗi đặt mặc định:", err);
        }
    };

    const updateForm = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    if (loading) return <MySpinner animation="border" />;

    return (
        <div className="container mt-4">
            <h3 style={{ color: "#E0B7B3" }}>ĐỊA CHỈ GIAO HÀNG</h3>

            <Button
                variant="light"
                style={{ backgroundColor: "#E0B7B3", color: "#fff", fontWeight: "bold", marginBottom: "15px" }}
                onClick={() => openModal()}
            >
                + Thêm địa chỉ
            </Button>

            <Table bordered hover responsive>
                <thead style={{ backgroundColor: "#f8f9fa" }}>
                    <tr>
                        <th>Người nhận</th>
                        <th>SĐT</th>
                        <th>Địa chỉ</th>
                        <th>Nhãn</th>
                        <th>Mặc định</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {addresses.length > 0 ? (
                        addresses.map((addr) => (
                            <tr key={addr.id}>
                                <td>{addr.recipientName}</td>
                                <td>{addr.phoneNumber}</td>
                                <td>{addr.addressLine}, {addr.ward}, {addr.district}, {addr.province} {addr.postalCode}</td>
                                <td>{addr.label}</td>
                                <td>
                                    {addr.isDefault ? (
                                        <Badge bg="success">Mặc định</Badge>
                                    ) : (
                                        <Button
                                            variant="outline-primary"
                                            size="sm"
                                            onClick={() => setDefault(addr.id)}
                                        >
                                            Đặt mặc định
                                        </Button>
                                    )}
                                </td>
                                <td>
                                    <Button variant="warning" size="sm" onClick={() => openModal(addr)}>Sửa</Button>{" "}
                                    <Button variant="danger" size="sm" onClick={() => deleteAddress(addr.id)}>Xóa</Button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="6" className="text-center">Chưa có địa chỉ nào</td>
                        </tr>
                    )}
                </tbody>
            </Table>

            {/* Modal thêm/sửa địa chỉ */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{editAddress ? "Cập nhật địa chỉ" : "Thêm địa chỉ mới"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-2">
                            <Form.Label>Người nhận</Form.Label>
                            <Form.Control name="recipientName" value={form.recipientName} onChange={updateForm} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Số điện thoại</Form.Label>
                            <Form.Control name="phoneNumber" value={form.phoneNumber} onChange={updateForm} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Địa chỉ</Form.Label>
                            <Form.Control name="addressLine" value={form.addressLine} onChange={updateForm} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Phường/Xã</Form.Label>
                            <Form.Control name="ward" value={form.ward} onChange={updateForm} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Quận/Huyện</Form.Label>
                            <Form.Control name="district" value={form.district} onChange={updateForm} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Tỉnh/Thành phố</Form.Label>
                            <Form.Control name="province" value={form.province} onChange={updateForm} />
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Mã bưu điện</Form.Label>
                            <Form.Control name="postalCode" value={form.postalCode} onChange={updateForm} />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Nhãn</Form.Label>
                            <Form.Control name="label" value={form.label} onChange={updateForm} />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>Hủy</Button>
                    <Button variant="primary" style={{ backgroundColor: "#E0B7B3", border: "none" }} onClick={saveAddress}>
                        Lưu
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default ShippingAddress;
