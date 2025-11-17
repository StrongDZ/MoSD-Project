import React, { useState } from "react";
import config from "../../config";
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

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4">Search Hotels</Typography>
    </Container>
  );
};

export default TimKhachSan;

