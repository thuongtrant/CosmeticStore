import { Container, Row, Col, Form, Button } from "react-bootstrap";

const Footer = () => {
    return (
        <footer style={{ backgroundColor: "#E0B7B3", color: "#fff", padding: "20px 0" }}>
            <Container>
                <Row className="align-items-start" style={{ fontSize: "14px" }}>
                    <Col md={3} style={{ lineHeight: "1.6" }}>
                        <h6 style={{ fontWeight: "bold" }}>Beauty For You</h6>
                        <p style={{ marginBottom: "4px" }}>Discover nature's beauty with our natural care products.</p>
                        <p style={{ marginBottom: "4px" }}>+38 050 123 45 67</p>
                        <p style={{ marginBottom: "4px" }}>bfy@gmail.com</p>
                        <p style={{ marginBottom: "4px" }}>Kiyv, Ukraine</p>
                    </Col>
                    <Col md={2}>
                        <h6 style={{ fontWeight: "bold" }}>HELP</h6>
                        <p>Contact us</p>
                        <p>FAQ</p>
                        <p>Shipping & Returns</p>
                    </Col>
                    <Col md={2}>
                        <h6 style={{ fontWeight: "bold" }}>MY ACCOUNT</h6>
                        <p>Addresses</p>
                        <p>Order Status</p>
                        <p>Wishlist</p>
                    </Col>
                    <Col md={2}>
                        <h6 style={{ fontWeight: "bold" }}>CUSTOMER CARE</h6>
                        <p>About us</p>
                        <p>Blog</p>
                    </Col>
                    <Col md={3}>
                        <h6 style={{ fontWeight: "bold" }}>SIGN UP FOR EMAILS</h6>
                        <p style={{ marginBottom: "8px" }}>Stay informed, subscribe to our newsletter now!</p>
                        <Form className="d-flex">
                            <Form.Control
                                type="email"
                                placeholder="Email"
                                style={{ borderRadius: "4px", fontSize: "14px" }}
                            />
                            <Button variant="light" className="ms-2" style={{ padding: "0 10px" }}>→</Button>
                        </Form>
                    </Col>
                </Row>
                <hr style={{ borderColor: "rgba(255,255,255,0.4)", margin: "10px 0" }} />
                <Row>
                    <Col className="text-center" style={{ fontSize: "12px" }}>
                        <small>© 2025 Bloom Beauty</small>
                        <div>
                            <a href="#" style={{ color: "#fff", marginRight: 10 }}>Privacy Policy</a>
                            <a href="#" style={{ color: "#fff" }}>Terms And Conditions</a>
                        </div>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
};

export default Footer;
