import React, { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/MyContexts";
import { authApis, endpoints } from "../configs/Apis";
import { Card, Button, Row, Col, Form, Accordion } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import '../styles/cardProduct.css';
import "../styles/filter.css";
import { CartDispatchContext } from "../configs/CartContext";
import { useNavigate } from "react-router-dom";
import qs from "qs";
import FirstHeader from "./layout/FirstHeader";
const Home = () => {
    const user = useContext(MyUserContext);
    const cartDispatch = useContext(CartDispatchContext);

    const [products, setProducts] = useState([]);
    const [originalProducts, setOriginalProducts] = useState([]);
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

    useEffect(() => {
        const loadProducts = async () => {
            setLoading(true);
            try {
                let res = await authApis().get(endpoints['listProduct']);
                if (Array.isArray(res.data)) {
                    setProducts(res.data);
                    setOriginalProducts(res.data); // Lưu lại bản gốc
                }
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        loadProducts();
    }, []);

    // Toggle checkbox
    const toggleSelection = (value, setState, state) => {
        if (state.includes(value)) {
            setState(state.filter(v => v !== value));
        } else {
            setState([...state, value]);
        }
    };

    // Gọi search khi click Tra Cứu
    const handleSearch = async () => {
        // Kiểm tra nếu tất cả filter trống thì giữ nguyên
        if (
            !keyword &&
            selectedCategories.length === 0 &&
            selectedIngredients.length === 0 &&
            selectedSkinTypes.length === 0 &&
            !minPrice &&
            !maxPrice
        ) {
            setProducts(originalProducts);
            return;
        }

        setLoading(true);
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
                size: 20
            };

            let res = await authApis().get(endpoints["search"], {
                params,
                paramsSerializer: (params) => {
                    return qs.stringify(params, { arrayFormat: 'repeat' });
                }
            });

            // Response backend: { products: [...], currentPage, totalPages, ... }
            if (res.data && Array.isArray(res.data.products)) {
                setProducts(res.data.products);
            } else {
                setProducts([]);
            }
        } catch (err) {
            console.error("Lỗi search products:", err);
            setProducts([]);
        } finally {
            setLoading(false);
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
        } catch (err) {
            console.error("Lỗi thêm vào giỏ hàng:", err);
        }
    };

    return (
        <>
            <FirstHeader />
            <div className="container mt-4">
                <h3 className="text-center mb-4">Sản phẩm</h3>
                <Row>
                    {/* FILTER */}
                    <Col md={3}>
                        <div className="filter-box p-3 shadow-sm">
                            <h5 className="fw-bold mb-3" style={{ marginLeft: "5px" }}>LỌC</h5>

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
                            <Row>
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
                        )}
                    </Col>
                </Row>
            </div>

        </>
    );
};

export default Home;
