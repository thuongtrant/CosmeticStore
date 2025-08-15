import React from "react";
import { Spinner } from "react-bootstrap";

const MySpinner = () => {
    return (
        <div className="text-center">
            <Spinner animation="border" role="status">
                <span className="visually-hidden">Loading...</span>
            </Spinner>
        </div>
    );
};

export default MySpinner;
