import React, { useEffect, useRef, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import { Container, Card, Alert, Button } from "react-bootstrap";
import MySpinner from "../layout/MySpinner";

const MoMoReturn = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [paymentResult, setPaymentResult] = useState(null);
  const [status, setStatus] = useState(undefined);
  const pollTimerRef = useRef(null);

  const orderIdFromQuery = searchParams.get("orderId") || "";
  const resultCode = searchParams.get("resultCode");
  const message = searchParams.get("message") || "";
  const sessionId = orderIdFromQuery || localStorage.getItem("momoSessionId") || "";

  const clearPoll = () => {
    if (pollTimerRef.current) {
      clearTimeout(pollTimerRef.current);
      pollTimerRef.current = null;
    }
  };

  const checkOrderStatus = async (sid) => {
    try {
      const response = await authApis().get(endpoints['checkoutSid'](sid));
      if (response.status === 200 && response.data?.status === "COMPLETED" && response.data?.order?.orderNumber) {
        localStorage.removeItem("momoSessionId");
        setPaymentResult(response.data);
        setStatus("COMPLETED");
        setTimeout(() => navigate(`/orderDetail/${response.data.order.orderNumber}`), 800);
        return true;
      }
      return false;
    } catch (error) {
      return false;
    }
  };

  const startPolling = (sid) => {
    const pollInterval = [2000, 2000, 3000, 3000, 5000, 5000, 5000, 5000, 5000, 5000];
    let attempt = 0;
    const startTime = Date.now();

    const poll = async () => {
      if (Date.now() - startTime > 60000) {
        setPaymentResult({
          success: false,
          message: "Đang xác nhận thanh toán. Vui lòng đợi thêm hoặc nhấn Thử lại để cập nhật trạng thái.",
        });
        setStatus("PENDING");
        return;
      }

      const completed = await checkOrderStatus(sid);
      if (!completed) {
        const delay = pollInterval[Math.min(attempt++, pollInterval.length - 1)];
        pollTimerRef.current = setTimeout(poll, delay);
      }
    };

    setStatus("PENDING");
    setPaymentResult({
      success: false,
      message: "Đang xác nhận thanh toán. Vui lòng đợi trong giây lát...",
      pending: true,
      sessionId: sid,
    });
    poll();
  };
  const handleBackToHome = () => navigate("/home");

  useEffect(() => {
    let isCancelled = false;

    const processReturn = async () => {
      if (!sessionId || resultCode == null) {
        setPaymentResult({
          success: false,
          message: "Thông tin thanh toán không hợp lệ",
        });
        setStatus("ERROR");
        setLoading(false);
        return;
      }

      try {
        const url = `/api/payment/momo/return?orderId=${encodeURIComponent(sessionId)}&resultCode=${encodeURIComponent(resultCode)}&message=${encodeURIComponent(message)}`;
        const response = await authApis().get(url);

        if (isCancelled) return;

        // Payment completed
        if (response.status === 200 && response.data?.status === "COMPLETED" && response.data?.order?.orderNumber) {
          localStorage.removeItem("momoSessionId");
          setPaymentResult(response.data);
          setStatus("COMPLETED");
          setLoading(false);
          setTimeout(() => navigate(`/orderDetail/${response.data.order.orderNumber}`), 800);
          return;
        }

        // Payment pending
        if (response.status === 202 || response.data?.status === "PENDING" || response.data?.pending) {
          setPaymentResult(response.data);
          setStatus("PENDING");
          setLoading(false);
          startPolling(sessionId);
          return;
        }

        // Payment failed
        setPaymentResult(response.data);
        setStatus(response.data?.status || "FAILED");
        setLoading(false);
      } catch (error) {
        if (isCancelled) return;
        setPaymentResult({
          success: false,
          message: "Có lỗi xảy ra khi xử lý kết quả thanh toán",
        });
        setStatus("ERROR");
        setLoading(false);
      }
    };

    processReturn();

    return () => {
      isCancelled = true;
      clearPoll();
    };
  }, [sessionId, resultCode, message, navigate]);

  const handleViewOrder = () => {
    if (paymentResult?.order?.orderNumber) {
      navigate(`/orderDetail/${paymentResult.order.orderNumber}`);
    }
  };


  const retry = () => window.location.reload();

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: "60vh" }}>
        <div className="text-center">
          <MySpinner animation="border" />
          <p className="mt-3">Đang xử lý kết quả thanh toán...</p>
        </div>
      </Container>
    );
  }

  const isSuccess = status === "COMPLETED";
  const isPending = status === "PENDING";

  return (
    <Container className="mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <Card className="shadow">
            <Card.Header className="text-center bg-light">
              <h4 style={{ color: "#E0B7B3" }}>Kết quả thanh toán MoMo</h4>
            </Card.Header>

            <Card.Body className="text-center">
              {isSuccess ? (
                <>
                  <div className="mb-4">
                    <div style={{ fontSize: "4rem", color: "#28a745" }}>✅</div>
                    <h5 className="text-success">Thanh toán thành công!</h5>
                  </div>

                  <Alert variant="success">
                    <strong>Đơn hàng:</strong> {paymentResult?.order?.orderNumber}
                    <br />
                    <strong>Số tiền:</strong> {paymentResult?.order?.totalAmount?.toLocaleString()} ₫
                  </Alert>

                  <p className="mb-4">
                    Cảm ơn bạn đã thanh toán qua MoMo. Đơn hàng của bạn đã được xác nhận và sẽ được xử lý sớm nhất.
                  </p>

                  <div className="d-grid gap-2">
                    <Button
                      style={{ backgroundColor: "#E0B7B3", borderColor: "#E0B7B3" }}
                      onClick={handleViewOrder}
                    >
                      Xem chi tiết đơn hàng
                    </Button>
                    <Button variant="outline-secondary" onClick={handleBackToHome}>
                      Về trang chủ
                    </Button>
                  </div>
                </>
              ) : isPending ? (
                <>
                  <div className="mb-4">
                    <div style={{ fontSize: "4rem" }}>⏳</div>
                    <h5>Đang xác nhận thanh toán...</h5>
                  </div>

                  <Alert variant="warning">
                    <strong>Trạng thái:</strong> Đang xử lý từ MoMo / chờ IPN.
                    <br />
                    <span>{paymentResult?.message || "Vui lòng đợi trong giây lát..."}</span>
                  </Alert>

                  <div className="d-grid gap-2">
                    <Button
                      style={{ backgroundColor: "#E0B7B3", borderColor: "#E0B7B3" }}
                      onClick={retry}
                    >
                      Thử lại
                    </Button>
                    <Button variant="outline-secondary" onClick={handleBackToHome}>
                      Về trang chủ
                    </Button>
                  </div>
                </>
              ) : (
                <>
                  <div className="mb-4">
                    <div style={{ fontSize: "4rem", color: "#dc3545" }}>❌</div>
                    <h5 className="text-danger">Thanh toán thất bại!</h5>
                  </div>

                  <Alert variant="danger">
                    <strong>Lý do:</strong> {paymentResult?.message || "Không xác định"}
                  </Alert>

                  <p className="mb-4">
                    Rất tiếc, thanh toán của bạn đã thất bại. Vui lòng thử lại hoặc chọn phương thức thanh toán khác.
                  </p>

                  <div className="d-grid gap-2">
                    <Button
                      style={{ backgroundColor: "#E0B7B3", borderColor: "#E0B7B3" }}
                      onClick={() => navigate("/cart")}
                    >
                      Quay lại giỏ hàng
                    </Button>
                    <Button variant="outline-secondary" onClick={handleBackToHome}>
                      Về trang chủ
                    </Button>
                  </div>
                </>
              )}
            </Card.Body>
          </Card>
        </div>
      </div>
    </Container>
  );
};

export default MoMoReturn;