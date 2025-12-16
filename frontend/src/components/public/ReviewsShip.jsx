import React, { useState, useEffect } from "react";
import { FaStar } from "react-icons/fa";
import config from "../../config";
import { handleErrorToast } from "../../utils/toastHandler";
import { axiosRequest } from "../../utils/axiosUtils";
import { useAuth } from "../../contexts/AuthProvider";
const ReviewsShip = ({ shipId }) => {
    const [reviews, setReviews] = useState([]);
    const [filteredStar, setFilteredStar] = useState(null);
    const [newReview, setNewReview] = useState({ name: "", content: "", stars: 5 });
    const { token } = useAuth();    

    const fetchReviews = async () => {
        try {
            const res = await axiosRequest({
                url: `${config.api.url}/api/ship/${shipId}/reviews`,
                method: "GET",
            });
            setReviews(res.data.data || []);
        } catch (err) {
            console.log(err);
            handleErrorToast(err, "Đã có lỗi xảy ra khi tải dữ liệu đánh giá!");
        }
    };

    useEffect(() => {
        fetchReviews();
    }, [shipId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axiosRequest({
                url: `${config.api.url}/api/ship/${shipId}/reviews`,
                method: "POST",
                data: newReview,
                token: token,
            });

            setNewReview({ name: "", content: "", stars: 5 });
            fetchReviews();
        } catch (err) {
            console.error("Lỗi gửi đánh giá:", err);
            handleErrorToast(err, "Đã có lỗi xảy ra khi gửi đánh giá!");
        }
    };

    const displayedReviews = filteredStar ? reviews.filter((r) => r.stars === filteredStar) : reviews;

    return (
        <div className="py-8 space-y-8">
            <h2 className="text-2xl font-bold">Đánh giá</h2>

            {/* Bộ lọc */}
            <div className="flex gap-2 mb-4">
                {[5, 4, 3, 2, 1].map((star) => (
                    <button
                        key={star}
                        className={`px-3 py-1 rounded-full border ${filteredStar === star ? "bg-primary text-white" : "bg-white text-gray-600"}`}
                        onClick={() => setFilteredStar(filteredStar === star ? null : star)}
                    >
                        {star} ⭐
                    </button>
                ))}
                <button className="px-3 py-1 border rounded-full text-gray-500" onClick={() => setFilteredStar(null)}>
                    Tất cả
                </button>
            </div>

            {/* Danh sách đánh giá */}
            <div className="space-y-4">
                {displayedReviews.length > 0 ? (
                    displayedReviews.map((review, index) => (
                        <div key={index} className="border p-4 rounded-xl shadow-sm bg-white">
                            <div className="flex items-center justify-between mb-1">
                                <h4 className="font-semibold">{review.name}</h4>
                                <div className="flex text-yellow-400">
                                    {[...Array(review.stars)].map((_, i) => (
                                        <FaStar key={i} />
                                    ))}
                                </div>
                            </div>
                            <p className="text-gray-700 text-sm">{review.content}</p>
                        </div>
                    ))
                ) : (
                    <p className="text-gray-500">Chưa có đánh giá nào.</p>
                )}
            </div>

            {/* Form đánh giá */}
            <div className="border p-6 rounded-xl bg-gray-50 mt-6">
                <h3 className="text-lg font-semibold mb-4">Gửi đánh giá của bạn</h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <input
                        type="text"
                        placeholder="Tên của bạn"
                        className="w-full border p-2 rounded"
                        value={newReview.name}
                        onChange={(e) => setNewReview({ ...newReview, name: e.target.value })}
                        required
                    />
                    <textarea
                        placeholder="Nội dung đánh giá"
                        className="w-full border p-2 rounded"
                        rows={3}
                        value={newReview.content}
                        onChange={(e) => setNewReview({ ...newReview, content: e.target.value })}
                        required
                    />
                    <div className="flex items-center space-x-2">
                        <label>Số sao:</label>
                        <select
                            value={newReview.stars}
                            onChange={(e) => setNewReview({ ...newReview, stars: parseInt(e.target.value) })}
                            className="border p-1 rounded"
                        >
                            {[5, 4, 3, 2, 1].map((s) => (
                                <option key={s} value={s}>
                                    {s}
                                </option>
                            ))}
                        </select>
                    </div>
                    <button type="submit" className="bg-pink-400 text-white px-6 py-2 rounded-full hover:bg-pink-500 transition-all">
                        Gửi đánh giá
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ReviewsShip;
