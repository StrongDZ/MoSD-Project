export default function BookingDetailModal({ booking, onClose, type }) {
    if (!booking) return null;

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-md flex justify-center items-center z-[100] p-4">
            <div className="bg-white p-6 md:p-8 rounded-2xl w-full max-w-3xl relative shadow-2xl transform transition-all duration-300 scale-100 max-h-[90vh] overflow-y-auto">
                <button className="absolute top-4 right-4 text-2xl text-gray-500 hover:text-pink-500 transition-colors" onClick={onClose}>
                    ×
                </button>
                <h2 className="text-2xl font-bold mb-6 text-pink-600 pr-8">Chi tiết đặt phòng</h2>

                <div className="space-y-4">
                    {booking.rooms.map((roomData, i) => (
                        <div key={i} className="flex border border-pink-100 p-4 rounded-xl shadow-sm hover:shadow-md transition-shadow">
                            <img
                                src={roomData.room.images[0]}
                                alt={roomData.room.roomName}
                                className="w-24 h-24 object-cover rounded-lg mr-4 flex-shrink-0"
                            />
                            <div className="min-w-0 flex-1">
                                <h3 className="font-semibold text-black-600 truncate">{roomData.room.roomName}</h3>
                                <p className="text-gray-600">Số lượng: {roomData.quantity}</p>
                                <p className="text-gray-600">Giá: {roomData.room.roomPrice.toLocaleString()} đ</p>
                            </div>
                        </div>
                    ))}
                </div>

                <hr className="my-6 border-pink-100" />

                <div className="space-y-3 text-sm">
                    <p className="flex justify-between">
                        <span className="text-gray-600">Ngày khởi hành:</span>
                        <span className="font-medium">{booking.startDate}</span>
                    </p>
                    <p className="flex justify-between">
                        <span className="text-gray-600">Họ tên:</span>
                        <span className="font-medium">{booking.customerName}</span>
                    </p>
                    <p className="flex justify-between">
                        <span className="text-gray-600">SĐT:</span>
                        <span className="font-medium">{booking.phone}</span>
                    </p>
                    <p className="flex justify-between">
                        <span className="text-gray-600">Email:</span>
                        <span className="font-medium break-all">{booking.email}</span>
                    </p>
                    <p className="flex justify-between">
                        <span className="text-gray-600">Số lượng khách:</span>
                        <span className="font-medium">{booking.adults + booking.children}</span>
                    </p>
                    <p className="flex justify-between">
                        <span className="text-gray-600">Lưu ý:</span>
                        <span className="font-medium text-right flex-1 ml-4">{booking.specialRequest || "Không có"}</span>
                    </p>
                </div>

                <div className="mt-6 pt-4 border-t border-pink-100">
                    <p className="text-black-600 font-semibold text-lg text-right">Tổng tiền: {booking.totalAmount.toLocaleString()} đ</p>
                </div>
            </div>
        </div>
    );
}
