import BookingItemCard from "./BookingItemTab";

export default function BookingList({ bookings, onItemClick, type }) {
  // Lọc ra những booking có dữ liệu cụ thể
  const validBookings = bookings.filter((booking) =>
    type === "ship" ? booking.ship : booking.hotel
  );

  // Nếu không có dữ liệu hợp lệ
  if (validBookings.length === 0) {
    return <div className="mt-4 text-gray-500">Không có dữ liệu</div>;
  }

  return (
    <div className="mt-4 space-y-4">
      {validBookings.map((booking) => {
        const specificInfo = type === "ship" ? booking.ship : booking.hotel;

        return (
          <BookingItemCard
            key={booking.bookingId}
            booking={booking}
            specificInfo={specificInfo}
            onClick={onItemClick}
            type={booking.state}
          />
        );
      })}
    </div>
  );
}
