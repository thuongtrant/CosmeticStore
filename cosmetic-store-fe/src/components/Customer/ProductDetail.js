import React, { useContext,useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import { Card, Button, Row, Col } from "react-bootstrap";
import '../../styles/ProductDetail.css';
import '../../styles/cardProduct.css';
import { CartDispatchContext } from "../../configs/CartContext";

const ProductDetail = () => {
    const cartDispatch = useContext(CartDispatchContext);

    const { productId } = useParams();
    const [product, setProduct] = useState({});
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState("ingredients");
    const [mainImage, setMainImage] = useState("/images/no-image.png");
    const thumbnails = product?.mainImage
        ? [product.mainImage, ...(product.images?.filter(img => img !== product.mainImage) || [])]
        : product.images || [];
    useEffect(() => {
        const loadDetail = async () => {
            try {
                let res = await authApis().get(endpoints['productDetail'](productId));
                const data = res.data || {};
                setProduct(data);

                // Set ảnh chính mặc định
                if (data.mainImage) {
                    setMainImage(data.mainImage);
                }
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        loadDetail();
    }, [productId]);

    if (loading) return <MySpinner />;

    if (!product) return <p>Không tìm thấy sản phẩm</p>;

    const handleTabClick = (tab) => {
        setActiveTab(tab);
    };

    const handleThumbnailClick = (img) => {
        setMainImage(img);
    };

    const renderTabContent = () => {
        switch (activeTab) {
            case "ingredients":
                return product.ingredients && product.ingredients.length > 0
                    ? product.ingredients.join(", ")
                    : "Chưa có thông tin về thành phần";
            case "benefits":
                return product.benefits || "Chưa có thông tin về công dụng";
            case "howToUse":
                return product.howToUse || "Chưa có thông tin về hướng dẫn sử dụng";
            default:
                return null;
        }
    };

    const addToCart = async (productId) => {
        try {
            await authApis().post(endpoints["addToCart"], {
                productId,
                quantity: 1
            });
            let res = await authApis().get(endpoints["cartCount"]);
            cartDispatch({ type: "set", payload: res.data });
        } catch (error) {
            if (error.needAuth) {
                alert(error.message);
                window.location.href = '/login';
            } else {
                console.error(error);
            }
        }
    };
    return (
        <div className="container mt-4">
            <Row>
                <Col md={6} className="mb-4">
                    <Card className="shadow-sm border-0">
                        <Card.Img
                            variant="top"
                            src={mainImage}
                            alt={product.name}
                            style={{
                                height: "350px",
                                objectFit: "contain",
                                borderRadius: "8px",
                                transition: "transform 0.3s ease",
                            }}
                            className="main-image"
                        />
                    </Card>

                    <div className="d-grid gap-2 mt-3" style={{ gridTemplateColumns: "repeat(auto-fit, minmax(60px, 1fr))", maxWidth: "350px" }}>
                        {thumbnails.length > 0 ? (
                            thumbnails.map((img, idx) => (
                                <img
                                    key={idx}
                                    src={img}
                                    alt={`Thumbnail ${idx}`}
                                    style={{
                                        width: "60px",
                                        height: "60px",
                                        objectFit: "cover",
                                        border: mainImage === img ? "2px solid #eabbb7" : "1px solid #ddd",
                                        borderRadius: "4px",
                                        cursor: "pointer",
                                        transition: "border 0.2s ease",
                                    }}
                                    onClick={() => setMainImage(img)}
                                />
                            ))
                        ) : (
                            <img
                                src="/images/no-image.png"
                                alt="No Thumbnail"
                                style={{
                                    width: "60px",
                                    height: "60px",
                                    objectFit: "cover",
                                    border: "1px solid #ddd",
                                    borderRadius: "4px",
                                    cursor: "pointer",
                                }}
                            />
                        )}
                    </div>


                </Col>

                <Col md={6}>
                    <h3 className="mb-3">{product.name}</h3>
                    <h4 className="price mb-3">{product.price.toLocaleString()} ₫</h4>
                    <p><strong>Mô tả:</strong> {product.description || "Chưa có mô tả"}</p>
                    <Button variant="outline-dark" className="mb-3 btn-add-cart" onClick={() => {
                        addToCart(product.id);
                    }}>Thêm Vào Giỏ Hàng</Button>
                    <hr style={{ borderTop: "3px solid #eabbb7", margin: "1.5rem 0" }} />
                    <p><strong>Loại:</strong> {product.categoryName || "Chưa có thông tin"}</p>
                    <p><strong>Tình trạng kho:</strong> {product.inventory > 0 ? `${product.inventory} sản phẩm` : "Hết hàng"}</p>
                    <p><strong>Trạng thái:</strong> {product.isBestSeller ? "Bán chạy" : product.isNew ? "Mới" : "Bình thường"}</p>
                </Col>
            </Row>

            <div className="mt-5">
                <ul className="nav nav-tabs">
                    <li className="nav-item">
                        <button
                            className={`nav-link ${activeTab === "ingredients" ? "active" : ""}`}
                            onClick={() => handleTabClick("ingredients")}
                        >
                            Thành phần
                        </button>
                    </li>
                    <li className="nav-item">
                        <button
                            className={`nav-link ${activeTab === "benefits" ? "active" : ""}`}
                            onClick={() => handleTabClick("benefits")}
                        >
                            Công dụng
                        </button>
                    </li>
                    <li className="nav-item">
                        <button
                            className={`nav-link ${activeTab === "howToUse" ? "active" : ""}`}
                            onClick={() => handleTabClick("howToUse")}
                        >
                            Hướng dẫn sử dụng
                        </button>
                    </li>
                </ul>
                <div className="p-3 border border-top-0" style={{ minHeight: "100px" }}>
                    {renderTabContent()}
                </div>
            </div>
        </div>
    );
};

export default ProductDetail;