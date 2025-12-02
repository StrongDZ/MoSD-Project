import React from "react";
import { useParams } from "react-router-dom";

const ChiTietKhachSan = () => {
    const { id } = useParams();

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-8 max-w-4xl">
                <h1 className="text-3xl font-bold mb-4">Chi tiết khách sạn (demo đơn giản)</h1>
                <p className="mb-2 text-gray-600">
                    ID khách sạn lấy từ URL: <span className="font-semibold text-pink-500">{id}</span>
                </p>
                <div className="mt-6 space-y-4 bg-white rounded-xl shadow-sm p-6">
                    <h2 className="text-xl font-semibold">Thông tin cơ bản</h2>
                    <p className="text-gray-700">
                        Đây là phiên bản rút gọn, chỉ hiển thị thông tin tĩnh để phục vụ mục đích demo / fake commit. Logic gọi API, đặt phòng, đánh
                        giá, swiper... đã được lược bỏ cho đơn giản.
                    </p>
                    <ul className="list-disc list-inside text-gray-700 space-y-1">
                        <li>Tên khách sạn: Khách sạn demo #{id}</li>
                        <li>Địa chỉ: Một địa chỉ demo bất kỳ</li>
                        <li>Xếp hạng: 5.0 (giả lập)</li>
                    </ul>
                </div>

                <div className="mt-6 grid md:grid-cols-2 gap-4">
                    <div className="bg-white rounded-xl shadow-sm p-4">
                        <h3 className="font-semibold mb-2">Mô tả ngắn</h3>
                        <p className="text-gray-700 text-sm">
                            Khách sạn này chỉ là dữ liệu tĩnh, không liên quan tới backend. Bạn có thể dùng trang này để test routing UI mà không cần
                            API.
                        </p>
                    </div>
                    <div className="bg-white rounded-xl shadow-sm p-4">
                        <h3 className="font-semibold mb-2">Ghi chú</h3>
                        <p className="text-gray-700 text-sm">
                            Toàn bộ phần đặt phòng, danh sách phòng, modal chi tiết phòng... đã được loại bỏ để code ngắn gọn hơn.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ChiTietKhachSan;
