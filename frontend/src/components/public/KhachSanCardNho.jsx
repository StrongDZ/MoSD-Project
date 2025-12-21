import React from "react";
// Bạn có thể cần cài đặt react-icons: npm install react-icons
import { FaStar, FaMapMarkerAlt, FaBed } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function KhachSanCardNho({ hotel }) {
  const {
    hotelId,
    hotelName,
    totalRooms,
    hotelPrice,
    city,
    address,
    mapLink,
    thumbnail,
  } = hotel;

  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/khach-san/${hotelId}`);
  };

  return (
    <div
      className="max-w-sm rounded-lg overflow-hidden shadow-lg bg-white mx-auto my-4"
      onClick={handleClick}
    >
      {/* Phần hình ảnh và đánh giá */}
      <div className="relative">
        <img
          className="w-full h-48 object-cover"
          src={thumbnail}
          alt={hotelName}
        />
        {/* <div className="absolute top-3 left-3 bg-yellow-400 bg-opacity-90 text-white text-xs font-semibold px-2.5 py-1 rounded-md flex items-center space-x-1">
          <FaStar className="w-3 h-3" />
          <span>4.0 (0) đánh giá</span>
        </div> */}
      </div>

      {/* Phần nội dung */}
      <div className="p-4">
        {/* Địa điểm */}
        <div className="flex items-center text-gray-500 text-sm mb-1">
          <FaMapMarkerAlt className="w-4 h-4 mr-1.5" />
          <span>{city}</span>
        </div>

        {/* Tên khách sạn */}
        <h2 className="text-xl font-bold text-gray-900 mb-1">{hotelName}</h2>

        {/* Số phòng */}
        <div className="flex items-center text-gray-500 text-sm mb-4">
          <FaBed className="w-4 h-4 mr-1.5" />
          <span>{totalRooms} phòng</span>
        </div>

        {/* Phần giá và nút đặt */}
        <div className="flex justify-between items-end">
          <div>
            <p className="text-lg font-bold">
              {hotelPrice.toLocaleString()}đ / phòng
            </p>
          </div>
          <button className="bg-[#EC80B1] hover:bg-[#EC80B1] text-white font-semibold py-2 px-5 rounded-full transition duration-150 ease-in-out">
            Đặt ngay
          </button>
        </div>
      </div>
    </div>
  );
}
