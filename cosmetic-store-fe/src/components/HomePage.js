import React, { useEffect, useState, useContext } from "react";
import { authApis, endpoints } from "../configs/Apis";
import { Card, Button, Row, Col } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import { CartDispatchContext } from "../configs/CartContext";
import { useNavigate } from "react-router-dom";
import "../styles/cardProduct.css";
import "../styles/pagination.css";
import FirstHeader from "./layout/FirstHeader";

const HomePage = () => {
    const cartDispatch = useContext(CartDispatchContext);

    // Sản phẩm mới
    const [newProducts, setNewProducts] = useState([]);
    const [newPage, setNewPage] = useState(0);
    const [newTotalPages, setNewTotalPages] = useState(0);

    // Sản phẩm bán chạy
    const [bestProducts, setBestProducts] = useState([]);
    const [bestPage, setBestPage] = useState(0);
    const [bestTotalPages, setBestTotalPages] = useState(0);

    // Custom posts
    const [customPosts, setCustomPosts] = useState([
        {
            title: "Beauty For you",
            image: "/images/post1.png",
            description: "Chúng tôi sẽ giúp bạn tỏa sáng tự nhiên. Kem dưỡng da mặt và body với chiết xuất từ ​​hoa sen giúp dưỡng ẩm sâu và trẻ hóa. Phù hợp với mọi loại da. Thuần chay, không thử nghiệm trên động vật, thân thiện với môi trường.",
            hashtags: ["kemduong", "duongam", "trehoa", "thanchay", "byf"],
        },
        {
            title: "Mẹo làm đẹp với BFY",
            image: "/images/post2.png",
            description: "Bộ Mặt nạ mắt tinh chất hoa của chúng tôi. Mỗi mặt nạ có sự pha trộn độc đáo của các chiết xuất hoa để cấp ẩm và nuôi dưỡng làn da của bạn. Trải nghiệm tinh chất hoa trong quy trình chăm sóc da của bạn.",
            hashtags: ["matna", "capam", "nuoiduong", "chamsocdamat", "duongda"],
        },
    ]);

    const [loadingNew, setLoadingNew] = useState(true);
    const [loadingBest, setLoadingBest] = useState(true);
    const nav = useNavigate();

    const [animateNew, setAnimateNew] = useState(false);
    const [animateBest, setAnimateBest] = useState(false);

    const loadNewProducts = async (page = 0) => {
        try {
            setAnimateNew(false);
            let res = await authApis().get(endpoints["productsByTypePaged"]("new", page, 4));
            setNewProducts(res.data.products || []);
            setNewPage(res.data.currentPage);
            setNewTotalPages(res.data.totalPages);
            setTimeout(() => setAnimateNew(true), 50);
        } finally {
            setLoadingNew(false);
        }
    };

    const loadBestProducts = async (page = 0) => {
        try {
            setAnimateBest(false);
            let res = await authApis().get(endpoints["productsByTypePaged"]("bestseller", page, 4));
            setBestProducts(res.data.products || []);
            setBestPage(res.data.currentPage);
            setBestTotalPages(res.data.totalPages);
            setTimeout(() => setAnimateBest(true), 50);
        } finally {
            setLoadingBest(false);
        }
    };

    useEffect(() => {
        loadNewProducts(newPage);
    }, [newPage]);

    useEffect(() => {
        loadBestProducts(bestPage);
    }, [bestPage]);

    const renderPagination = (currentPage, totalPages, setPage) => (
        <div className="pagination-container">
            <button className="pagination-btn" disabled={currentPage === 0} onClick={() => setPage(currentPage - 1)}>«</button>
            {[...Array(totalPages)].map((_, index) => (
                <button key={index} className={`pagination-btn ${index === currentPage ? "active" : ""}`} onClick={() => setPage(index)}>{index + 1}</button>
            ))}
            <button className="pagination-btn" disabled={currentPage === totalPages - 1} onClick={() => setPage(currentPage + 1)}>»</button>
        </div>
    );

    const renderProductList = (products) => (
        <Row className="g-3">
            {products.map((p) => (
                <Col key={p.id} xs={6} md={3}>
                    <Card className="h-100 shadow-sm card-custom" onClick={() => nav(`/productdetail/${p.id}`)} style={{ cursor: "pointer" }}>
                        <Card.Img variant="top" src={p.mainImageUrl} style={{ height: "250px", objectFit: "cover" }} />
                        <Card.Body className="d-flex flex-column">
                            <Card.Title style={{ fontSize: "14px", fontWeight: "bold", minHeight: "40px" }}>{p.name}</Card.Title>
                            <Card.Text className="price" style={{ fontWeight: "bold" }}>{p.price.toLocaleString()}₫</Card.Text>

                            {/* Kiểm tra tồn kho trước khi hiển thị nút */}
                            {p.inventory && p.inventory > 0 ? (
                                <Button
                                    className="btn-add-cart mt-auto"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        addToCart(p.id);
                                    }}
                                >
                                    Thêm Vào Giỏ Hàng
                                </Button>
                            ) : (
                                <Button
                                    variant="secondary"
                                    className="mt-auto"
                                    disabled
                                    style={{ cursor: "not-allowed" }}
                                    onClick={(e) => e.stopPropagation()}
                                >
                                    Hết hàng
                                </Button>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            ))}
        </Row>
    );

    const addToCart = async (productId) => {
        try {
            await authApis().post(endpoints["addToCart"], { productId, quantity: 1 });
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

    const renderCustomPost = (post, index) => (
        <Row key={index} className="g-3 mb-5" style={{padding: "20px" }}>
            {index % 2 === 0 ? (
                <>
                    <Col md={6} className="d-flex flex-column justify-content-center" style={{paddingRight:"45px"}}>
                        <h1 style={{ fontWeight: "bold" }}>{post.title}</h1>
                        <p style={{fontSize: "17px"}}>{post.description}</p>
                        <div>{post.hashtags.map((tag, idx) => <span key={idx} className="me-2" style={{ color: "#6c757d" }}>#{tag}</span>)}</div>
                    </Col>
                    <Col md={6}><img src={post.image} alt={post.title} style={{ width: "100%", height: "auto" }} /></Col>
                </>
            ) : (
                <>
                    <Col md={6}><img src={post.image} alt={post.title} style={{ width: "100%", height: "auto" }} /></Col>
                    <Col md={6} className="d-flex flex-column justify-content-center" style={{paddingLeft:"45px"}}>
                        <h1 style={{ fontWeight: "bold" }}>{post.title}</h1>
                        <p style={{fontSize: "17px"}}>{post.description}</p>
                        <div>{post.hashtags.map((tag, idx) => <span key={idx} className="me-2" style={{ color: "#6c757d" }}>#{tag}</span>)}</div>
                    </Col>
                </>
            )}
        </Row>
    );

    return (
        <>
            <FirstHeader />
            <div className="container mt-4" style={{ color: "#E0B7B3" }}>
                <section className="mb-5 text-center">
                    <h4 className="fw-bold mb-3">SẢN PHẨM MỚI</h4>
                    <div className={`product-list ${animateNew ? "show" : ""}`}>{loadingNew ? <MySpinner /> : renderProductList(newProducts)}</div>
                    {renderPagination(newPage, newTotalPages, setNewPage)}
                </section>
                <section className="text-center">
                    <h4 className="fw-bold mb-3">BÁN CHẠY</h4>
                    <div className={`product-list ${animateBest ? "show" : ""}`}>{loadingBest ? <MySpinner /> : renderProductList(bestProducts)}</div>
                    {renderPagination(bestPage, bestTotalPages, setBestPage)}
                </section>
                {customPosts.map(renderCustomPost)}

            </div>
        </>
    );
};
export default HomePage;