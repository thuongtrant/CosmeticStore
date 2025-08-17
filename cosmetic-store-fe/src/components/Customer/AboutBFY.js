import React from "react";
import { Container, Row, Col, Card } from "react-bootstrap";

const About = () => {
  return (
    <Container className="mt-5">
      <h2 className="text-center mb-4" style={{color:"#E0B7B3"}}>Về Chúng Tôi</h2>
      
      <Row className="mb-4">
        <Col md={6}>
          <img 
            src="/images/about-banner.jpg" 
            alt="BFY Store" 
            className="img-fluid rounded shadow" 
          />
        </Col>
        <Col md={6} className="d-flex flex-column justify-content-center">
          <h4 style={{color:"#E0B7B3"}}>Beauty For You</h4>
          <p>
            Chúng tôi là thương hiệu mỹ phẩm uy tín, luôn mang đến sản phẩm chất lượng cao,
            an toàn cho làn da và sức khỏe của khách hàng. 
            Với sứ mệnh giúp bạn tự tin tỏa sáng, BFY cam kết mang lại những trải nghiệm mua sắm tốt nhất.
          </p>
        </Col>
      </Row>

      <Row className="text-center mb-4">
        <Col md={4}>
          <Card className="p-3 shadow-sm">
            <h5>Sứ mệnh</h5>
            <p>Cung cấp sản phẩm mỹ phẩm chất lượng, bảo vệ và chăm sóc làn da.</p>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="p-3 shadow-sm">
            <h5>Tầm nhìn</h5>
            <p>Trở thành thương hiệu mỹ phẩm đáng tin cậy hàng đầu tại Việt Nam.</p>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="p-3 shadow-sm">
            <h5>Giá trị</h5>
            <p>Chất lượng – Uy tín – Khách hàng là trung tâm.</p>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col className="text-center">
          <h5>Liên hệ</h5>
          <p>Email: support@bfy.com | Điện thoại: 0123 456 789</p>
          <p>Địa chỉ: 123 Nguyễn Trãi, Hà Nội</p>
        </Col>
      </Row>
    </Container>
  );
};

export default About;
