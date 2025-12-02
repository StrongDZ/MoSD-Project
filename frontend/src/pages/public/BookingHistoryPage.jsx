import React, { useState } from "react";

const shipBookings = [
    {
        id: 1,
        code: "SHP-001",
        name: "Du thuyền Hạ Long 2N1Đ",
        status: "Đã hoàn thành",
    },
    {
        id: 2,
        code: "SHP-002",
        name: "Du thuyền Lan Hạ 3N2Đ",
        status: "Đang chờ",
    },
];

const hotelBookings = [
    {
        id: 3,
        code: "HTL-001",
        name: "Khách sạn Hà Nội 1 đêm",
        status: "Đã hủy",
    },
    {
        id: 4,
        code: "HTL-002",
        name: "Resort Đà Nẵng 2 đêm",
        status: "Đã hoàn thành",
    },
];

export default function BookingHistoryPage() {
    const [selectedType, setSelectedType] = useState("ship");

    const list = selectedType === "ship" ? shipBookings : hotelBookings;

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-6">
                <h1 className="text-2xl font-bold mb-4">Lịch sử đặt chỗ (demo, không gọi API)</h1>
                <div className="flex gap-6">
                    {/* Sidebar */}
                    <div className="w-48 flex-shrink-0">
                        <div className="bg-white rounded-xl shadow-sm p-4 sticky top-6">
                            <div className="flex flex-col space-y-2">
                                <button
                                    className={`px-4 py-3 rounded-lg text-left ${
                                        selectedType === "ship"
                                            ? "bg-pink-400 text-white font-bold shadow-md"
                                            : "bg-white border border-pink-200 text-pink-500 hover:bg-pink-50"
                                    }`}
                                    onClick={() => setSelectedType("ship")}
                                >
                                    Du thuyền
                                </button>
                                <button
                                    className={`px-4 py-3 rounded-lg text-left ${
                                        selectedType === "hotel"
                                            ? "bg-pink-400 text-white font-bold shadow-md"
                                            : "bg-white border border-pink-200 text-pink-500 hover:bg-pink-50"
                                    }`}
                                    onClick={() => setSelectedType("hotel")}
                                >
                                    Khách sạn
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Main Content */}
                    <div className="flex-1">
                        <div className="bg-white rounded-xl shadow-sm p-6">
                            {list.length === 0 ? (
                                <p className="text-gray-500 text-center">Chưa có đặt chỗ nào.</p>
                            ) : (
                                <ul className="space-y-3">
                                    {list.map((item) => (
                                        <li key={item.id} className="border border-gray-100 rounded-lg px-4 py-3 flex justify-between items-center">
                                            <div>
                                                <p className="font-semibold">{item.name}</p>
                                                <p className="text-sm text-gray-500">{item.code}</p>
                                            </div>
                                            <span className="text-sm text-pink-500 font-medium">{item.status}</span>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
