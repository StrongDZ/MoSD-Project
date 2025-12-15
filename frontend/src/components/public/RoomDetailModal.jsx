import React from "react";
import Portal from "../common/Portal";

const RoomDetailModal = ({ room, isOpen, onClose, onQuantityChange, quantity }) => {
    if (!isOpen || !room) return null;
    console.log("Room detail modal:", room);
    return (
        <Portal>
            <div className="fixed inset-0 z-50">
                {/* Overlay with blur effect */}
                <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />

                {/* Modal Content */}
                <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-full max-w-4xl">
                    <div className="bg-white rounded-2xl p-6 relative overflow-y-auto max-h-[90vh] shadow-xl">
                        {/* Close Button */}
                        <button
                            onClick={onClose}
                            className="absolute top-4 right-4 text-xl font-bold text-gray-600 hover:text-black transition-colors"
                        >
                            ×
                        </button>

                        <div className="flex flex-col md:flex-row gap-6">
                            {/* Left: Image & thumbnails */}
                            <div className="flex-1">
                                <img
                                    src={room.images?.[0]}
                                    alt={room.roomName}
                                    className="rounded-xl w-full h-60 object-cover hover:opacity-95 transition-opacity"
                                />
                                <div className="flex gap-2 mt-3 overflow-x-auto pb-2">
                                    {room.images?.map((img, i) => (
                                        <img
                                            key={i}
                                            src={img}
                                            alt={`thumb-${i}`}
                                            className="w-14 h-14 rounded-lg object-cover border hover:border-primary cursor-pointer transition-colors"
                                        />
                                    ))}
                                </div>
                            </div>

                            {/* Right: Room Info */}
                            <div className="flex-1 space-y-6">
                                <div>
                                    <h3 className="text-2xl font-bold mb-2">{room.roomName}</h3>
                                    <div className="text-gray-600 space-y-2">
                                        <p className="flex items-center gap-2">
                                            <span className="text-primary">📐</span> {room.size} m²
                                        </p>
                                        <p className="flex items-center gap-2">
                                            <span className="text-primary">👥</span> Tối đa: {room.maxPersons} khách
                                        </p>
                                    </div>
                                </div>

                                <div className="border-t border-b py-4">
                                    <h4 className="font-semibold mb-3">Tiện nghi phòng</h4>
                                    <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-sm">
                                        {room.roomFeatures?.map((feature, i) => (
                                            <li key={i} className="flex items-center gap-2 text-gray-600">
                                                <span className="text-primary">✓</span> {feature}
                                            </li>
                                        ))}
                                    </ul>
                                </div>

                                {/* Price and Quantity picker */}
                                <div className="flex justify-between items-center pt-4">
                                    <div className="space-y-1">
                                        <p className="text-sm text-gray-600">Giá phòng</p>
                                        <p className="text-xl font-bold text-pink-600">
                                            {room.roomPrice?.toLocaleString()} đ<span className="text-sm text-gray-500 font-normal"> / đêm</span>
                                        </p>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <div className="flex items-center gap-2 border rounded-full px-3 py-1">
                                            <button
                                                onClick={() => onQuantityChange(-1)}
                                                className="w-8 h-8 flex items-center justify-center text-xl font-bold text-gray-600 hover:text-primary transition-colors"
                                                disabled={quantity <= 0}
                                            >
                                                −
                                            </button>
                                            <span className="w-8 text-center font-medium">{quantity || 0}</span>
                                            <button
                                                onClick={() => onQuantityChange(1)}
                                                className="w-8 h-8 flex items-center justify-center text-xl font-bold text-gray-600 hover:text-primary transition-colors"
                                            >
                                                +
                                            </button>
                                        </div>
                                        <button
                                            onClick={onClose}
                                            className="bg-pink-500 text-white px-6 py-2 rounded-full hover:bg-pink-600 transition-colors"
                                        >
                                            Xác nhận
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </Portal>
    );
};

export default RoomDetailModal;
