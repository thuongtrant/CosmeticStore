import React, { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/MyContexts";
import { authApis, endpoints } from "../configs/Apis";
import { Card, Button, Row, Col, Form, Accordion } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import '../styles/cardProduct.css';
import "../styles/filter.css";
import "../styles/pagination.css"; // dùng chung style
import { CartDispatchContext } from "../configs/CartContext";
import { useNavigate } from "react-router-dom";
import qs from "qs";
import FirstHeader from "./layout/FirstHeader";

const Home = () => {
    const cartDispatch = useContext(CartDispatchContext);

    const [products, setProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [categories, setCategories] = useState([]);
    const [ingredients, setIngredients] = useState([]);
    const [skinTypes, setSkinTypes] = useState([]);

    const [selectedCategories, setSelectedCategories] = useState([]);
    const [selectedIngredients, setSelectedIngredients] = useState([]);
    const [selectedSkinTypes, setSelectedSkinTypes] = useState([]);
    const [minPrice, setMinPrice] = useState("");
    const [maxPrice, setMaxPrice] = useState("");
    const [keyword, setKeyword] = useState("");

    const [loading, setLoading] = useState(true);
    const [animate, setAnimate] = useState(false);

    const nav = useNavigate();

    // Load filters
    useEffect(() => {
        const loadFilters = async () => {
            try {
                let [catRes, ingRes, skinRes] = await Promise.all([
                    authApis().get(endpoints["categories"]),
                    authApis().get(endpoints["ingredients"]),
                    authApis().get(endpoints["skin-types"])
                ]);
                setCategories(catRes.data || []);
                setIngredients(ingRes.data || []);
                setSkinTypes(skinRes.data || []);
            } catch (err) {
                console.error("Lỗi load filters:", err);
            }
        };
        loadFilters();
    }, []);

    // Load sản phẩm với pagination
    useEffect(() => {
        const loadProducts = async () => {
            setLoading(true);
            setAnimate(false);
            try {
                let res = await authApis().get(endpoints['productsAllPaged'](currentPage, 9));
                if (res.data?.products) {
                    setProducts(res.data.products);
                    setTotalPages(res.data.totalPages);
                }
                setTimeout(() => setAnimate(true), 50); // trigger fade-in
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        loadProducts();
    }, [currentPage]);

    // Toggle checkbox
    const toggleSelection = (value, setState, state) => {
        if (state.includes(value)) {
            setState(state.filter(v => v !== value));
        } else {
            setState([...state, value]);
        }
    };

    // Gọi search
    const handleSearch = async () => {
        setLoading(true);
        setAnimate(false);
        try {
            const params = {
                keyword: keyword || null,
                categoryIds: selectedCategories.length > 0 ? selectedCategories : null,
                ingredientIds: selectedIngredients.length > 0 ? selectedIngredients : null,
                skinTypeIds: selectedSkinTypes.length > 0 ? selectedSkinTypes : null,
                minPrice: minPrice || null,
                maxPrice: maxPrice || null,
                sortBy: "id",
                sortDirection: "ASC",
                page: 0,
                size: 9
            };

            let res = await authApis().get(endpoints["search"], {
                params,
                paramsSerializer: (params) => qs.stringify(params, { arrayFormat: 'repeat' })
            });

            if (res.data?.products) {
                setProducts(res.data.products);
                setTotalPages(res.data.totalPages || 1);
                setCurrentPage(0);
            }
            setTimeout(() => setAnimate(true), 50);
        } catch (err) {
            console.error("Lỗi search products:", err);
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    const addToCart = async (productId) => {
        try {
            await authApis().post(endpoints["addToCart"], { productId, quantity: 1 });
            let res = await authApis().get(endpoints["cartCount"]);
            cartDispatch({ type: "set", payload: res.data });
        } catch (err) {
            console.error("Lỗi thêm vào giỏ hàng:", err);
        }
    };

    const renderPagination = () => (
        <div className="pagination-container">
            <button
                className="pagination-btn"
                disabled={currentPage === 0}
                onClick={() => setCurrentPage(prev => prev - 1)}
            >
                « 
            </button>

            {[...Array(totalPages)].map((_, index) => (
                <button
                    key={index}
                    className={`pagination-btn ${index === currentPage ? "active" : ""}`}
                    onClick={() => setCurrentPage(index)}
                >
                    {index + 1}
                </button>
            ))}

            <button
                className="pagination-btn"
                disabled={currentPage === totalPages - 1}
                onClick={() => setCurrentPage(prev => prev + 1)}
            >
                »
            </button>
        </div>
    );


    return (
        <>
            <FirstHeader />
            <div className="container mt-4">
                <h3 className="text-center mb-4">Sản phẩm</h3>
                <Row>
                    {/* FILTER */}
                    <Col md={3}>
                        <div className="filter-box p-3 shadow-sm">
                            <h5 className="fw-bold mb-3">LỌC</h5>

                            <Accordion alwaysOpen>
                                {/* Loại sản phẩm */}
                                <Accordion.Item eventKey="0">
                                    <Accordion.Header>Loại Sản Phẩm</Accordion.Header>
                                    <Accordion.Body>
                                        {categories.map(c => (
                                            <Form.Check
                                                key={c.id}
                                                type="checkbox"
                                                label={c.name}
                                                value={c.id}
                                                checked={selectedCategories.includes(c.id)}
                                                onChange={() => toggleSelection(c.id, setSelectedCategories, selectedCategories)}
                                            />
                                        ))}
                                    </Accordion.Body>
                                </Accordion.Item>

                                {/* Loại thành phần */}
                                <Accordion.Item eventKey="1">
                                    <Accordion.Header>Loại Thành Phần</Accordion.Header>
                                    <Accordion.Body>
                                        {ingredients.map(i => (
                                            <Form.Check
                                                key={i.id}
                                                type="checkbox"
                                                label={i.name}
                                                value={i.id}
                                                checked={selectedIngredients.includes(i.id)}
                                                onChange={() => toggleSelection(i.id, setSelectedIngredients, selectedIngredients)}
                                            />
                                        ))}
                                    </Accordion.Body>
                                </Accordion.Item>

                                {/* Loại da */}
                                <Accordion.Item eventKey="2">
                                    <Accordion.Header>Loại Da</Accordion.Header>
                                    <Accordion.Body>
                                        {skinTypes.map(s => (
                                            <Form.Check
                                                key={s.id}
                                                type="checkbox"
                                                label={s.name}
                                                value={s.id}
                                                checked={selectedSkinTypes.includes(s.id)}
                                                onChange={() => toggleSelection(s.id, setSelectedSkinTypes, selectedSkinTypes)}
                                            />
                                        ))}
                                    </Accordion.Body>
                                </Accordion.Item>

                                {/* Phạm vi giá */}
                                <Accordion.Item eventKey="3">
                                    <Accordion.Header>Phạm Vi Giá</Accordion.Header>
                                    <Accordion.Body>
                                        <div className="d-flex gap-2">
                                            <Form.Control
                                                size="sm"
                                                placeholder="Giá từ"
                                                value={minPrice}
                                                onChange={(e) => setMinPrice(e.target.value)}
                                            />
                                            <Form.Control
                                                size="sm"
                                                placeholder="Đến"
                                                value={maxPrice}
                                                onChange={(e) => setMaxPrice(e.target.value)}
                                            />
                                        </div>
                                    </Accordion.Body>
                                </Accordion.Item>
                            </Accordion>

                            <Button className="filter-submit-btn mt-3" onClick={handleSearch}>
                                Tra Cứu
                            </Button>
                        </div>
                    </Col>

                    {/* PRODUCTS */}
                    <Col md={9}>
                        {loading ? (
                            <MySpinner animation="border" />
                        ) : (
                            <>
                                <Row className={`fade-container ${animate ? "show" : ""}`}>
                                    {products.map((p) => (
                                        <Col key={p.id} md={4} className="mb-4">
                                            <Card
                                                className="h-100 shadow-sm card-custom"
                                                onClick={() => nav(`/productdetail/${p.id}`)}
                                                style={{ cursor: "pointer" }}
                                            >
                                                <Card.Img
                                                    variant="top"
                                                    src={p.mainImageUrl}
                                                    style={{ height: "250px", objectFit: "cover" }}
                                                />
                                                <Card.Body className="d-flex flex-column">
                                                    <Card.Title style={{ fontSize: "16px", fontWeight: "bold", minHeight: "48px" }}>
                                                        {p.name}
                                                    </Card.Title>
                                                    <Card.Text className="price" style={{ fontWeight: "bold" }}>
                                                        {p.price.toLocaleString()}₫
                                                    </Card.Text>
                                                    <Button
                                                        className="btn-add-cart mt-auto"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            addToCart(p.id);
                                                        }}
                                                    >
                                                        Thêm vào giỏ hàng
                                                    </Button>
                                                </Card.Body>
                                            </Card>
                                        </Col>
                                    ))}
                                </Row>
                                {renderPagination()}
                            </>
                        )}
                    </Col>
                </Row>
            </div>
        </>
    );
};

export default Home;
