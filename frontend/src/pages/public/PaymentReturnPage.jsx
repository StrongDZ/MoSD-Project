import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import config from "../../config";
import { axiosRequest } from "../../utils/axiosUtils";

const map_payment_response_code_to_message = {
  "00": "Giao dịch thành công",
  "07": "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).",
  "09": "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.",
  10: "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần",
  11: "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.",
  12: "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.",
  13: "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.",
  24: "Giao dịch không thành công do: Khách hàng hủy giao dịch",
  51: "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.",
  65: "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.",
  75: "Ngân hàng thanh toán đang bảo trì.",
  79: "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch",
  99: "Lỗi không xác định",
};

export default function PaymentReturnPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [paymentResult, setPaymentResult] = useState(null);
  const [isProcessing, setIsProcessing] = useState(true);

  useEffect(() => {
    const processPayment = async () => {
      try {
        // Get all VNPay parameters from URL
        const vnpParams = {};
        searchParams.forEach((value, key) => {
          vnpParams[key] = value;
        });

        console.log("Processing VNPay return parameters:", vnpParams);

        // Send to backend for verification and processing
        const response = await axiosRequest({
          url: `${config.api.baseUrl}/api/payment/process-vnpay-return`,
          method: "POST",
          data: vnpParams,
        });

        setPaymentResult(response.data);
      } catch (error) {
        console.error("Error processing payment:", error);
        setPaymentResult({
          success: false,
          message: "Có lỗi xảy ra khi xử lý thanh toán",
        });
      } finally {
        setIsProcessing(false);
      }
    };

    if (searchParams.toString()) {
      processPayment();
    } else {
      setIsProcessing(false);
      setPaymentResult({
        success: false,
        message: "Không tìm thấy thông tin thanh toán",
      });
    }
  }, [searchParams]);

  const handleBackToBookings = () => {
    navigate("/bookings");
  };

  if (isProcessing) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-pink-50 to-purple-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl shadow-2xl p-8 max-w-md w-full text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-pink-500 mx-auto mb-4"></div>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">
            Đang xử lý thanh toán
          </h2>
          <p className="text-gray-600">Vui lòng đợi trong giây lát...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 to-purple-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-2xl p-8 max-w-md w-full">
        {paymentResult?.success ? (
          <div className="text-center">
            {/* Success Icon */}
            <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-green-100 mb-4">
              <svg
                className="h-10 w-10 text-green-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
            </div>

            <h2 className="text-2xl font-bold text-gray-800 mb-2">
              Thanh toán thành công!
            </h2>
            <p className="text-gray-600 mb-6">
              Đơn hàng của bạn đã được thanh toán thành công. Chúng tôi đã gửi
              email xác nhận đến địa chỉ của bạn.
            </p>

            {/* Payment Details */}
            <div className="bg-gray-50 rounded-lg p-4 mb-6 text-left">
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Mã đơn hàng:</span>
                  <span className="font-medium text-gray-800">
                    {paymentResult.orderId}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Số tiền:</span>
                  <span className="font-medium text-gray-800">
                    {(parseInt(paymentResult.amount) / 100).toLocaleString()} đ
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Mã giao dịch:</span>
                  <span className="font-medium text-gray-800">
                    {paymentResult.transactionNo}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Email:</span>
                  <span className="font-medium text-gray-800 truncate">
                    {paymentResult.customerEmail}
                  </span>
                </div>
              </div>
            </div>

            <button
              onClick={handleBackToBookings}
              className="w-full bg-gradient-to-r from-pink-500 to-pink-600 hover:from-pink-600 hover:to-pink-700 text-white font-semibold py-3 px-6 rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
            >
              Xem lịch sử đặt phòng
            </button>
          </div>
        ) : (
          <div className="text-center">
            {/* Error Icon */}
            <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-red-100 mb-4">
              <svg
                className="h-10 w-10 text-red-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </div>

            <h2 className="text-2xl font-bold text-gray-800 mb-2">
              Thanh toán thất bại
            </h2>
            <p className="text-gray-600 mb-6">
              {paymentResult?.message ||
                "Đã có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại sau."}
            </p>

            {paymentResult?.orderId && (
              <div className="bg-gray-50 rounded-lg p-4 mb-6 text-left">
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Mã đơn hàng:</span>
                    <span className="font-medium text-gray-800">
                      {paymentResult.orderId}
                    </span>
                  </div>
                  {paymentResult.responseCode && (
                    <div className="flex justify-between">
                      <span className="text-gray-600">Mã lỗi:</span>
                      <span className="font-medium text-gray-800">
                        {map_payment_response_code_to_message[
                          paymentResult.responseCode
                        ] || "Lỗi không xác định"}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            )}

            <div className="space-y-3">
              <button
                onClick={handleBackToBookings}
                className="w-full bg-gradient-to-r from-pink-500 to-pink-600 hover:from-pink-600 hover:to-pink-700 text-white font-semibold py-3 px-6 rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
              >
                Quay lại lịch sử đặt phòng
              </button>
              <button
                onClick={() => navigate("/")}
                className="w-full bg-white border-2 border-gray-300 hover:border-pink-500 text-gray-700 hover:text-pink-600 font-semibold py-3 px-6 rounded-lg transition-all duration-200"
              >
                Về trang chủ
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
