import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import { Button, Form, Modal, Table, Badge, Spinner } from "react-bootstrap";
import MySpinner from "../layout/MySpinner";

const ShippingAddress = () => {
    const [addresses, setAddresses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editAddress, setEditAddress] = useState(null);
    const [errors, setErrors] = useState({});

    const [form, setForm] = useState({
        recipientName: "",
        phoneNumber: "",
        addressLine: "",
        ward: "",
        district: "",
        province: "",
        wardCode: "",
        districtCode: "",
        provinceCode: "",
        postalCode: "",
        label: "",
        isDefault: false
    });

    // State cho dropdown địa lý
    const [provinces, setProvinces] = useState([]);
    const [districts, setDistricts] = useState([]);
    const [wards, setWards] = useState([]);
    const [loadingGeo, setLoadingGeo] = useState(false);

    // Load danh sách địa chỉ
    const loadAddresses = async () => {
        try {
            let res = await authApis().get(endpoints["shippingAddress"]);
            setAddresses(res.data.addresses || []);
        } catch (err) {
            alert("Lỗi tải danh sách địa chỉ: " + (err.response?.data?.message || err.message));
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadAddresses();
    }, []);

    // Load tỉnh/thành phố từ API provinces.open-api.vn
    const loadProvinces = async () => {
        setLoadingGeo(true);
        try {
            const res = await fetch("https://provinces.open-api.vn/api/p/");
            const data = await res.json();
            setProvinces(data || []);
        } catch (err) {
            alert("Lỗi tải danh sách tỉnh/thành phố");
        } finally {
            setLoadingGeo(false);
        }
    };

    // Load quận/huyện khi chọn tỉnh
    const loadDistricts = async (provinceCode) => {
        setLoadingGeo(true);
        try {
            const res = await fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`);
            const data = await res.json();
            setDistricts(data.districts || []);
        } catch (err) {
            alert("Lỗi tải danh sách quận/huyện");
        } finally {
            setLoadingGeo(false);
        }
    };

    // Load phường/xã khi chọn quận/huyện
    const loadWards = async (districtCode) => {
        setLoadingGeo(true);
        try {
            const res = await fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`);
            const data = await res.json();
            setWards(data.wards || []);
        } catch (err) {
            alert("Lỗi tải danh sách phường/xã");
        } finally {
            setLoadingGeo(false);
        }
    };

    // Mở modal thêm/sửa
    const openModal = (address = null) => {
        if (address) {
            setEditAddress(address);
            setForm({
                ...address,
                provinceCode: address.provinceCode || "",
                districtCode: address.districtCode || "",
                wardCode: address.wardCode || ""
            });
            // Load quận/xã tương ứng nếu có provinceCode/districtCode
            if (address.provinceCode) loadDistricts(address.provinceCode);
            if (address.districtCode) loadWards(address.districtCode);
        } else {
            setEditAddress(null);
            setForm({
                recipientName: "",
                phoneNumber: "",
                addressLine: "",
                ward: "",
                district: "",
                province: "",
                wardCode: "",
                districtCode: "",
                provinceCode: "",
                postalCode: "",
                label: "",
                isDefault: false
            });
            setDistricts([]);
            setWards([]);
        }
        setErrors({});
        setShowModal(true);
        loadProvinces();
    };

    // Khi chọn tỉnh/thành phố
    const handleProvinceChange = (e) => {
        const provinceCode = e.target.value;
        const provinceObj = provinces.find(p => p.code.toString() === provinceCode);
        setForm({
            ...form,
            province: provinceObj ? provinceObj.name : "",
            provinceCode,
            district: "",
            districtCode: "",
            ward: "",
            wardCode: ""
        });
        setDistricts([]);
        setWards([]);
        if (provinceCode) loadDistricts(provinceCode);
    };

    // Khi chọn quận/huyện
    const handleDistrictChange = (e) => {
        const districtCode = e.target.value;
        const districtObj = districts.find(d => d.code.toString() === districtCode);
        setForm({
            ...form,
            district: districtObj ? districtObj.name : "",
            districtCode,
            ward: "",
            wardCode: ""
        });
        setWards([]);
        if (districtCode) loadWards(districtCode);
    };

    // Khi chọn phường/xã
    const handleWardChange = (e) => {
        const wardCode = e.target.value;
        const wardObj = wards.find(w => w.code.toString() === wardCode);
        setForm({
            ...form,
            ward: wardObj ? wardObj.name : "",
            wardCode
        });
    };

    // Validation cơ bản phía client
    const validateForm = () => {
        const newErrors = {};
        if (!form.recipientName.trim()) newErrors.recipientName = "Tên người nhận không được để trống";
        if (!form.phoneNumber.trim()) newErrors.phoneNumber = "Số điện thoại không được để trống";
        else if (!/^[0-9]{10}$/.test(form.phoneNumber)) newErrors.phoneNumber = "Số điện thoại phải gồm 10 chữ số";
        if (!form.addressLine.trim()) newErrors.addressLine = "Địa chỉ chi tiết không được để trống";
        if (!form.provinceCode) newErrors.province = "Vui lòng chọn tỉnh/thành phố";
        if (!form.districtCode) newErrors.district = "Vui lòng chọn quận/huyện";
        if (!form.wardCode) newErrors.ward = "Vui lòng chọn phường/xã";
        return newErrors;
    };

    // Lưu địa chỉ
    const saveAddress = async () => {
        // Kiểm tra validation phía client
        const clientErrors = validateForm();
        if (Object.keys(clientErrors).length > 0) {
            setErrors(clientErrors);
            return;
        }

        try {
            if (editAddress) {
                await authApis().put(`${endpoints["shippingAddress"]}/${editAddress.id}`, form);
                alert("Cập nhật địa chỉ thành công");
            } else {
                await authApis().post(endpoints["shippingAddress"], form);
                alert("Thêm địa chỉ thành công");
            }
            setShowModal(false);
            loadAddresses();
            setErrors({});
        } catch (err) {
            if (err.response?.data?.errors) {
                setErrors(err.response.data.errors); // Lưu lỗi validation từ backend
            } else {
                alert("Lỗi lưu địa chỉ: " + (err.response?.data?.message || err.message));
            }
        }
    };

    // Xóa địa chỉ
    const deleteAddress = async (id) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa địa chỉ này?")) return;
        try {
            await authApis().delete(`${endpoints["shippingAddress"]}/${id}`);
            alert("Xóa địa chỉ thành công");
            loadAddresses();
        } catch (err) {
            alert("Lỗi xóa địa chỉ: " + (err.response?.data?.message || err.message));
        }
    };

    // Đặt làm mặc định
    const setDefault = async (id) => {
        try {
            await authApis().put(`${endpoints["shippingAddress"]}/${id}/set-default`);
            alert("Đặt địa chỉ mặc định thành công");
            loadAddresses();
        } catch (err) {
            alert("Lỗi đặt mặc định: " + (err.response?.data?.message || err.message));
        }
    };

    // Cập nhật form
    const updateForm = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
        // Xóa lỗi của trường khi người dùng bắt đầu nhập
        if (errors[e.target.name]) {
            setErrors({ ...errors, [e.target.name]: null });
        }
    };

    // Toggle checkbox mặc định
    const toggleDefault = () => {
        setForm({ ...form, isDefault: !form.isDefault });
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
                            <Form.Control
                                name="recipientName"
                                value={form.recipientName}
                                onChange={updateForm}
                                isInvalid={!!errors.recipientName}
                                placeholder="Nhập tên người nhận"
                            />
                            <Form.Control.Feedback type="invalid">{errors.recipientName}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Số điện thoại</Form.Label>
                            <Form.Control
                                name="phoneNumber"
                                value={form.phoneNumber}
                                onChange={updateForm}
                                isInvalid={!!errors.phoneNumber}
                                placeholder="Nhập số điện thoại"
                            />
                            <Form.Control.Feedback type="invalid">{errors.phoneNumber}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Địa chỉ chi tiết</Form.Label>
                            <Form.Control
                                name="addressLine"
                                value={form.addressLine}
                                onChange={updateForm}
                                isInvalid={!!errors.addressLine}
                                placeholder="Số nhà, tên đường..."
                            />
                            <Form.Control.Feedback type="invalid">{errors.addressLine}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Tỉnh/Thành phố</Form.Label>
                            <Form.Select
                                name="provinceCode"
                                value={form.provinceCode || ""}
                                onChange={handleProvinceChange}
                                isInvalid={!!errors.province}
                                disabled={loadingGeo}
                            >
                                <option value="">Chọn tỉnh/thành phố</option>
                                {provinces.map(p => (
                                    <option key={p.code} value={p.code}>{p.name}</option>
                                ))}
                            </Form.Select>
                            <Form.Control.Feedback type="invalid">{errors.province}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Quận/Huyện</Form.Label>
                            <Form.Select
                                name="districtCode"
                                value={form.districtCode || ""}
                                onChange={handleDistrictChange}
                                isInvalid={!!errors.district}
                                disabled={!form.provinceCode || loadingGeo}
                            >
                                <option value="">Chọn quận/huyện</option>
                                {districts.map(d => (
                                    <option key={d.code} value={d.code}>{d.name}</option>
                                ))}
                            </Form.Select>
                            <Form.Control.Feedback type="invalid">{errors.district}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Phường/Xã</Form.Label>
                            <Form.Select
                                name="wardCode"
                                value={form.wardCode || ""}
                                onChange={handleWardChange}
                                isInvalid={!!errors.ward}
                                disabled={!form.districtCode || loadingGeo}
                            >
                                <option value="">Chọn phường/xã</option>
                                {wards.map(w => (
                                    <option key={w.code} value={w.code}>{w.name}</option>
                                ))}
                            </Form.Select>
                            <Form.Control.Feedback type="invalid">{errors.ward}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Label>Nhãn</Form.Label>
                            <Form.Control
                                name="label"
                                value={form.label}
                                onChange={updateForm}
                                isInvalid={!!errors.label}
                                placeholder="VD: Nhà riêng, Văn phòng..."
                            />
                            <Form.Control.Feedback type="invalid">{errors.label}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-2">
                            <Form.Check
                                type="checkbox"
                                label="Đặt làm mặc định"
                                checked={form.isDefault}
                                onChange={toggleDefault}
                            />
                        </Form.Group>
                    </Form>
                    {loadingGeo && <div className="text-center"><Spinner animation="border" size="sm" /></div>}
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