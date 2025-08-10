import { useContext } from "react";
import { Navbar, Container, Nav, NavDropdown, Form, Button } from "react-bootstrap";
import { MyUserContext, MyDispatchContext } from "../../configs/MyContexts";
import { Link, NavLink, useNavigate } from "react-router-dom";

const Header = () => {
    const user = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);
    const nav = useNavigate();
    const logout = () => {
        dispatch({type:"logout"});
        nav("/login");
    }
    return (
        <>
            <Navbar expand="lg" className="bg-body-tertiary">
                <Container fluid>
                    <Navbar.Brand href="#" style={{color:"#E0B7B3"}}>BFY Cosmetic Store</Navbar.Brand>
                    <Navbar.Toggle aria-controls="navbarScroll" />
                    <Navbar.Collapse id="navbarScroll">
                        <Nav
                            className="me-auto my-2 my-lg-0"
                            style={{ maxHeight: '100px' }}
                            navbarScroll
                        >
                            {user === null ? <>
                                <Link to="/login" className="nav-link">Đăng nhập</Link>
                                <Link to="/register" className="nav-link">Đăng ký</Link>
                            </> :
                                <>
                                        <>
                                            <Link to="/home" className="nav-link">Trang chủ</Link>
                                            {/* <Link to="" className="nav-link"></Link> */}
                                            {/* <Link to="" className="nav-link"></Link> */}
                                        </>
                                        

                                    
                                    <Link to="/profile" className="nav-link" style={{color:"#E0B7B3"}}>
                                         <span className="ms-2">{user.username}!</span>
                                    </Link>
                                    <Button className="btn btn-danger ms-2" onClick={logout}>
                                        Đăng xuất
                                    </Button>
                                </>}
                        </Nav>
                        {/* <Form className="d-flex">
                            <Form.Control
                                type="search"
                                placeholder="Search"
                                className="me-2"
                                aria-label="Search"
                            />
                            <Button variant="outline-success">Search</Button>
                        </Form> */}
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
}
export default Header;