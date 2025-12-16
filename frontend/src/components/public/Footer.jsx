import React from "react";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-[#4e2c32] border-t-4 border-purple-500 text-white py-6 px-4 mt-8">
      <div className="max-w-6xl mx-auto  sm:flex-row sm:items-center gap-4">
        {/* Logo Section */}
        <div className="flex items-center gap-3">
          <Link to="/" className="-m-1.5 p-1.5 flex items-center gap-2">
            <div className="w-12 h-12 rounded-full bg-pink-300"></div>
          </Link>
          <div>
            <p className="text-lg font-handwritten text-white leading-none">
              monkey D<span className="text-sm ml-1 align-super">vn</span>
            </p>
          </div>
        </div>

        {/* Text Content */}
        <div className="text-sm leading-relaxed mt-8 sm:mt-0 ">
          <p>
            Công ty TNHH Du Lịch và Dịch Vụ{" "}
            <span className="italic">monkey D vn</span>
          </p>
          <p>
            Tầng 7, số nhà 25, ngõ 38 phố Yên Lãng, phường Láng Hạ, Đống Đa,
            TP.Hà Nội
          </p>
          <p>
            Mã số doanh nghiệp: 01931248724948 do Sở Kế hoạch và Đầu tư Thành
            phố Hà Nội cấp ngày 05/06/2023
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
