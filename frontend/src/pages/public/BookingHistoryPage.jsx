import { useState } from "react";
//import { bookings } from "../data/mockBookings";
import BookingHistoryTabs from "../../components/public/BookingHistoryTab";
import BookingList from "../../components/public/BookingList";
import BookingDetailModal from "../../components/public/BookingDetailModal";
import config from "../../config";
import { useAuth } from "../../contexts/AuthProvider";
import { useEffect } from "react";
import { handleErrorToast } from "../../utils/toastHandler";
import { axiosRequest } from "../../utils/axiosUtils";

export default function BookingHistoryPage() {
    const [selectedType, setSelectedType] = useState("ship");
    const [bookingList, setBookingList] = useState([]);
    const [selectedBooking, setSelectedBooking] = useState(null);
    const { token } = useAuth();
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        setIsLoading(true);
        if (!token) return;

        const fetchBookings = async () => {
            try {
                const response = await axiosRequest({
                    url: `${config.api.baseUrl}/api/booking/my-bookings/${selectedType}`,
                    method: "GET",
                    token: token,
                });
                setBookingList(response.data.data);
            } catch (error) {
                console.error("Error fetching bookings:", error);
                handleErrorToast(error, "Đã có lỗi xảy ra khi tải lịch sử đặt phòng!");
            } finally {
                setIsLoading(false);
            }
        };

        fetchBookings();
    }, [token, selectedType]);

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="container mx-auto px-4 py-6">
                <div className="flex gap-6">
                    {/* Sidebar */}
                    <div className="w-48 flex-shrink-0">
                        <div className="bg-white rounded-xl shadow-sm p-4 sticky top-6">
                            <div className="flex flex-col space-y-2">
                                <button
                                    className={`px-4 py-3 rounded-lg transition-all duration-200 text-left ${
                                        selectedType === "ship"
                                            ? "bg-pink-400 text-white font-bold shadow-md"
                                            : "bg-white border border-pink-200 text-pink-500 hover:bg-pink-50"
                                    }`}
                                    onClick={() => setSelectedType("ship")}
                                >
                                    Du thuyền
                                </button>
                                <button
                                    className={`px-4 py-3 rounded-lg transition-all duration-200 text-left ${
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
                        {isLoading ? (
                            <div className="flex justify-center items-center h-[calc(100vh-200px)]">
                                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-pink-500"></div>
                            </div>
                        ) : (
                            <div className="bg-white rounded-xl shadow-sm p-6">
                                <BookingList bookings={bookingList} onItemClick={setSelectedBooking} type={selectedType} />
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Modal */}
            <BookingDetailModal booking={selectedBooking} onClose={() => setSelectedBooking(null)} type={selectedType} />
        </div>
    );
}
