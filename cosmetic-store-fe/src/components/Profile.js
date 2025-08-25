import React, { useEffect, useState } from "react";
import { authApis, endpoints } from "../configs/Apis";
import { Card, Spinner } from "react-bootstrap";

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadProfile = async () => {
      try {
        let res = await authApis().get(endpoints["my-profile"]);
        setProfile(res.data);
      } catch (err) {
        console.error("Lỗi load profile:", err);
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, []);

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{height: "60vh"}}>
        <Spinner animation="border" variant="primary" />
      </div>
    );
  }

  if (!profile) {
    return <p className="text-center mt-5">Không tải được thông tin người dùng!</p>;
  }

  return (
    <div className="container mt-5">
      <h3 className="text-center mb-4" style={{color:"#E0B7B3"}}>Thông tin cá nhân</h3>
      <Card className="shadow-sm p-4" style={{maxWidth:"600px", margin:"0 auto"}}>
        <p><strong>Username:</strong> {profile.username}</p>
        <p><strong>Email:</strong> {profile.email}</p>
        <p><strong>Số điện thoại:</strong> {profile.phone}</p>
        <p><strong>Giới tính:</strong> {profile.gender}</p>
        <p><strong>Vai trò:</strong> {profile.role}</p>
        <p><strong>Ngày tạo:</strong> {new Date(profile.createdAt).toLocaleString()}</p>
      </Card>
    </div>
  );
};

export default Profile;
