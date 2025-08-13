import React from "react";
import { Container, Row, Col, Button } from "react-bootstrap";
import "../../styles/FirstHeader.css";

const FirstHeader = () => {
    return (
        <div className="hero-section">
            {/* Bên trái */}
            <div className="hero-left">
                <h3>KHÁM PHÁ VẺ ĐẸP BÊN TRONG CỦA BẠN VỚI BEAUTY FOR YOU</h3>
                <p>Món quà tuyệt vời cho chính bạn và những người thân yêu</p>
                <Button variant="light" className="hero-btn">Shop Now</Button>
            </div>

            {/* Bên phải */}
            <div className="hero-right">
                <img src="/images/hero-image.png" alt="Beauty Product" />
            </div>
        </div>
    );
};

export default FirstHeader;
