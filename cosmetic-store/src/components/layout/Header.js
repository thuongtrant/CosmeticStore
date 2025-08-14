// import { useContext } from "react";
// import { Navbar, Container, Nav, NavDropdown, Form, Button } from "react-bootstrap";
// import { MyUserContext, MyDispatchContext } from "../../configs/MyContexts";
// import { Link, NavLink, useNavigate } from "react-router-dom";

// const Header = () => {
//     const user = useContext(MyUserContext);
//     const dispatch = useContext(MyDispatchContext);
//     const nav = useNavigate();
//     const logout = () => {
//         dispatch({type:"logout"});
//         nav("/login");
//     }
//     return (
//         <>
//             <Navbar expand="lg" className="bg-body-tertiary">
//                 <Container fluid>
//                     <Navbar.Brand href="#" style={{color:"#E0B7B3"}}>BFY Cosmetic Store</Navbar.Brand>
//                     <Navbar.Toggle aria-controls="navbarScroll" />
//                     <Navbar.Collapse id="navbarScroll">
//                         <Nav
//                             className="me-auto my-2 my-lg-0"
//                             style={{ maxHeight: '100px' }}
//                             navbarScroll
//                         >
//                             {user === null ? <>
//                                 <Link to="/login" className="nav-link">Đăng nhập</Link>
//                                 <Link to="/register" className="nav-link">Đăng ký</Link>
//                             </> :
//                                 <>
//                                         <>
//                                             <Link to="/home" className="nav-link">Trang chủ</Link>
//                                             {/* <Link to="" className="nav-link"></Link> */}
//                                             {/* <Link to="" className="nav-link"></Link> */}
//                                         </>



//                                     <Link to="/profile" className="nav-link" style={{color:"#E0B7B3"}}>
//                                          <span className="ms-2">{user.username}!</span>
//                                     </Link>
//                                     <Button className="btn btn-danger ms-2" onClick={logout}>
//                                         Đăng xuất
//                                     </Button>
//                                 </>}
//                         </Nav>
//                         {/* <Form className="d-flex">
//                             <Form.Control
//                                 type="search"
//                                 placeholder="Search"
//                                 className="me-2"
//                                 aria-label="Search"
//                             />
//                             <Button variant="outline-success">Search</Button>
//                         </Form> */}
//                     </Navbar.Collapse>
//                 </Container>
//             </Navbar>
//         </>
//     );
// }
// export default Header;
import { useContext } from "react";
import { Navbar, Container, Nav, Button, Dropdown, Badge } from "react-bootstrap";
import { MyUserContext, MyDispatchContext } from "../../configs/MyContexts";
import { Link, useNavigate } from "react-router-dom";
import { FaSearch, FaUser, FaShoppingBag } from "react-icons/fa";
import { CartContext } from "../../configs/CartContext";
import { NavLink } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import "../../styles/header.css";

const Header = () => {
    const user = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);
    const cartCount = useContext(CartContext);
    const nav = useNavigate();

    const logout = () => {
        dispatch({ type: "logout" });
        nav("/login");
    };

    return (
        <Navbar expand="lg" bg="white" className="shadow-sm py-3 fixed-top">
            <Container>
                <Navbar.Brand as={Link} to="/" style={{ color: "#E0B7B3", fontWeight: "bold" }}>
                    Beauty For You
                </Navbar.Brand>

                <Nav className="mx-auto">
                    <NavLink to="/home" className="nav-link-custom">
                        Sản phẩm
                    </NavLink>
                    <NavLink to="/about" className="nav-link-custom">
                        Về chúng tôi
                    </NavLink>
                    <NavLink to="/news" className="nav-link-custom">
                        Tin tức
                    </NavLink>
                </Nav>


                <div className="d-flex align-items-center">
                    <FaSearch
                        className="mx-3"
                        style={{ cursor: "pointer" }}
                        title="Tìm kiếm"
                    />

                    {user ? (
                        <Dropdown align="end">
                            <Dropdown.Toggle
                                variant="link"
                                bsPrefix="p-0 border-0 bg-transparent"
                                id="dropdown-user"
                                style={{ color: "black", marginBottom: "5px" }}
                            >
                                <FaUser
                                    style={{ fontSize: "18px", cursor: "pointer" }}
                                    title="Tài khoản"
                                />
                            </Dropdown.Toggle>

                            <Dropdown.Menu>
                                <Dropdown.Item as={Link} to="/profile">
                                    Hồ sơ
                                </Dropdown.Item>
                                <Dropdown.Item as={Link} to="/orders">
                                    Đơn hàng của bạn
                                </Dropdown.Item>
                                <Dropdown.Divider />
                                <Dropdown.Item onClick={logout}>
                                    Đăng xuất
                                </Dropdown.Item>
                            </Dropdown.Menu>
                        </Dropdown>
                    ) : (
                        <>
                            <Link to="/login" className="nav-link text-dark">
                                Đăng nhập
                            </Link>
                            <Link to="/register" className="nav-link text-dark">
                                Đăng ký
                            </Link>
                        </>
                    )}

                    <div style={{ position: "relative", cursor: "pointer", margin: "10px 14px 14px 12px" }}
                        onClick={() => nav("/cart")}>

                        <FaShoppingBag style={{ fontSize: "18px" }} />
                        {cartCount > 0 && (
                            <span
                                style={{
                                    position: "absolute",
                                    top: "-5px",
                                    right: "-8px",
                                    background: "#E0B7B3",
                                    color: "#fff",
                                    borderRadius: "50%",
                                    padding: "2px 6px",
                                    fontSize: "10px",

                                }}
                            >
                                {cartCount}
                            </span>
                        )}
                    </div>
                </div>
            </Container>
        </Navbar>
    );
};

export default Header;
