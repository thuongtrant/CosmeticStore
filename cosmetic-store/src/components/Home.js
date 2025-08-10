import React, { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/MyContexts";
import Apis, { authApis, endpoints } from "../configs/Apis";
import { Card, Button, Row, Col, Spinner } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import '../styles/cardProduct.css';
import { useNavigate } from "react-router-dom";
const Home = () => {
    const user = useContext(MyUserContext);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const nav = useNavigate();
    useEffect(() => {
        const loadProducts = async () => {
            try {
                let res = await authApis().get(endpoints['listProduct']);
                console.log("API data:", res.data);
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


    return (
        <div className="container mt-4">
            <h3 className="text-center">Sản phẩm</h3>
            {loading ? (
                <div className="text-center">
                    <MySpinner animation="border" />
                </div>
            ) : (
                <Row>
                    {products.map((p) => (
                        <Col key={p.id} md={3} className="mb-4">
                            <Card className="h-100 shadow-sm card-custom"
                                onClick={() => nav(`/productdetail/${p.id}`)}
                                style={{ cursor: "pointer" }}>
                                <Card.Img
                                    variant="top"
                                    src={p.mainImageUrl}
                                    style={{
                                        height: "250px",
                                        objectFit: "cover"
                                    }}
                                />
                                <Card.Body className="d-flex flex-column">
                                    <Card.Title
                                        style={{
                                            fontSize: "16px",
                                            fontWeight: "bold",
                                            minHeight: "48px"
                                        }}
                                    >
                                        {p.name}
                                    </Card.Title>
                                    <Card.Text
                                        className="price"
                                        style={{ fontWeight: "bold" }}
                                    >
                                        {p.price.toLocaleString()}₫
                                    </Card.Text>
                                    <Button className="btn-add-cart mt-auto">
                                        Thêm vào giỏ hàng
                                    </Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}
        </div>
    );
};

export default Home;
