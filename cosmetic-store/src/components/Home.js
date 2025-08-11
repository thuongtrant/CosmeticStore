import React, { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/MyContexts";
import { authApis, endpoints } from "../configs/Apis";
import { Card, Button, Row, Col, Form, Accordion } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import '../styles/cardProduct.css';
import "../styles/filter.css";
import { CartDispatchContext } from "../configs/CartContext";
import { useNavigate } from "react-router-dom";

const Home = () => {
    const user = useContext(MyUserContext);
    const cartDispatch = useContext(CartDispatchContext);

    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [ingredients, setIngredients] = useState([]);
    const [skinTypes, setSkinTypes] = useState([]);

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

    // Load products
    useEffect(() => {
        const loadProducts = async () => {
            try {
                let res = await authApis().get(endpoints['listProduct']);
                if (Array.isArray(res.data)) {
                    setProducts(res.data);
                } else {
                    console.error("API không trả về mảng:", res.data);
                    setProducts([]);
                }
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        loadProducts();
    }, []);

    // Add to cart
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
        <div className="container mt-4">
            <h3 className="text-center mb-4">Sản phẩm</h3>
            <Row>
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
                                        />
                                    ))}
                                </Accordion.Body>
                            </Accordion.Item>

                            {/* Phạm vi giá */}
                            <Accordion.Item eventKey="3">
                                <Accordion.Header>Phạm Vi Giá</Accordion.Header>
                                <Accordion.Body>
                                    <Form.Check type="radio" name="price" label="Dưới 200" />
                                    <Form.Check type="radio" name="price" label="200 - 400" />
                                    <Form.Check type="radio" name="price" label="400 trở lên" />
                                    <div className="d-flex gap-2 mt-2">
                                        <Form.Control size="sm" placeholder="Giá từ" />
                                        <Form.Control size="sm" placeholder="Đến" />
                                    </div>
                                </Accordion.Body>
                            </Accordion.Item>
                        </Accordion>

                        <Button className="filter-submit-btn mt-3">Tra Cứu</Button>
                    </div>
                </Col>

                {/* Danh sách sản phẩm */}
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
    );
};

export default Home;
