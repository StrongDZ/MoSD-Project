import { useState } from "react";
import config from "../../config";
import { axiosRequest } from "../../utils/axiosUtils";
import { handleErrorToast } from "../../utils/toastHandler";

export default function BookingDetailModal({ booking, onClose, type }) {
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);

  if (!booking) return null;

  const handlePayment = async () => {
    try {
      setIsProcessingPayment(true);

      // Create payment URL
      const paymentResponse = await axiosRequest({
        url: `${config.api.baseUrl}/api/payment/create`,
        method: "POST",
        data: {
          amount: booking.totalAmount,
          orderId: booking.bookingId?.toString() || `BOOKING_${Date.now()}`,
          customerEmail: booking.email,
          bookingType: type, // Pass the booking type (hotel or ship)
        },
      });

      if (paymentResponse.data?.paymentUrl) {
        // Redirect to VNPay payment page
        window.location.href = paymentResponse.data.paymentUrl;
      } else {
        throw new Error("Payment URL not received");
      }
    } catch (error) {
      console.error("Payment error:", error);
      handleErrorToast(error, "Không thể tạo thanh toán. Vui lòng thử lại!");
      setIsProcessingPayment(false);
    }
  };

  const getPaymentStatusBadge = () => {
    if (!booking.state) return null;

    const statusConfig = {
      PENDING: {
        text: "Chưa thanh toán",
        color: "bg-yellow-100 text-yellow-800",
      },
      PAID: { text: "Đã thanh toán", color: "bg-green-100 text-green-800" },
      FAILED: { text: "Thanh toán thất bại", color: "bg-red-100 text-red-800" },
      CONFIRMED: { text: "Đã xác nhận", color: "bg-blue-100 text-blue-800" },
      CANCELLED: { text: "Đã hủy", color: "bg-gray-100 text-gray-800" },
      // Support lowercase for backward compatibility
      pending: {
        text: "Chưa thanh toán",
        color: "bg-yellow-100 text-yellow-800",
      },
      paid: { text: "Đã thanh toán", color: "bg-green-100 text-green-800" },
      failed: { text: "Thanh toán thất bại", color: "bg-red-100 text-red-800" },
      confirmed: { text: "Đã xác nhận", color: "bg-blue-100 text-blue-800" },
      cancelled: { text: "Đã hủy", color: "bg-gray-100 text-gray-800" },
    };

    const status = statusConfig[booking.state] || statusConfig.PENDING;

    return (
      <span
        className={`px-3 py-1 rounded-full text-xs font-semibold ${status.color}`}
      >
        {status.text}
      </span>
    );
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-md flex justify-center items-center z-[100] p-4">
      <div className="bg-white p-6 md:p-8 rounded-2xl w-full max-w-3xl relative shadow-2xl transform transition-all duration-300 scale-100 max-h-[90vh] overflow-y-auto">
        <button
          className="absolute top-4 right-4 text-2xl text-gray-500 hover:text-pink-500 transition-colors"
          onClick={onClose}
        >
          ×
        </button>
        <div className="flex items-center justify-between mb-6 pr-8">
          <h2 className="text-2xl font-bold text-pink-600">
            Chi tiết đặt phòng
          </h2>
          {getPaymentStatusBadge()}
        </div>

        <div className="space-y-4">
          {booking.rooms.map((roomData, i) => (
            <div
              key={i}
              className="flex border border-pink-100 p-4 rounded-xl shadow-sm hover:shadow-md transition-shadow"
            >
              <img
                src={roomData.room.images[0]}
                alt={roomData.room.roomName}
                className="w-24 h-24 object-cover rounded-lg mr-4 flex-shrink-0"
              />
              <div className="min-w-0 flex-1">
                <h3 className="font-semibold text-black-600 truncate">
                  {roomData.room.roomName}
                </h3>
                <p className="text-gray-600">Số lượng: {roomData.quantity}</p>
                <p className="text-gray-600">
                  Giá: {roomData.room.roomPrice.toLocaleString()} đ
                </p>
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
            <span className="font-medium">
              {booking.adults + booking.children}
            </span>
          </p>
          <p className="flex justify-between">
            <span className="text-gray-600">Lưu ý:</span>
            <span className="font-medium text-right flex-1 ml-4">
              {booking.specialRequest || "Không có"}
            </span>
          </p>
        </div>

        <div className="mt-6 pt-4 border-t border-pink-100">
          <p className="text-black-600 font-semibold text-lg text-right">
            Tổng tiền: {booking.totalAmount.toLocaleString()} đ
          </p>
        </div>

        {console.log("booking.state:", booking.state)}

        {/* Payment Button */}
        {(!booking.state ||
          booking.state === "PENDING" ||
          booking.state === "pending" ||
          booking.state === "FAILED" ||
          booking.state === "failed") && (
          <div className="mt-6 flex gap-3">
            <button
              onClick={handlePayment}
              disabled={isProcessingPayment}
              className={`flex-1 py-3 px-6 rounded-lg font-semibold text-white transition-all duration-200 ${
                isProcessingPayment
                  ? "bg-gray-400 cursor-not-allowed"
                  : "bg-gradient-to-r from-pink-500 to-pink-600 hover:from-pink-600 hover:to-pink-700 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
              }`}
            >
              {isProcessingPayment ? (
                <span className="flex items-center justify-center">
                  <svg
                    className="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    ></circle>
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    ></path>
                  </svg>
                  Đang xử lý...
                </span>
              ) : (
                <>
                  <svg
                    className="inline-block w-5 h-5 mr-2"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"
                    />
                  </svg>
                  Thanh toán ngay
                </>
              )}
            </button>
          </div>
        )}

        {(booking.state === "PAID" || booking.state === "paid") && (
          <div className="mt-6 p-4 bg-green-50 border border-green-200 rounded-lg">
            <div className="flex items-center text-green-700">
              <svg
                className="w-5 h-5 mr-2"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clipRule="evenodd"
                />
              </svg>
              <span className="font-medium">
                Đơn hàng đã được thanh toán thành công
              </span>
            </div>
          </div>
        )}

        {(booking.state === "CONFIRMED" || booking.state === "confirmed") && (
          <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <div className="flex items-center text-blue-700">
              <svg
                className="w-5 h-5 mr-2"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clipRule="evenodd"
                />
              </svg>
              <span className="font-medium">
                Đơn hàng đã được xác nhận bởi công ty
              </span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
