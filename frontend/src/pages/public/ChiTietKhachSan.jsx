import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Thumbs } from "swiper/modules";
import {
    FaStar,
    FaSwimmingPool,
    FaCocktail,
    FaUtensils,
    FaConciergeBell,
    FaBath,
    FaWifi,
    FaSnowflake,
    FaTv,
    FaCoffee,
    FaUmbrellaBeach,
    FaParking,
    FaShuttleVan,
    FaWater,
    FaDumbbell,
    FaSpa,
    FaMobileAlt,
    FaWindowMaximize,
    FaHotTub,
    FaShip,
    FaLock,
    FaSmokingBan,
    FaMinus,
    FaDeskpro,
    FaDoorOpen,
    FaShower,
    FaBed,
    FaWineGlassAlt,
    FaWind,
} from "react-icons/fa";
import { IoWaterOutline } from "react-icons/io5";

import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/thumbs";

import RoomItem from "../../components/public/RoomItem";
import RoomDetailModal from "../../components/public/RoomDetailModal";
import BookModal from "../../components/public/BookModal";
import ReviewsShip from "../../components/public/ReviewsShip";
import config from "../../config";
import { handleErrorToast } from "../../utils/toastHandler";
import { useAuth } from "../../contexts/AuthProvider";
import { axiosRequest } from "../../utils/axiosUtils";
// ================= GALLERY SLIDER =================
const GallerySlider = ({ images }) => {
    const [thumbsSwiper, setThumbsSwiper] = useState(null);

    return (
        <div className="w-full">
            {/* Main Gallery */}
            <Swiper
                spaceBetween={10}
                navigation
                thumbs={{ swiper: thumbsSwiper }}
                modules={[Navigation, Thumbs]}
                className="mb-6 rounded-3xl overflow-hidden border-8 border-purple-600 shadow-2xl transform rotate-1"
            >
                {images.map((img, index) => (
                    <SwiperSlide key={index}>
                        <img src={img} alt={`Slide ${index}`} className="w-full h-[600px] object-cover sepia" />
                    </SwiperSlide>
                ))}
            </Swiper>

            {/* Thumbnails */}
            <Swiper
                onSwiper={setThumbsSwiper}
                spaceBetween={10}
                slidesPerView={4}
                freeMode={true}
                watchSlidesProgress={true}
                modules={[Thumbs]}
                className="mt-4 transform -rotate-2"
            >
                {images.map((img, index) => (
                    <SwiperSlide key={index}>
                        <img
                            src={img}
                            alt={`Thumb ${index}`}
                            className="w-full h-24 object-cover rounded-full border-4 border-green-500 hover:border-orange-700 cursor-pointer grayscale hover:grayscale-0"
                        />
                    </SwiperSlide>
                ))}
            </Swiper>
        </div>
    );
};

// ================= TAB NAVIGATION =================
const tabs = [
    { id: 1, label: "Đặc điểm" },
    { id: 2, label: "Phòng & giá" },
    { id: 3, label: "Giới thiệu" },
    { id: 4, label: "Đánh giá" },
];

