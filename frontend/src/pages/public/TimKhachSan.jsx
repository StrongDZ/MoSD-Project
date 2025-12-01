import React, { useState, useEffect } from "react";
import config from "../../config";
import {
  Container,
  Grid,
  Paper,
  TextField,
  Button,
  Box,
  Typography,
  InputAdornment,
  Slider,
  FormGroup,
  FormControlLabel,
  Checkbox,
  Pagination,
  Stack,
  MenuItem,
  Autocomplete,
} from "@mui/material";
import { Search, LocationOn, CalendarToday, Person } from "@mui/icons-material";
import SearchIcon from "@mui/icons-material/Search";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import LongCard from "../../components/public/LongCard";
import { handleErrorToast } from "../../utils/toastHandler";
import { axiosRequest } from "../../utils/axiosUtils";
const TimKhachSan = () => {
  const [hotels, setHotels] = useState([]);
  const [location, setLocation] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [cities, setCities] = useState([]);
  const [priceRangeOption, setPriceRangeOption] = useState("");
  const [hotelOptions, setHotelOptions] = useState([]);

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

  // useEffect(() => {
  //   const fetchFeatures = async () => {
  //     try {
  //       const response = await axiosRequest({
  //         url: `${config.api.url}/api/hotel/features`,
  //         method: "GET",
  //       });
  //       setAvailableFeatures(response.data.data || []);
  //     } catch (error) {
  //       console.error("Lỗi khi lấy danh sách features:", error);
  //     }
  //   };
  //   fetchFeatures();
  // }, []);

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

  useEffect(() => {
    fetchHotels(currentPage);
  }, [currentPage, searchParams, filters, selectedFeatures]);

  const handleSearch = () => {
    console.log("Tìm kiếm với params:", searchParams);
    setCurrentPage(1); // Reset về trang 1 khi tìm kiếm
    fetchHotels(1);
  };

  useEffect(() => {
    // Reset trang về 1 khi thay đổi bộ lọc
    setCurrentPage(1);
  }, [filters, searchParams]);

  const handleGiaRangeChange = (event, newValue) => {
    setFilters({ ...filters, giaRange: newValue });
  };

  const handleTienIchChange = (name) => (event) => {
    setFilters({
      ...filters,
      tienIch: {
        ...filters.tienIch,
        [name]: event.target.checked,
      },
    });
  };

  const handlePageChange = (event, value) => {
    console.log("Chuyển sang trang:", value);
    setCurrentPage(value);
  };

  // Hàm gọi API suggest tên khách sạn
  const handleHotelInputChange = async (event, value) => {
    if (!value) {
      setHotelOptions([]);
      setSearchParams({ ...searchParams, tenKhachSan: "" });
      return;
    }
    try {
      const res = await axiosRequest({
        url: `${config.api.url}/api/hotel/suggest?q=${value}`,
        method: "GET",
      });
      setHotelOptions(res.data.data || []);
    } catch (err) {
      setHotelOptions([]);
    }
    setSearchParams({ ...searchParams, tenKhachSan: value });
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <div className="absolute inset-0 bg-[url('/background-pattern.svg')] bg-cover opacity-5 -z-10"></div>

      {/* Search Bar */}
      <Paper
        elevation={3}
        sx={{ px: 4, py: 5, borderRadius: 5, mb: 4, bgcolor: "#fff" }}
      >
        <Typography
          variant="h4"
          fontWeight="bold"
          align="center"
          gutterBottom
          color="#EC80B1"
        >
          Bạn lựa chọn khách sạn nào?
        </Typography>
        <Typography align="center" color="text.secondary" mb={4}>
          Hơn 100 khách sạn hạng sang giá tốt đang chờ bạn
        </Typography>

        <Box
          display="flex"
          flexDirection={{ xs: "column", lg: "row" }}
          alignItems="center"
          gap={2}
        >
          <Box
            display="flex"
            alignItems="center"
            width={{ xs: "100%", lg: "40%" }}
            bgcolor="grey.100"
            px={2}
            py={1.5}
            borderRadius="50px"
            height="50px"
          >
            <SearchIcon sx={{ color: "#EC80B1", mr: 1 }} />
            <Autocomplete
              freeSolo
              options={hotelOptions}
              onInputChange={(event, value) => {
                setSearchParams({ ...searchParams, tenKhachSan: value });
                handleHotelInputChange(event, value);
              }}
              inputValue={searchParams.tenKhachSan}
              renderInput={(params) => (
                <TextField
                  {...params}
                  placeholder="Nhập tên khách sạn"
                  variant="standard"
                  InputProps={{ ...params.InputProps, disableUnderline: true }}
                  sx={{ bgcolor: "transparent", width: "100%" }}
                />
              )}
              sx={{ width: "100%" }}
            />
          </Box>

          <Box
            width={{ xs: "100%", lg: "20%" }}
            bgcolor="grey.100"
            px={2}
            py={1.5}
            borderRadius="50px"
            height="50px"
            display="flex"
            alignItems="center"
          >
            <LocationOnIcon sx={{ color: "#EC80B1", mr: 1 }} />
            <TextField
              select
              variant="standard"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              InputProps={{ disableUnderline: true }}
              fullWidth
              sx={{
                height: "100%",
                "& .MuiSelect-select": { py: 1.2 },
                "& .MuiMenu-paper": {
                  maxHeight: 250,
                },
              }}
              SelectProps={{
                MenuProps: {
                  PaperProps: {
                    style: {
                      maxHeight: 250,
                    },
                  },
                },
              }}
            >
              {cities.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
          </Box>

          <Box
            width={{ xs: "100%", lg: "20%" }}
            bgcolor="grey.100"
            px={2}
            py={1.5}
            borderRadius="50px"
            height="50px"
            display="flex"
            alignItems="center"
          >
            <AttachMoneyIcon sx={{ color: "#EC80B1", mr: 1 }} />
            <TextField
              select
              variant="standard"
              value={priceRangeOption}
              onChange={(e) => {
                const val = e.target.value;
                setPriceRangeOption(val);

                if (val) {
                  const [min, max] = val.split("-").map(Number);
                  setFilters((prev) => ({
                    ...prev,
                    giaRange: [min, max],
                  }));
                }
              }}
              InputProps={{ disableUnderline: true }}
              fullWidth
              sx={{ height: "100%" }}
            >
              {PRICE_OPTIONS.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
          </Box>

          <Button
            variant="contained"
            fullWidth
            sx={{
              bgcolor: "#EC80B1",
              borderRadius: "50px",
              px: 4,
              py: 1.5,
              textTransform: "none",
              width: { xs: "100%", lg: "20%" },
              "&:hover": {
                bgcolor: "#d66d9e",
              },
            }}
            onClick={handleSearch}
            disabled={loading}
          >
            {loading ? "Đang tìm..." : "Tìm kiếm"}
          </Button>
        </Box>
      </Paper>

      {/* Kết quả tìm kiếm */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={3}>
          <Paper elevation={3} sx={{ borderRadius: 5, p: 2, bgcolor: "#fff" }}>
            <Typography variant="h6" gutterBottom color="#EC80B1">
              Bộ lọc
            </Typography>

            <Box sx={{ mb: 3 }}>
              <Typography gutterBottom>Khoảng giá (VNĐ)</Typography>
              <Slider
                value={filters.giaRange}
                onChange={handleGiaRangeChange}
                valueLabelDisplay="auto"
                min={0}
                max={5000000}
                step={100000}
                valueLabelFormat={(value) =>
                  `${value.toLocaleString("vi-VN")}đ`
                }
                sx={{
                  color: "#EC80B1",
                  "& .MuiSlider-thumb": {
                    "&:hover, &.Mui-focusVisible": {
                      boxShadow: "0px 0px 0px 8px rgba(236, 128, 177, 0.16)",
                    },
                  },
                }}
              />
              <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                <Typography variant="body2" color="text.secondary">
                  {filters.giaRange[0].toLocaleString("vi-VN")}đ
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {filters.giaRange[1].toLocaleString("vi-VN")}đ
                </Typography>
              </Box>
            </Box>

            <Box sx={{ mb: 3 }}>
              <Typography gutterBottom>Tiện ích</Typography>
              <FormGroup>
                {availableFeatures.map((feature) => (
                  <FormControlLabel
                    key={feature}
                    control={
                      <Checkbox
                        checked={selectedFeatures.includes(feature)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            setSelectedFeatures([...selectedFeatures, feature]);
                          } else {
                            setSelectedFeatures(
                              selectedFeatures.filter((f) => f !== feature)
                            );
                          }
                        }}
                        sx={{
                          color: "#EC80B1",
                          "&.Mui-checked": { color: "#EC80B1" },
                        }}
                      />
                    }
                    label={feature}
                  />
                ))}
              </FormGroup>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={9}>
          <Paper elevation={3} sx={{ borderRadius: 5, p: 2, bgcolor: "#fff" }}>
            <Typography variant="h6" gutterBottom color="#EC80B1">
              Danh sách khách sạn {loading && "(Đang tải...)"}
            </Typography>
            {!loading && hotels.length === 0 && (
              <Typography align="center" color="text.secondary" py={4}>
                Không tìm thấy khách sạn nào phù hợp
              </Typography>
            )}
            {hotels.map((hotel) => (
              <LongCard
                key={hotel.hotelId}
                data={hotel}
                type="khach-san"
                idField="hotelId"
                nameField="hotelName"
                priceField="hotelPrice"
                imageField="thumbnail"
                features={hotel.features}
              />
            ))}

            {/* Pagination */}
            <Box
              sx={{ display: "flex", justifyContent: "center", mt: 4, mb: 2 }}
            >
              <Pagination
                count={totalPages}
                page={currentPage}
                onChange={handlePageChange}
                color="primary"
                shape="rounded"
                disabled={loading}
                sx={{
                  "& .MuiPaginationItem-root": {
                    color: "#666",
                    "&.Mui-selected": {
                      backgroundColor: "#EC80B1",
                      color: "white",
                      "&:hover": {
                        backgroundColor: "#d66d9e",
                      },
                    },
                    "&:hover": {
                      backgroundColor: "rgba(236, 128, 177, 0.1)",
                    },
                  },
                }}
              />
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default TimKhachSan;
