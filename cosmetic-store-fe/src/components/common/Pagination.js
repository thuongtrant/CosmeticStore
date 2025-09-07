import React from 'react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import '../../styles/pagination.css';

const Pagination = ({ currentPage, totalPages, onPageChange, maxVisiblePages = 3 }) => {
    if (totalPages <= 1) return null;

    const generatePageNumbers = () => {
        const pages = [];
        const halfVisible = Math.floor(maxVisiblePages / 2);

        // Xác định range của các trang hiển thị
        let startPage = Math.max(1, currentPage - halfVisible);
        let endPage = Math.min(totalPages, currentPage + halfVisible);

        // Điều chỉnh để luôn hiển thị đủ số trang
        if (endPage - startPage + 1 < maxVisiblePages) {
            if (startPage === 1) {
                endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
            } else if (endPage === totalPages) {
                startPage = Math.max(1, endPage - maxVisiblePages + 1);
            }
        }

        // Nút Previous
        pages.push(
            <button
                key="prev"
                className="pagination-btn pagination-nav"
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 1}
                title="Trang trước"
            >
                <FaChevronLeft size={12} />
            </button>
        );

        // Trang đầu tiên (nếu không trong range)
        if (startPage > 1) {
            pages.push(
                <button
                    key={1}
                    className={`pagination-btn ${currentPage === 1 ? 'active' : ''}`}
                    onClick={() => onPageChange(1)}
                >
                    1
                </button>
            );

            // Dấu ... nếu có gap
            if (startPage > 2) {
                pages.push(
                    <span key="dots-start" className="pagination-dots">
                        ⋯
                    </span>
                );
            }
        }

        // Các trang trong range
        for (let i = startPage; i <= endPage; i++) {
            pages.push(
                <button
                    key={i}
                    className={`pagination-btn ${currentPage === i ? 'active' : ''}`}
                    onClick={() => onPageChange(i)}
                >
                    {i}
                </button>
            );
        }

        // Trang cuối cùng (nếu không trong range)
        if (endPage < totalPages) {
            // Dấu ... nếu có gap
            if (endPage < totalPages - 1) {
                pages.push(
                    <span key="dots-end" className="pagination-dots">
                        ⋯
                    </span>
                );
            }

            pages.push(
                <button
                    key={totalPages}
                    className={`pagination-btn ${currentPage === totalPages ? 'active' : ''}`}
                    onClick={() => onPageChange(totalPages)}
                >
                    {totalPages}
                </button>
            );
        }

        // Nút Next
        pages.push(
            <button
                key="next"
                className="pagination-btn pagination-nav"
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
                title="Trang tiếp theo"
            >
                <FaChevronRight size={12} />
            </button>
        );

        return pages;
    };

    return (
        <div className="pagination-container">
            {generatePageNumbers()}
        </div>
    );
};

export default Pagination;