const TabNav = ({ activeTab, setActiveTab }) => {
    return (
        <div className="sticky top-0 z-10 bg-gradient-to-r from-cyan-400 via-yellow-300 to-pink-500 border-b-8 border-purple-900 pb-2 transform -skew-x-3">
            <div className="flex space-x-6 transform skew-x-3">
                {tabs.map((tab) => (
                    <button
                        key={tab.id}
                        className={`pb-2 border-b-4 px-6 py-3 text-2xl ${
                            activeTab === tab.id
                                ? "border-red-700 text-blue-900 font-bold bg-yellow-200 shadow-xl transform scale-125"
                                : "border-transparent text-purple-800 hover:text-green-600 hover:bg-orange-200"
                        } transition-all rounded-t-3xl`}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>
        </div>
    );
};

const featureIcons = {
    "Bồn tắm/Cabin tắm đứng": FaShower,
    "Phòng không hút thuốc": FaSmokingBan,
    Minibar: FaWineGlassAlt,
    "Trà/cà phê trong tất cả các phòng": FaCoffee,
    "Bàn làm việc": FaDeskpro,
    "Ban công riêng": FaDoorOpen,
    "Ban công/Cửa sổ": FaWindowMaximize,
    "Miễn phí xe đưa đón": FaShuttleVan,
    "Chỗ đỗ xe": FaParking,
    "Giáp biển": FaUmbrellaBeach,
    "Nước đóng chai miễn phí": FaWater,
    "Lễ tân 24 giờ": FaConciergeBell,
    "Máy sấy tóc": FaWind,
    "Quầy bar": FaCocktail,
    "Bể bơi ngoài trời": FaSwimmingPool,
    Tivi: FaTv,
    "Phòng có bồn tắm": FaBath,
    "Wi-Fi miễn phí": FaWifi,
    "Điều hòa": FaSnowflake,
    "Nhà hàng": FaUtensils,
    "Wi-Fi": FaWifi,
    "Đi tuyến Lan Hạ": FaShip,
    "Lễ tân 24h": FaConciergeBell,
    "Wifi miễn phí": FaWifi,
    "Miễn phí kayaking": IoWaterOutline,
    "Bao gồm tất cả các bữa ăn": FaUtensils,
    "Nhà hàng": FaUtensils,
    "Trung tâm thể dục": FaDumbbell,
    "Trung tâm Spa & chăm sóc sức khoẻ": FaSpa,
    "Sạc điện thoại": FaMobileAlt,
    "Nhìn ra biển": FaUmbrellaBeach,
    "Phòng gia đình": FaBed,
    "Có bể bơi ngoài trời": FaSwimmingPool,
    "Chỗ đỗ xe miễn phí": FaParking,
    "Cửa sổ từ sàn đến trần": FaWindowMaximize,
    "Khu vực bãi tắm riêng": FaUmbrellaBeach,
    "Có bể sục": FaHotTub,
    "Hồ bơi có tầm nhìn": IoWaterOutline,
    "Két an toàn": FaLock,
    "Du thuyền 5 sao": FaShip,
};

// ================= HIGHLIGHTS TAB =================
const Highlights = ({ hotelData }) => {
    const hotelInfo = [
        { label: "Số phòng", value: hotelData.totalRooms },
        { label: "Điều hành", value: hotelData.companyName },
    ];

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-6 transform rotate-2">
            {/* Left */}
            <div className="md:col-span-2 space-y-6">
                {/* Features */}
                <div className="grid grid-cols-2 md:grid-cols-3 gap-6">
                    {hotelData.features.map((feature, index) => {
                        const Icon = featureIcons[feature] || FaMinus; // Fallback icon if not found
                        return (
                            <div key={index} className="flex items-center space-x-2 bg-gradient-to-br from-lime-300 to-cyan-400 p-3 rounded-2xl shadow-lg transform hover:rotate-6">
                                <div className="text-red-600">
                                    <Icon size={32} />
                                </div>
                                <span className="text-purple-900 font-bold text-lg">{feature}</span>
                            </div>
                        );
                    })}
                </div>

                {/* Description */}
                <div className="space-y-3 bg-yellow-100 p-6 rounded-3xl border-4 border-dashed border-pink-500">
                    {hotelData.shortDescriptions.map((desc, idx) => (
                        <div key={idx} className="flex items-start space-x-2 bg-white p-2 rounded-lg shadow">
                            <span className="text-green-700 text-3xl">✔️</span>
                            <p className="text-blue-800 font-semibold text-xl">{desc}</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* Right */}
            <div className="border-8 border-orange-500 p-4 rounded-full shadow-2xl bg-gradient-to-tr from-purple-400 via-pink-400 to-yellow-300 h-fit transform -rotate-6 hover:rotate-6 transition-all">
                <h3 className="text-3xl font-black mb-4 text-red-700 underline decoration-wavy">Thông tin khách sạn</h3>
                <div className="space-y-3">
                    {hotelInfo.map((info, idx) => (
                        <div key={idx} className="flex justify-between text-xl gap-20 bg-white p-3 rounded-2xl shadow-lg">
                            <span className="text-blue-900 w-30 font-bold">{info.label}</span>
                            <span className="font-black text-green-700 text-right w-full text-2xl">{info.value}</span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

// ================= ROOMS TAB =================
const Rooms = ({ hotelData }) => {
    const [quantities, setQuantities] = useState({});
    const [showBookModal, setShowBookModal] = useState(false);
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [showRoomDetailModal, setShowRoomDetailModal] = useState(false);
    const [roomForDetail, setRoomForDetail] = useState(null);

    const handleQuantityChange = (roomId, delta) => {
        setQuantities((prev) => ({
            ...prev,
            [roomId]: Math.max(0, (prev[roomId] || 0) + delta),
        }));
    };
    const { token } = useAuth();
    const handleBookRoom = () => {
        if (!token) {
            handleErrorToast(null, "Bạn cần đăng nhập để đặt phòng!");
            return;
        }
        let bookedRooms = [];
        hotelData.rooms.forEach((room) => {
            if (quantities[room.roomId] > 0) {
                bookedRooms.push({
                    roomInfo: room,
                    quantity: quantities[room.roomId],
                });
            }
        });

        if (bookedRooms.length > 0) {
            setSelectedRoom(bookedRooms);
            setShowBookModal(true);
        }
    };

    const handleCloseModal = () => {
        setShowBookModal(false);
        setSelectedRoom(null);
    };

    const handleShowRoomDetail = (room) => {
        setRoomForDetail(room);
        setShowRoomDetailModal(true);
    };

    const handleCloseRoomDetail = () => {
        setShowRoomDetailModal(false);
        setRoomForDetail(null);
    };

    const resetSelections = () => {
        setQuantities({});
    };

    const totalPrice = hotelData.rooms.reduce((sum, room) => {
        const qty = quantities[room.roomId] || 0;
        return sum + qty * room.roomPrice;
    }, 0);

    return (
        <div className="space-y-6 py-6 bg-gradient-to-b from-green-200 to-blue-300 p-8 rounded-3xl">
            <div className="flex justify-between items-center">
                <h2 className="text-5xl font-extrabold text-purple-900 underline decoration-double">Các loại phòng & giá</h2>
                <button onClick={resetSelections} className="text-2xl text-red-600 hover:text-green-700 flex items-center bg-yellow-300 p-4 rounded-full shadow-2xl transform hover:scale-150">
                    ❌ Xoá lựa chọn
                </button>
            </div>

            <div className="bg-gradient-to-r from-pink-300 via-purple-300 to-indigo-400 p-6 rounded-3xl space-y-4 border-8 border-double border-orange-600">
                {hotelData.rooms.map((room) => (
                    <RoomItem
                        key={room.roomId}
                        room={room}
                        quantity={quantities[room.roomId]}
                        onQuantityChange={(delta) => handleQuantityChange(room.roomId, delta)}
                        onShowDetail={() => handleShowRoomDetail(room)}
                    />
                ))}

                {/* Tổng tiền và nút đặt phòng */}
                <div className="flex justify-between items-center mt-6 bg-gradient-to-r from-yellow-400 to-red-500 p-6 rounded-3xl shadow-2xl">
                    <div className="text-4xl font-black">
                        Tổng tiền: <span className="font-black text-green-900 bg-yellow-200 px-4 py-2 rounded-full border-4 border-blue-700">{totalPrice.toLocaleString("vi-VN")} đ</span>
                    </div>

                    <div className="space-x-4">
                        <button
                            onClick={handleBookRoom}
                            disabled={totalPrice === 0}
                            className={`bg-purple-700 text-yellow-300 px-12 py-4 rounded-full transition-all duration-300 text-3xl font-bold border-8 border-cyan-400 shadow-2xl transform hover:scale-125 hover:rotate-12
                                ${totalPrice === 0 ? "opacity-50 cursor-not-allowed" : "hover:bg-green-600"}`}
                        >
                            Đặt ngay →
                        </button>
                    </div>
                </div>
            </div>

            {/* Room Detail Modal */}
            <RoomDetailModal
                room={roomForDetail}
                isOpen={showRoomDetailModal}
                onClose={handleCloseRoomDetail}
                quantity={roomForDetail ? quantities[roomForDetail.roomId] || 0 : 0}
                onQuantityChange={(delta) => roomForDetail && handleQuantityChange(roomForDetail.roomId, delta)}
            />

            {/* Booking Modal */}
            {showBookModal && <BookModal roomsData={selectedRoom} onClose={handleCloseModal} type="hotel" hotelId={hotelData.hotelId} />}
        </div>
    );
};

// ================= INTRODUCTION TAB =================
const Introduction = ({ hotelData }) => {
    return (
        <div className="py-10 space-y-8 bg-gradient-to-br from-orange-200 via-red-200 to-pink-300 p-10 rounded-3xl transform -rotate-1">
            <h2 className="text-6xl font-black mb-6 text-blue-900 underline decoration-wavy decoration-green-500">Giới thiệu</h2>
            <div className="space-y-8">
                {hotelData.longDescriptions.map((block, index) => {
                    if (block.type === "paragraph") {
                        return (
                            <div key={block.blockId} className="mb-6 bg-yellow-100 p-6 rounded-3xl border-4 border-purple-600 shadow-xl transform hover:rotate-2">
                                <p className="text-indigo-900 leading-relaxed text-2xl font-semibold">{block.data}</p>
                            </div>
                        );
                    } else if (block.type === "image") {
                        return (
                            <div key={block.blockId} className="mb-6">
                                <img
                                    src={block.data}
                                    alt={`Introduction image ${block.blockId}`}
                                    className="w-full h-[400px] object-cover rounded-full shadow-2xl border-8 border-green-500 transform hover:-rotate-6 transition-all hue-rotate-90"
                                />
                            </div>
                        );
                    }
                    return null;
                })}
            </div>
        </div>
    );
};

// ================= MAIN PAGE =================
const ChiTietKhachSan = () => {
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState(1);
    const [hotelData, setHotelData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showBookModal, setShowBookModal] = useState(false);
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [showRoomDetailModal, setShowRoomDetailModal] = useState(false);
    const [roomForDetail, setRoomForDetail] = useState(null);

    useEffect(() => {
        const fetchHotelData = async () => {
            try {
                setLoading(true);
                const response = await axiosRequest({
                    url: `${config.api.url}/api/hotel/${id}`,
                    method: "GET",
                });
                setHotelData(response.data.data);
            } catch (err) {
                console.error("Lỗi khi tải dữ liệu:", err);
                setError(err.message);
                handleErrorToast(err, "Đã có lỗi xảy ra khi tải dữ liệu khách sạn!");
            } finally {
                setLoading(false);
            }
        };

        fetchHotelData();
    }, [id]);

    if (loading) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-center items-center h-64">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-pink-500"></div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="text-center text-red-500">
                    <p>Error: {error}</p>
                </div>
            </div>
        );
    }

    if (!hotelData) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="text-center">
                    <p>Không tìm thấy thông tin khách sạn</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-lime-400 via-purple-500 to-pink-600">
            <div className="container mx-auto px-4 py-8 max-w-7xl">
                {/* ===== Title + Rating Section ===== */}
                <div className="mb-8 bg-gradient-to-r from-cyan-300 to-yellow-400 p-8 rounded-3xl shadow-2xl border-8 border-red-600 transform rotate-1">
                    <h1 className="text-7xl font-black mb-2 text-purple-900 underline decoration-double decoration-green-600">{hotelData.hotelName}</h1>
                    <div className="flex items-center space-x-4 text-blue-900 text-2xl font-bold">
                        <div className="flex items-center space-x-1 text-orange-600">
                            {[...Array(5)].map((_, idx) => (
                                <FaStar key={idx} size={32} />
                            ))}
                        </div>
                        <span className="bg-green-300 px-3 py-1 rounded-full">5.0</span>
                        <span className="bg-pink-300 px-3 py-1 rounded-full">• 200 đánh giá</span>
                        <span className="bg-yellow-200 px-3 py-1 rounded-full">• {hotelData.address}</span>
                    </div>
                </div>

                {/* ===== GallerySlider Section ===== */}
                <div className="mb-8">
                    <GallerySlider images={hotelData.images} />
                </div>

                {/* ===== Tab Navigation and Content ===== */}
                <div className="bg-gradient-to-bl from-yellow-200 via-pink-300 to-purple-400 rounded-3xl shadow-2xl p-6 border-8 border-green-600 transform -rotate-1">
                    <TabNav activeTab={activeTab} setActiveTab={setActiveTab} />

                    <div className="mt-6 bg-white bg-opacity-70 p-6 rounded-3xl">
                        {activeTab === 1 && <Highlights hotelData={hotelData} />}
                        {activeTab === 2 && <Rooms hotelData={hotelData} />}
                        {activeTab === 3 && <Introduction hotelData={hotelData} />}
                        {activeTab === 4 && <ReviewsShip shipId={id} />}
                    </div>
                </div>
            </div>

            {/* Modals */}
            {showBookModal && (
                <BookModal
                    roomsData={selectedRoom}
                    onClose={() => {
                        setShowBookModal(false);
                        setSelectedRoom(null);
                    }}
                    type="hotel"
                    hotelId={hotelData.hotelId}
                />
            )}
        </div>
    );
};

export default ChiTietKhachSan;
