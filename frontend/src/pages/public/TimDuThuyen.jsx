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
  Autocomplete,
  MenuItem,
} from "@mui/material";
import LongCard from "../../components/public/LongCard";
import SearchIcon from "@mui/icons-material/Search";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import { axiosRequest } from "../../utils/axiosUtils";
import { Pool, Wifi, Restaurant, Spa } from "@mui/icons-material";

const TimDuThuyen = () => {
  const [ships, setShips] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [name, setName] = useState("");
  const [trip, setTrip] = useState("");
  const [priceRangeOption, setPriceRangeOption] = useState(""); // dùng preset khoảng giá
  const [shipOptions, setShipOptions] = useState([]);
  // const [availableFeatures, setAvailableFeatures] = useState([]);

  const DIA_DIEM_OPTIONS = [
    { value: "", label: "Tất cả địa điểm" },
    { value: "Vịnh Hạ Long", label: "Vịnh Hạ Long" },
    { value: "Vịnh Lan Hạ", label: "Vịnh Lan Hạ" },
    { value: "Vịnh Bái Tử Long", label: "Vịnh Bái Tử Long" },
  ];
  const PRICE_OPTIONS = [
    { label: "Tất cả mức giá", value: "" },
    { label: "Từ 1 đến 3 triệu", value: "1000000-3000000" },
    { label: "Từ 3 đến 6 triệu", value: "3000000-6000000" },
    { label: "Trên 6 triệu", value: "6000000-999999999" },
  ];

  const availableFeatures = [
    "Trà/cà phê trong tất cả các phòng",
    "Giáp biển",
    "Nhà hàng",
    "Lễ tân 24h",
    "Miễn phí kayaking",
    "Nhìn ra biển",
    "Phòng gia đình",
    "Máy sấy tóc",
    "Phòng có bồn tắm",
  ];

  const [searchParams, setSearchParams] = useState({
    tenDuThuyen: "",
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

  // // Fetch available features when component mounts
  // useEffect(() => {
  //   const fetchFeatures = async () => {
  //     try {
  //       const response = await axiosRequest({
  //         url: `${config.api.url}/api/ship/features`,
  //         method: "GET",
  //       });
  //       setAvailableFeatures(response.data.data || []);
  //     } catch (error) {
  //       console.error("Lỗi khi lấy danh sách features:", error);
  //     }
  //   };
  //   fetchFeatures();
  // }, []);

  const fetchShips = async (page) => {
    try {
      setLoading(true);
      console.log("Đang tải dữ liệu cho trang:", page);
      const response = await axiosRequest({
        url: `${config.api.url}/api/ship/search`,
        method: "GET",
        params: {
          name: searchParams.tenDuThuyen,
          minPrice: filters.giaRange[0],
          maxPrice: filters.giaRange[1],
          trip: trip,
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
      setShips(data.result || []);
      setTotalPages(data.meta?.pages || 1);
    } catch (error) {
      console.error("Lỗi khi gọi API:", error);
    } finally {
      setLoading(false);
    }
  };

  // Thêm useEffect để gọi lại search khi currentPage thay đổi
  useEffect(() => {
    fetchShips(currentPage);
  }, [currentPage, searchParams, filters, selectedFeatures, trip]);

  const handleSearch = () => {
    console.log("Tìm kiếm với params:", searchParams);
    setCurrentPage(1); // Reset về trang 1 khi tìm kiếm
    fetchShips(1);
  };

  // Thêm useEffect để reset về trang 1 khi thay đổi bộ lọc
  useEffect(() => {
    setCurrentPage(1);
  }, [searchParams, filters, selectedFeatures, trip]);

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

  const handleShipInputChange = async (event, value) => {
    if (!value) {
      setShipOptions([]);
      setSearchParams({ ...searchParams, tenDuThuyen: "" });
      return;
    }
    try {
      const res = await axiosRequest({
        url: `${config.api.url}/api/ship/suggest?q=${value}`,
        method: "GET",
      });
      setShipOptions(res.data.data || []);
    } catch (err) {
      setShipOptions([]);
    }
    setSearchParams({ ...searchParams, tenDuThuyen: value });
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <div className="absolute inset-0 bg-[url('/background-pattern.svg')] bg-cover opacity-5 -z-10"></div>

      {/* Search Bar */}
      <Paper elevation={3} sx={{ px: 4, py: 5, borderRadius: 5, mb: 4 }}>
        <Typography
          variant="h4"
          fontWeight="bold"
          align="center"
          gutterBottom
          color="#EC80B1"
        >
          Bạn lựa chọn du thuyền nào?
        </Typography>
        <Typography align="center" color="text.secondary" mb={4}>
          Hơn 100 du thuyền hạng sang giá tốt đang chờ bạn
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
              options={shipOptions}
              onInputChange={(event, value) => {
                setSearchParams({ ...searchParams, tenDuThuyen: value });
                handleShipInputChange(event, value);
              }}
              inputValue={searchParams.tenDuThuyen}
              renderInput={(params) => (
                <TextField
                  {...params}
                  placeholder="Nhập tên du thuyền"
                  variant="standard"
                  InputProps={{ ...params.InputProps, disableUnderline: true }}
                  sx={{ bgcolor: "transparent", width: "100%" }}
                />
              )}
              sx={{ width: "100%" }}
            />
          </Box>

          {/* Chọn địa điểm */}
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
              value={trip}
              onChange={(e) => setTrip(e.target.value)}
              InputProps={{ disableUnderline: true }}
              fullWidth
              sx={{ height: "100%" }}
            >
              {DIA_DIEM_OPTIONS.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
          </Box>

          {/* Chọn mức giá */}
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
            color="primary"
            fullWidth
            sx={{
              bgcolor: "#EC80B1",
              borderRadius: "50px",
              px: 4,
              py: 1.5,
              textTransform: "none",
              width: { xs: "100%", lg: "20%" },
              height: "56px",
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
          <Paper elevation={3} sx={{ borderRadius: 5, p: 3 }}>
            <Typography
              variant="h6"
              gutterBottom
              fontWeight="bold"
              color="#EC80B1"
            >
              Bộ lọc
            </Typography>

            {/* Bộ lọc giá */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="medium">
                Khoảng giá (VNĐ)
              </Typography>
              <Slider
                value={filters.giaRange}
                onChange={handleGiaRangeChange}
                valueLabelDisplay="auto"
                min={0}
                max={10000000}
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
              <Box
                sx={{ display: "flex", justifyContent: "space-between", mt: 1 }}
              >
                <Typography variant="body2" color="text.secondary">
                  {filters.giaRange[0].toLocaleString("vi-VN")}đ
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {filters.giaRange[1].toLocaleString("vi-VN")}đ
                </Typography>
              </Box>
            </Box>

            {/* Bộ lọc đánh giá */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="medium">
                Đánh giá
              </Typography>
              <FormGroup>
                {[5, 4, 3, 2, 1].map((rating) => (
                  <FormControlLabel
                    key={rating}
                    control={
                      <Checkbox
                        checked={filters.rating === rating}
                        onChange={() =>
                          setFilters((prev) => ({
                            ...prev,
                            rating: prev.rating === rating ? 0 : rating,
                          }))
                        }
                        sx={{
                          color: "#EC80B1",
                          "&.Mui-checked": {
                            color: "#EC80B1",
                          },
                        }}
                      />
                    }
                    label={
                      <Box sx={{ display: "flex", alignItems: "center" }}>
                        {[...Array(rating)].map((_, i) => (
                          <span key={i} style={{ color: "#FFD700" }}>
                            ★
                          </span>
                        ))}
                        {[...Array(5 - rating)].map((_, i) => (
                          <span key={i} style={{ color: "#E0E0E0" }}>
                            ★
                          </span>
                        ))}
                        <Typography
                          variant="body2"
                          sx={{ ml: 1, color: "text.secondary" }}
                        >
                          {rating === 5
                            ? "Tuyệt vời"
                            : rating === 4
                            ? "Rất tốt"
                            : rating === 3
                            ? "Tốt"
                            : rating === 2
                            ? "Trung bình"
                            : "Kém"}
                        </Typography>
                      </Box>
                    }
                  />
                ))}
              </FormGroup>
            </Box>

            {/* Bộ lọc tiện ích */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="medium">
                Tiện ích
              </Typography>
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
                          "&.Mui-checked": {
                            color: "#EC80B1",
                          },
                        }}
                      />
                    }
                    label={feature}
                  />
                ))}
              </FormGroup>
            </Box>

            {/* Nút áp dụng bộ lọc */}
            <Button
              variant="contained"
              fullWidth
              onClick={handleSearch}
              sx={{
                bgcolor: "#EC80B1",
                "&:hover": {
                  bgcolor: "#d66d9e",
                },
                borderRadius: "25px",
                py: 1.5,
                textTransform: "none",
                fontWeight: "bold",
              }}
            >
              Áp dụng bộ lọc
            </Button>
          </Paper>
        </Grid>

        <Grid item xs={12} md={9} id="danh-sach-du-thuyen">
          <Paper elevation={3} sx={{ borderRadius: 5, p: 2 }}>
            <Typography variant="h6" gutterBottom>
              Danh sách du thuyền {loading && "(Đang tải...)"}
            </Typography>
            {!loading && ships.length === 0 && (
              <Typography align="center" color="text.secondary" py={4}>
                Không tìm thấy du thuyền nào phù hợp
              </Typography>
            )}
            {ships.map((ship) => (
              <LongCard
                key={ship.shipId}
                data={ship}
                type="du-thuyen"
                idField="shipId"
                nameField="shipName"
                priceField="shipPrice"
                imageField="thumbnail"
                features={ship.features}
              />
            ))}

            {/* Phân trang */}
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

export default TimDuThuyen;
