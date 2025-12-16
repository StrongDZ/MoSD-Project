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

  useEffect(() => {
    fetchHotels(currentPage);
  }, [currentPage, searchParams, filters, selectedFeatures]);

  const handleSearch = () => {
    console.log("Tìm kiếm với params:", searchParams);
    setCurrentPage(1);
    fetchHotels(1);
  };

  useEffect(() => {
    setCurrentPage(1);
  }, [filters, searchParams]);

  const handleGiaRangeChange = (event, newValue) => {
    setFilters({ ...filters, giaRange: newValue });
  };

  const handlePageChange = (event, value) => {
    console.log("Chuyển sang trang:", value);
    setCurrentPage(value);
  };

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
    <Container maxWidth="lg" sx={{ py: 4, position: 'relative' }}>
      {/* Decorative Elements Loạn */}
      <div className="absolute top-0 left-0 w-32 h-32 bg-gradient-to-br from-pink-500 to-yellow-400 rounded-full blur-3xl opacity-30 animate-bounce"></div>
      <div className="absolute top-20 right-10 w-48 h-48 bg-gradient-to-tr from-cyan-400 to-purple-600 rounded-full blur-2xl opacity-20 animate-pulse"></div>
      <div className="absolute bottom-40 left-1/4 w-64 h-64 bg-gradient-to-bl from-green-400 to-blue-500 rounded-full blur-3xl opacity-25 animate-spin" style={{animationDuration: '20s'}}></div>
      <div className="absolute inset-0 bg-[url('/background-pattern.svg')] bg-cover opacity-5 -z-10"></div>
      
      {/* Floating Random Shapes */}
      <div className="absolute top-1/4 right-1/3 w-12 h-12 border-4 border-orange-500 rotate-45 animate-ping"></div>
      <div className="absolute bottom-1/3 left-1/4 w-16 h-16 bg-red-400 rounded-tr-full opacity-40 animate-bounce" style={{animationDelay: '1s'}}></div>
      <div className="absolute top-1/2 right-1/4 text-6xl opacity-10 animate-spin" style={{animationDuration: '15s'}}>🌟</div>
      <div className="absolute bottom-1/4 left-1/2 text-5xl opacity-20 animate-bounce" style={{animationDelay: '2s'}}>🎨</div>

      {/* Search Bar */}
      <Paper
        elevation={10}
        sx={{ 
          px: 4, 
          py: 5, 
          borderRadius: '30px', 
          mb: 4, 
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 25%, #f093fb 50%, #4facfe 75%, #00f2fe 100%)',
          transform: 'rotate(-1deg)',
          border: '8px solid #FFD700',
          boxShadow: '0 20px 60px rgba(236, 128, 177, 0.5)',
        }}
      >
        <Typography
          variant="h2"
          fontWeight="900"
          align="center"
          gutterBottom
          sx={{ 
            color: '#FFFF00',
            textShadow: '4px 4px 0px #FF00FF, 8px 8px 0px #00FFFF',
            transform: 'skew(-5deg) rotate(2deg)',
            letterSpacing: '0.2em',
            fontFamily: 'Comic Sans MS, cursive',
            textDecoration: 'underline wavy #00FF00',
            animation: 'pulse 2s infinite'
          }}
        >
          🎉 Bạn lựa chọn khách sạn nào? 🏨
        </Typography>
        <Typography 
          align="center" 
          mb={4}
          sx={{
            color: '#FFFFFF',
            fontSize: '1.8rem',
            fontWeight: 'bold',
            textShadow: '2px 2px 4px rgba(0,0,0,0.5)',
            background: 'linear-gradient(90deg, #FF6B6B, #4ECDC4, #FFE66D)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            fontStyle: 'italic',
          }}
        >
          ⭐⭐⭐ Hơn 100 khách sạn hạng sang giá tốt đang chờ bạn ⭐⭐⭐
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
            sx={{
              background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
              border: '5px dashed #00FF00',
              boxShadow: '0 8px 20px rgba(255, 105, 180, 0.8)',
              transform: 'skew(-2deg)',
            }}
            px={2}
            py={1.5}
            borderRadius="25px"
            height="60px"
          >
            <SearchIcon sx={{ color: "#FFFF00", mr: 1, fontSize: '2.5rem', animation: 'spin 3s linear infinite' }} />
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
            sx={{
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              border: '4px solid #FFD700',
              boxShadow: '0 0 20px rgba(102, 126, 234, 0.8)',
              transform: 'rotate(1deg)',
            }}
            px={2}
            py={1.5}
            borderRadius="20px"
            height="60px"
            display="flex"
            alignItems="center"
          >
            <LocationOnIcon sx={{ color: "#00FFFF", mr: 1, fontSize: '2rem' }} />
            <TextField
              select
              variant="standard"
              value={location}
              onChange={(e) => {
                setLocation(e.target.value);
                setSearchParams({ ...searchParams, diaDiem: e.target.value });
              }}
              InputProps={{ disableUnderline: true }}
              fullWidth
              sx={{
                height: "100%",
                "& .MuiSelect-select": { py: 1.2 },
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
            sx={{
              background: 'linear-gradient(90deg, #FA8BFF 0%, #2BD2FF 52%, #2BFF88 90%)',
              border: '5px dotted #FF00FF',
              boxShadow: '0 0 25px rgba(250, 139, 255, 0.9)',
              transform: 'skew(2deg)',
            }}
            px={2}
            py={1.5}
            borderRadius="30px"
            height="60px"
            display="flex"
            alignItems="center"
          >
            <AttachMoneyIcon sx={{ color: "#FFD700", mr: 1, fontSize: '2.2rem' }} />
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
              background: 'linear-gradient(45deg, #FF0080 0%, #FF8C00 50%, #40E0D0 100%)',
              borderRadius: "40px",
              px: 4,
              py: 2,
              textTransform: "uppercase",
              width: { xs: "100%", lg: "20%" },
              fontWeight: '900',
              fontSize: '1.3rem',
              border: '6px solid #FFFF00',
              boxShadow: '0 10px 30px rgba(255, 0, 128, 0.7)',
              transform: 'rotate(-2deg) scale(1.05)',
              letterSpacing: '0.1em',
              "&:hover": {
                background: 'linear-gradient(45deg, #40E0D0 0%, #FF8C00 50%, #FF0080 100%)',
                transform: 'rotate(2deg) scale(1.15)',
                boxShadow: '0 15px 40px rgba(255, 140, 0, 0.9)',
              },
            }}
            onClick={handleSearch}
            disabled={loading}
          >
            {loading ? "🔍 Đang tìm..." : "🚀 Tìm kiếm 🔥"}
          </Button>
        </Box>
      </Paper>

      {/* Kết quả tìm kiếm */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={3}>
          {/* Decorative Stars around Filter */}
          <div className="absolute -top-4 -left-4 text-4xl animate-spin" style={{animationDuration: '8s'}}>⭐</div>
          <div className="absolute -bottom-4 -right-4 text-3xl animate-bounce" style={{animationDelay: '1s'}}>💎</div>
          <Paper 
            elevation={10} 
            sx={{ 
              borderRadius: '40px', 
              p: 3, 
              background: 'linear-gradient(180deg, #ffecd2 0%, #fcb69f 50%, #ff6b6b 100%)',
              border: '8px ridge #FF1493',
              boxShadow: '0 0 40px rgba(255, 107, 107, 0.7)',
              transform: 'rotate(2deg)',
              position: 'relative',
            }}
          >
            <Typography 
              variant="h4" 
              gutterBottom 
              sx={{
                color: '#4B0082',
                fontWeight: '900',
                textShadow: '3px 3px 0px #FFD700',
                transform: 'skew(-5deg)',
                textDecoration: 'underline wavy #00FF00',
                fontSize: '2.5rem',
                textAlign: 'center',
              }}
            >
              🎯 Bộ lọc 🎯
            </Typography>

            <Box sx={{ mb: 3, bgcolor: 'rgba(255, 255, 255, 0.5)', p: 2, borderRadius: '20px', border: '3px dashed #FF00FF' }}>
              <Typography 
                gutterBottom
                sx={{
                  fontSize: '1.5rem',
                  fontWeight: 'bold',
                  color: '#0000FF',
                  textShadow: '2px 2px #FFFF00',
                }}
              >
                💰 Khoảng giá (VNĐ) 💰
              </Typography>
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
                  color: "#FF1493",
                  height: 12,
                  "& .MuiSlider-thumb": {
                    width: 32,
                    height: 32,
                    background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
                    border: '4px solid #FFD700',
                    "&:hover, &.Mui-focusVisible": {
                      boxShadow: "0px 0px 0px 12px rgba(255, 20, 147, 0.3)",
                    },
                  },
                  "& .MuiSlider-track": {
                    height: 12,
                    background: 'linear-gradient(90deg, #00FF00, #FFFF00, #FF00FF)',
                  },
                  "& .MuiSlider-rail": {
                    height: 12,
                    background: '#DDD',
                  },
                }}
              />
              <Box sx={{ display: "flex", justifyContent: "space-between", mt: 2 }}>
                <Typography 
                  variant="h6" 
                  sx={{ 
                    color: '#FF0000', 
                    fontWeight: 'bold',
                    bgcolor: '#FFFF00',
                    px: 2,
                    py: 1,
                    borderRadius: '15px',
                    border: '3px solid #00FF00',
                  }}
                >
                  {filters.giaRange[0].toLocaleString("vi-VN")}đ
                </Typography>
                <Typography 
                  variant="h6" 
                  sx={{ 
                    color: '#0000FF', 
                    fontWeight: 'bold',
                    bgcolor: '#FF00FF',
                    color: '#FFFFFF',
                    px: 2,
                    py: 1,
                    borderRadius: '15px',
                    border: '3px solid #00FFFF',
                  }}
                >
                  {filters.giaRange[1].toLocaleString("vi-VN")}đ
                </Typography>
              </Box>
            </Box>

            <Box sx={{ mb: 3, bgcolor: 'rgba(255, 255, 0, 0.3)', p: 2, borderRadius: '25px', border: '4px double #00FF00' }}>
              <Typography 
                gutterBottom
                sx={{
                  fontSize: '1.8rem',
                  fontWeight: '900',
                  color: '#FF1493',
                  textShadow: '2px 2px #00FFFF',
                  textAlign: 'center',
                  textDecoration: 'underline wavy #FF6600',
                }}
              >
                ✨ Tiện ích ✨
              </Typography>
              <FormGroup>
                {availableFeatures.map((feature, idx) => (
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
                          color: ["#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF"][idx % 5],
                          transform: `rotate(${idx * 5}deg)`,
                          "&.Mui-checked": { 
                            color: ["#FF1493", "#00CED1", "#FFD700", "#FF4500", "#9370DB"][idx % 5],
                            transform: `scale(1.5) rotate(${idx * 10}deg)`,
                          },
                        }}
                      />
                    }
                    label={feature}
                    sx={{
                      bgcolor: ['rgba(255,0,0,0.1)', 'rgba(0,255,0,0.1)', 'rgba(0,0,255,0.1)', 'rgba(255,255,0,0.1)', 'rgba(255,0,255,0.1)'][idx % 5],
                      my: 1,
                      p: 1,
                      borderRadius: '15px',
                      border: `2px solid ${['#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF'][idx % 5]}`,
                      '& .MuiTypography-root': {
                        fontWeight: 'bold',
                        fontSize: '1.1rem',
                      }
                    }}
                  />
                ))}
              </FormGroup>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={9}>
          {/* Floating decorations around results */}
          <div className="absolute top-0 right-0 text-5xl animate-bounce">🏨</div>
          <div className="absolute bottom-0 left-0 text-4xl animate-pulse">🌴</div>
          <Paper 
            elevation={10} 
            sx={{ 
              borderRadius: '35px', 
              p: 3, 
              background: 'linear-gradient(135deg, #fdfcfb 0%, #e2d1c3 50%, #f8edeb 100%)',
              border: '10px groove #FF6B6B',
              boxShadow: '0 0 50px rgba(255, 107, 107, 0.6)',
              transform: 'rotate(-1deg)',
              position: 'relative',
            }}
          >
            <Typography 
              variant="h3" 
              gutterBottom 
              sx={{
                color: '#FF1493',
                fontWeight: '900',
                textShadow: '4px 4px 0px #00FFFF, 8px 8px 0px #FFD700',
                textAlign: 'center',
                textDecoration: 'underline double #00FF00',
                transform: 'skew(-3deg)',
                mb: 3,
              }}
            >
              🏆 Danh sách khách sạn {loading && "(⏳ Đang tải...) 🔄"}
            </Typography>
            {!loading && hotels.length === 0 && (
              <Box
                sx={{
                  textAlign: 'center',
                  py: 6,
                  background: 'linear-gradient(45deg, #FFF3E0 30%, #FFE0B2 90%)',
                  borderRadius: '30px',
                  border: '6px dashed #FF6B6B',
                  my: 2,
                }}
              >
                <Typography 
                  sx={{
                    fontSize: '3rem',
                    fontWeight: 'bold',
                    color: '#D32F2F',
                    textShadow: '2px 2px #FFD54F',
                  }}
                >
                  😢 Không tìm thấy khách sạn nào phù hợp 😢
                </Typography>
                <Typography sx={{ fontSize: '1.5rem', color: '#F57C00', mt: 2, fontWeight: 'bold' }}>
                  Hãy thử tìm kiếm với điều kiện khác! 🔍
                </Typography>
              </Box>
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
              sx={{ 
                display: "flex", 
                justifyContent: "center", 
                mt: 4, 
                mb: 2,
                background: 'linear-gradient(90deg, #FA8BFF 0%, #2BD2FF 52%, #2BFF88 90%)',
                p: 3,
                borderRadius: '40px',
                border: '8px ridge #FF1493',
                boxShadow: '0 0 30px rgba(250, 139, 255, 0.8)',
              }}
            >
              <Pagination
                count={totalPages}
                page={currentPage}
                onChange={handlePageChange}
                color="primary"
                shape="rounded"
                disabled={loading}
                size="large"
                sx={{
                  "& .MuiPaginationItem-root": {
                    color: "#4B0082",
                    fontSize: '1.5rem',
                    fontWeight: 'bold',
                    border: '3px solid #FFD700',
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    color: '#FFFFFF',
                    mx: 0.5,
                    "&.Mui-selected": {
                      background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
                      color: "#FFFF00",
                      border: '4px solid #00FF00',
                      transform: 'scale(1.3) rotate(5deg)',
                      boxShadow: '0 8px 20px rgba(255, 105, 180, 0.8)',
                      "&:hover": {
                        background: 'linear-gradient(45deg, #FF8E53 30%, #FE6B8B 90%)',
                        transform: 'scale(1.4) rotate(-5deg)',
                      },
                    },
                    "&:hover": {
                      background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                      transform: 'scale(1.2)',
                      boxShadow: '0 5px 15px rgba(245, 87, 108, 0.6)',
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
