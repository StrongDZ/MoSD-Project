import axios from "axios";
import React, { useState, useEffect } from "react";
import Portal from "../common/Portal";
import { useAuth } from "../../contexts/AuthProvider";
import config from "../../config";
import { handleErrorToast } from "../../utils/toastHandler";
import { axiosRequest } from "../../utils/axiosUtils";

const BookModal = ({ roomsData, onClose, type, hotelId, shipId }) => {
    const [formData, setFormData] = useState({
        hotelId: hotelId,
        shipId: shipId,
        customerName: "",
        phone: "",
        email: "",
        specialRequest: "",
        startDate: "",
        endDate: "",
        adults: 1,
        children: 0,
        totalAmount: 0,
    });

    const [showGuestPicker, setShowGuestPicker] = useState(false);
    const { token } = useAuth();
    const createBooking = async (bookingData) => {
        try {
            console.log(token);
            const response = await axiosRequest({
                url: `${config.api.url}/api/booking`,
                method: "POST",
                data: bookingData,
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            return response.data;
        } catch (err) {
            console.log(err);
            handleErrorToast(err, "Đã có lỗi xảy ra khi đặt phòng!");
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const roomList = roomsData.map((room) => ({
            roomId: room.roomInfo.roomId,
            quantity: room.quantity,
        }));

        // Gửi dữ liệu form tới server hoặc xử lý theo yêu cầu
        const bookingData = {
            type: type.toLowerCase(),
            roomList,
            ...formData,
        };

        console.log("Đặt phòng:", bookingData);

        const response = await createBooking(bookingData);
        if (response.responseCode === 201) {
            alert("Booking created successfully!");
            onClose();
        } else {
            alert("Failed to create booking: " + response.message);
        }

        alert("Đặt phòng thành công!");
        onClose();
    };

    const handleGuestChange = (type, value) => {
        setFormData((prev) => ({
            ...prev,
            [type]: prev[type] + value,
        }));
    };

    // Tính tổng tiền cho tất cả các phòng đã chọn
    const totalPrice = roomsData.reduce((sum, room) => {
        return sum + room.roomInfo.roomPrice * room.quantity;
    }, 0);

    useEffect(() => {
        setFormData((prev) => ({
            ...prev,
            totalAmount: totalPrice,
        }));
    }, [totalPrice]);

    return (
        <Portal>
            <div className="fixed inset-0 z-50">
                {/* Overlay with blur effect */}
                <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />

                {/* Modal Content */}
                <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-full max-w-2xl">
                    <div className="bg-white rounded-2xl p-6 relative overflow-y-auto max-h-[90vh] shadow-xl">
                        {/* Close Button */}
                        <button
                            onClick={onClose}
                            className="absolute top-4 right-4 text-xl font-bold text-gray-600 hover:text-black transition-colors"
                        >
                            ×
                        </button>

                        {/* Header */}
                        <h2 className="text-2xl font-semibold mb-4">Đặt phòng du thuyền</h2>

                        {/* Selected Rooms Info */}
                        <div className="space-y-4 mb-6">
                            {roomsData.map((roomData) => (
                                <div
                                    key={roomData.roomInfo.roomId}
                                    className="flex items-center gap-4 border p-4 rounded-lg hover:border-primary transition-colors"
                                >
                                    <img
                                        src={roomData.roomInfo.images[0]}
                                        alt={roomData.roomInfo.roomName}
                                        className="w-24 h-24 object-cover rounded-lg"
                                    />
                                    <div className="flex-grow">
                                        <h3 className="font-semibold text-lg">{roomData.roomInfo.roomName}</h3>
                                        <p className="text-sm text-gray-600">
                                            {roomData.roomInfo.size} m² · Tối đa {roomData.roomInfo.maxPersons} khách
                                        </p>
                                        <p className="text-pink-600 font-bold mt-1">{roomData.roomInfo.roomPrice.toLocaleString()} đ / KHÁCH</p>
                                    </div>
                                    <div className="text-right">
                                        <p className="text-sm text-gray-600">Số lượng:</p>
                                        <p className="font-semibold">{roomData.quantity}</p>
                                        <p className="text-pink-600 font-bold">
                                            {(roomData.roomInfo.roomPrice * roomData.quantity).toLocaleString()} đ
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* Booking Form */}
                        <form onSubmit={handleSubmit} className="mt-6 grid grid-cols-1 gap-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="text-sm font-medium">Ngày khởi hành</label>
                                    <input
                                        type="date"
                                        name="startDate"
                                        value={formData.startDate}
                                        onChange={handleChange}
                                        className="border p-2 rounded w-full focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                        required
                                    />
                                </div>

                                <div className="relative">
                                    <label className="text-sm font-medium">Số lượng khách</label>
                                    <button
                                        type="button"
                                        onClick={() => setShowGuestPicker((prev) => !prev)}
                                        className="border p-2 rounded w-full text-left focus:border-primary focus:ring-1 focus:ring-primary transition-all flex justify-between items-center"
                                    >
                                        <span>
                                            {formData.adults} Người lớn - {formData.children} Trẻ em
                                        </span>
                                        <span className="text-gray-500">▼</span>
                                    </button>

                                    {/* Guest Picker Dropdown */}
                                    {showGuestPicker && (
                                        <div className="absolute top-full left-0 w-full mt-2 bg-white border rounded-lg shadow-lg p-4 z-10">
                                            {/* Adults Picker */}
                                            <div className="mb-4">
                                                <div className="flex justify-between items-center mb-2">
                                                    <span className="font-medium">Người lớn</span>
                                                    <div className="flex items-center gap-2 border rounded-full px-3 py-1">
                                                        <button
                                                            type="button"
                                                            onClick={() => handleGuestChange("adults", -1)}
                                                            className="w-8 h-8 flex items-center justify-center text-xl font-bold text-gray-600 hover:text-primary transition-colors"
                                                            disabled={formData.adults <= 1}
                                                        >
                                                            −
                                                        </button>
                                                        <span className="w-8 text-center font-medium">{formData.adults}</span>
                                                        <button
                                                            type="button"
                                                            onClick={() => handleGuestChange("adults", 1)}
                                                            className="w-8 h-8 flex items-center justify-center text-xl font-bold text-gray-600 hover:text-primary transition-colors"
                                                        >
                                                            +
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>

                                            {/* Children Picker */}
                                            <div>
                                                <div className="flex justify-between items-center mb-2">
                                                    <span className="font-medium">Trẻ em</span>
                                                    <div className="flex items-center gap-2 border rounded-full px-3 py-1">
                                                        <button
                                                            type="button"
                                                            onClick={() => handleGuestChange("children", -1)}
                                                            className="w-8 h-8 flex items-center justify-center text-xl font-bold text-gray-600 hover:text-primary transition-colors"
                                                            disabled={formData.children <= 0}
                                                        >
                                                            −
                                                        </button>
                                                        <span className="w-8 text-center font-medium">{formData.children}</span>
                                                        <button
                                                            type="button"
                                                            onClick={() => handleGuestChange("children", 1)}
                                                            className="w-8 h-8 flex items-center justify-center text-xl font-bold text-gray-600 hover:text-primary transition-colors"
                                                        >
                                                            +
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>

                            <input
                                type="text"
                                name="customerName"
                                placeholder="Họ và tên"
                                value={formData.customerName}
                                onChange={handleChange}
                                className="border p-2 rounded w-full focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                required
                            />

                            <input
                                type="tel"
                                name="phone"
                                placeholder="Số điện thoại"
                                value={formData.phone}
                                onChange={handleChange}
                                className="border p-2 rounded w-full focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                required
                            />

                            <input
                                type="email"
                                name="email"
                                placeholder="Địa chỉ email"
                                value={formData.email}
                                onChange={handleChange}
                                className="border p-2 rounded w-full focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                required
                            />

                            <textarea
                                name="specialRequest"
                                placeholder="Yêu cầu đặc biệt của bạn"
                                value={formData.specialRequest}
                                onChange={handleChange}
                                className="border p-2 rounded w-full focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                                rows={3}
                            />

                            <div className="flex justify-between items-center pt-4 border-t mt-2">
                                <div>
                                    <p className="text-sm text-gray-600">Tổng thanh toán</p>
                                    <p className="text-xl font-bold text-pink-600">{totalPrice.toLocaleString()} đ</p>
                                </div>
                                <button type="submit" className="bg-pink-500 text-white px-6 py-2 rounded-full hover:bg-pink-600 transition-colors">
                                    Đặt ngay →
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </Portal>
    );
};

export default BookModal;
