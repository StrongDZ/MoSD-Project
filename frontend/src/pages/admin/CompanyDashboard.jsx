import React, { useState, useEffect } from "react";
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Divider,
  Chip,
  Box,
  ImageList,
  ImageListItem,
} from "@mui/material";
import { axiosRequest } from "../../utils/axiosUtils";
import config from "../../config";
const CompanyDashboard = () => {
  const [companyInfo, setCompanyInfo] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [editForm, setEditForm] = useState({
    companyName: "",
    username: "",
    address: "",
    phone: "",
    email: "",
    description: "",
    hotelName: "",
    hotelPrice: "",
    city: "",
    mapLink: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    fetchCompanyInfo();
  }, []);

  const fetchCompanyInfo = async () => {
    try {
      const response = await axiosRequest({
        url: `${config.api.url}/api/company/current`,
        method: "GET",
      });
      setCompanyInfo(response.data.data);
      setEditForm({
        companyName: response.data.data.companyName,
        accommodationName:
          response.data.data.hotelName || response.data.data.shipName,
        address: response.data.data.address,
        city: response.data.data.city,
        hotelPrice: response.data.data.hotelPrice,
        mapLink: response.data.data.mapLink,
      });
    } catch (error) {
      setError("Không thể tải thông tin doanh nghiệp");
    }
  };

  const handleEdit = () => {
    setOpenDialog(true);
  };

  const handleClose = () => {
    setOpenDialog(false);
  };

  const handleSubmit = async () => {
    try {
      await axiosRequest({
        url: `${config.api.url}/api/company/update`,
        method: "PUT",
        data: editForm,
      });
      setSuccess("Cập nhật thông tin thành công");
      fetchCompanyInfo();
      handleClose();
    } catch (error) {
      setError("Không thể cập nhật thông tin");
    }
  };

  const handleChange = (e) => {
    setEditForm({
      ...editForm,
      [e.target.name]: e.target.value,
    });
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  if (!companyInfo) {
    return <Typography>Đang tải...</Typography>;
  }

  const isHotel = companyInfo.id <= 217;

  return (
    <div className="p-6">
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Card className="shadow-md rounded-2xl">
            <CardContent>
              <div className="flex justify-between items-center mb-4">
                <Typography variant="h5">
                  Thông tin {isHotel ? "Khách sạn" : "Tàu du lịch"}
                </Typography>
                <Button
                  variant="contained"
                  color="primary"
                  onClick={handleEdit}
                >
                  Chỉnh sửa
                </Button>
              </div>
              {error && (
                <Alert severity="error" className="mb-4">
                  {error}
                </Alert>
              )}
              {success && (
                <Alert severity="success" className="mb-4">
                  {success}
                </Alert>
              )}

              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Tên doanh nghiệp:
                  </Typography>
                  <Typography variant="body1" className="mb-4">
                    {companyInfo.companyName}
                  </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Tên khách sạn:
                  </Typography>
                  <Typography variant="body1" className="mb-4">
                    {companyInfo.hotelName}
                  </Typography>
                </Grid>

                <Grid item xs={12}>
                  <Divider className="my-2" />
                </Grid>

                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Địa chỉ:
                  </Typography>
                  <Typography variant="body1" className="mb-4">
                    {companyInfo.address}
                  </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Thành phố:
                  </Typography>
                  <Typography variant="body1" className="mb-4">
                    {companyInfo.city}
                  </Typography>
                </Grid>

                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Giá phòng:
                  </Typography>
                  <Typography variant="body1" className="mb-4">
                    {formatPrice(companyInfo.hotelPrice)}
                  </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Tổng số phòng:
                  </Typography>
                  <Typography variant="body1" className="mb-4">
                    {companyInfo.totalRooms}
                  </Typography>
                </Grid>

                <Grid item xs={12}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Tiện ích:
                  </Typography>
                  <Box className="flex flex-wrap gap-2 mt-2">
                    {companyInfo.features?.map((feature, index) => (
                      <Chip
                        key={index}
                        label={feature}
                        color="primary"
                        variant="outlined"
                      />
                    ))}
                  </Box>
                </Grid>

                <Grid item xs={12}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Mô tả ngắn:
                  </Typography>
                  <Box className="mt-2">
                    {companyInfo.shortDescriptions?.map((desc, index) => (
                      <Typography key={index} variant="body1" className="mb-2">
                        • {desc}
                      </Typography>
                    ))}
                  </Box>
                </Grid>

                <Grid item xs={12}>
                  <Typography variant="subtitle1" className="font-semibold">
                    Hình ảnh:
                  </Typography>
                  <ImageList
                    sx={{ width: "100%", height: 450 }}
                    cols={3}
                    rowHeight={164}
                  >
                    {companyInfo.images?.map((image, index) => (
                      <ImageListItem key={index}>
                        <img
                          src={image}
                          alt={`Hình ảnh ${index + 1}`}
                          loading="lazy"
                          className="object-cover"
                        />
                      </ImageListItem>
                    ))}
                  </ImageList>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Dialog open={openDialog} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>Chỉnh sửa thông tin doanh nghiệp</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} className="mt-2">
            <Grid item xs={12} md={6}>
              <TextField
                margin="dense"
                name="companyName"
                label="Tên doanh nghiệp"
                type="text"
                fullWidth
                value={editForm.companyName}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                margin="dense"
                name="accommodationName"
                label="Tên khách sạn"
                type="text"
                fullWidth
                value={editForm.accommodationName}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                margin="dense"
                name="address"
                label="Địa chỉ"
                type="text"
                fullWidth
                value={editForm.address}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                margin="dense"
                name="city"
                label="Thành phố"
                type="text"
                fullWidth
                value={editForm.city}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                margin="dense"
                name="hotelPrice"
                label="Giá phòng"
                type="number"
                fullWidth
                value={editForm.hotelPrice}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                margin="dense"
                name="mapLink"
                label="Link bản đồ"
                type="text"
                fullWidth
                value={editForm.mapLink}
                onChange={handleChange}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Hủy</Button>
          <Button onClick={handleSubmit} color="primary">
            Lưu
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default CompanyDashboard;
