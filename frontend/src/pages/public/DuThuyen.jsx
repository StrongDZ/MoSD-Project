import React from "react";
import { useParams } from "react-router-dom";

const DuThuyen = () => {
    const { id } = useParams();

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-8 max-w-4xl">
                <h1 className="text-3xl font-bold mb-4">Chi tiết du thuyền (demo đơn giản)</h1>
                <p className="mb-2 text-gray-600">
                    ID du thuyền lấy từ URL: <span className="font-semibold text-pink-500">{id}</span>
                </p>
                <div className="mt-6 space-y-4 bg-white rounded-xl shadow-sm p-6">
                    <h2 className="text-xl font-semibold">Thông tin cơ bản</h2>
                    <p className="text-gray-700">
                        Trang này đã được rút gọn, chỉ hiển thị một số thông tin tĩnh để thuận tiện cho việc test giao diện và routing. Không còn gọi
                        API, không còn Swiper, modal đặt phòng hay đánh giá.
                    </p>
                    <ul className="list-disc list-inside text-gray-700 space-y-1">
                        <li>Tên du thuyền: Du thuyền demo #{id}</li>
                        <li>Lịch trình: Hạ Long / Lan Hạ (demo)</li>
                        <li>Xếp hạng: 5.0 (giả lập)</li>
                    </ul>
                </div>

                <div className="mt-6 grid md:grid-cols-2 gap-4">
                    <div className="bg-white rounded-xl shadow-sm p-4">
                        <h3 className="font-semibold mb-2">Mô tả ngắn</h3>
                        <p className="text-gray-700 text-sm">
                            Đây chỉ là nội dung tĩnh. Bạn có thể sửa/expand thêm nếu cần hiển thị nhiều thông tin demo hơn.
                        </p>
                    </div>
                    <div className="bg-white rounded-xl shadow-sm p-4">
                        <h3 className="font-semibold mb-2">Ghi chú</h3>
                        <p className="text-gray-700 text-sm">
                            Logic phòng, giá, đặt chỗ, reviews... đã được loại bỏ hoàn toàn để code ngắn gọn và dễ đọc hơn.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DuThuyen;
