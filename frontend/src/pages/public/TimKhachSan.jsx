import React, { useState, useEffect } from "react";
import config from "../../config";
import { axiosRequest } from "../../utils/axiosUtils";
import {
  Container,
  Grid,
  Paper,
  TextField,
  Button,
  Box,
  Typography,
} from "@mui/material";

const TimKhachSan = () => {
  const [hotels, setHotels] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [cities, setCities] = useState([]);
  const [priceRangeOption, setPriceRangeOption] = useState("");
  const [hotelOptions, setHotelOptions] = useState([]);

  const [searchParams, setSearchParams] = useState({
    tenKhachSan: "",
    diaDiem: "",
    ngayNhanPhong: "",
    ngayTraPhong: "",
    soNguoi: 1,
  });

  const [filters, setFilters] = useState({
    giaRange: [0, 5000000],
    rating: 0,
  });

  const [selectedFeatures, setSelectedFeatures] = useState([]);

  const availableFeatures = [
    "Bồn tắm/Cabin tắm đứng",
    "Quầy bar",
    "Tivi",
    "Điều hòa",
    "Khu vực bãi tắm riêng",
  ];
  const PRICE_OPTIONS = [
    { label: "Tất cả mức giá", value: "" },
    { label: "Từ 1 đến 3 triệu", value: "1000000-3000000" },
    { label: "Từ 3 đến 6 triệu", value: "3000000-6000000" },
    { label: "Trên 6 triệu", value: "6000000-999999999" },
  ];

  useEffect(() => {
    const fetchCities = async () => {
      try {
        const response = await axiosRequest({
          url: `${config.api.url}/api/hotel/cities`,
          method: "GET",
        });
        const cityList = response.data.data;
        setCities([
          { value: "", label: "Tất cả địa điểm" },
          ...cityList.map((city) => ({ value: city, label: city })),
        ]);
      } catch (error) {
        console.error("Lỗi khi lấy danh sách thành phố:", error);
      }
    };
    fetchCities();
  }, []);

  const fetchHotels = async (page) => {
    try {
      setLoading(true);
      console.log("Đang tải dữ liệu cho trang:", page);
      const response = await axiosRequest({
        url: `${config.api.url}/api/hotel/search`,
        method: "GET",
        params: {
          name: searchParams.tenKhachSan,
          minPrice: filters.giaRange[0],
          maxPrice: filters.giaRange[1],
          city: searchParams.diaDiem,
          currentPage: page,
          pageSize: 6,
          features:
            selectedFeatures.length > 0
              ? selectedFeatures.join(",")
              : undefined,
        },
      });
      const data = response.data.data;
      console.log("Dữ liệu nhận được:", data);
      setHotels(data.result || []);
      setTotalPages(data.meta?.pages || 1);
    } catch (error) {
      console.error("Lỗi khi gọi API:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4">Search Hotels</Typography>
    </Container>
  );
};

export default TimKhachSan;

