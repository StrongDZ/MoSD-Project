const map_state_to_vietnamese = {
  pending: "Đang chờ xử lý",
  paid: "Đã thanh toán",
  failed: "Thanh toán thất bại",
  confirmed: "Đã xác nhận",
};

const map_state_to_color = {
  pending: "text-yellow-600",
  paid: "text-green-600",
  failed: "text-red-600",
  confirmed: "text-blue-600",
};

export default function BookingItemCard({
  booking,
  onClick,
  specificInfo,
  type,
}) {
  return (
    <div
      className="flex border p-4 cursor-pointer hover:bg-pink-50 rounded-lg shadow-sm transition-all duration-200"
      onClick={() => onClick(booking)}
    >
      <img
        src={specificInfo.thumbnail}
        alt="Ảnh"
        className="w-24 h-24 object-cover rounded-lg mr-4"
      />
      <div className="flex-1">
        <h3 className="font-semibold text-pink-600">
          {type === "ship" ? specificInfo.shipName : specificInfo.hotelName}
        </h3>
        <p className="text-gray-600">{specificInfo.address}</p>
        <p className="text-gray-600">Ngày: {booking.startDate}</p>
        <p className="text-gray-600">
          Số người: {booking.adults + booking.children}
        </p>
        <p className="text-black-600 font-semibold mt-2">
          Tổng tiền: {booking.totalAmount.toLocaleString()} đ
        </p>
        <p className="text-gray-600">
          Trạng thái:{" "}
          <div
            className={`inline-block font-semibold ${
              map_state_to_color[booking.state.toLowerCase()] || "text-pink-600"
            }`}
          >
            {map_state_to_vietnamese[booking.state.toLowerCase()] ||
              booking.state}
          </div>
        </p>
      </div>
    </div>
  );
}
