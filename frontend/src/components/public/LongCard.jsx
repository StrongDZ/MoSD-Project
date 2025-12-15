import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  Box,
  Rating,
  Chip,
  Stack,
  Button,
} from "@mui/material";
import { LocationOn, Pool, Wifi, Restaurant } from "@mui/icons-material";

const LongCard = ({
  data,
  idField = "hotelId",
  nameField = "hotelName",
  priceField = "hotelPrice",
  type = "khach-san", // hoặc "du-thuyen"
  imageField = "hinhAnh",
  features = [], // Features từ API
  maxVisibleFeatures = 2, // Số lượng features tối đa hiển thị
}) => {
  const navigate = useNavigate();
  const id = data[idField];
  const name = data[nameField];
  const price = data[priceField];
  const image = data[imageField] || "https://picsum.photos/400/300";

  const handleClick = () => {
    navigate(`/${type}/${id}`);
  };

  const ratingMock = 4.2;
  const soReviewMock = 320;

  // Tính toán số lượng features còn lại
  const safeFeatures = features || []; // Add this line
  const remainingFeatures = Math.max(
    0,
    safeFeatures.length - maxVisibleFeatures
  );
  const visibleFeatures = safeFeatures.slice(0, maxVisibleFeatures);

  // Hàm lấy icon tương ứng với feature
  const getFeatureIcon = (featureName) => {
    const featureIcons = {
      "Hồ bơi": <Pool fontSize="small" />,
      "Wifi miễn phí": <Wifi fontSize="small" />,
      "Nhà hàng": <Restaurant fontSize="small" />,
      // Thêm các icon khác tương ứng với features từ API
    };
    return featureIcons[featureName] || null;
  };

  return (
    <Card
      sx={{
        display: "flex",
        mb: 2,
        position: "relative",
        cursor: "pointer",
        "&:hover": {
          boxShadow: 6,
        },
        alignItems: "center",
        padding: "10px",
        borderRadius: "10px",
        border: "2px solid #e0e0e0",
      }}
      onClick={handleClick}
    >
      <CardMedia
        component="img"
        sx={{ width: 280, height: 210, borderRadius: "10px" }}
        image={image}
        alt={name}
      />
      <Box sx={{ display: "flex", flexDirection: "column", flex: 1 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {name}
          </Typography>

          <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
            <Rating value={ratingMock} readOnly precision={0.5} size="small" />
            <Typography variant="body2" sx={{ ml: 1 }} color="text.secondary">
              ({soReviewMock} đánh giá)
            </Typography>
          </Box>

          <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
            <LocationOn fontSize="small" color="action" />
            <Typography variant="body2" sx={{ ml: 0.5 }} color="text.secondary">
              {data.address}
            </Typography>
          </Box>

          <Stack direction="row" spacing={1} sx={{ mb: 2 }}>
            {visibleFeatures.map((feature, index) => (
              <Chip
                key={index}
                size="small"
                icon={getFeatureIcon(feature)}
                label={feature}
                variant="outlined"
              />
            ))}
            {remainingFeatures > 0 && (
              <Chip
                size="small"
                label={`+${remainingFeatures}`}
                variant="outlined"
                sx={{ backgroundColor: "#f5f5f5" }}
              />
            )}
          </Stack>
        </CardContent>

        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            p: 2,
            mt: "auto",
          }}
        >
          <Box>
            <Typography variant="h6" color="primary">
              {price.toLocaleString("vi-VN")}đ
            </Typography>
            <Typography variant="caption" color="text.secondary">
              /đêm
            </Typography>
          </Box>
          <Button
            variant="contained"
            onClick={(e) => {
              e.stopPropagation();
              navigate(`/${type}/${id}`);
            }}
            sx={{
              backgroundColor: "#EC80B1",
              "&:hover": {
                backgroundColor: "#d46f9d",
              },
              color: "#fff",
              borderRadius: "20px",
              textTransform: "none",
              px: 3,
              py: 1,
            }}
          >
            Đặt ngay
          </Button>
        </Box>
      </Box>
    </Card>
  );
};

export default LongCard;

