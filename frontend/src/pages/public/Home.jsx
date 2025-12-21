import React, { useState, useEffect } from "react";
import { Container, Typography, Box, Card, CardContent, CardMedia, Button, Grid } from "@mui/material";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import backgroundImage from "../../assets/hinhnen.webp";
import KhachSanCardNho from "../../components/public/KhachSanCardNho";
import axios from "axios";
import { Link } from "react-router-dom";
import config from "../../config";
const Home = () => {
    const [hotels, setHotels] = useState([]);

    useEffect(() => {
        axios
            .get(`${config.api.url}/api/hotel?page=1&pageSize=6`)
            .then((response) => {
                const data = response.data;
                setHotels(data.result || []);
            })
            .catch((error) => console.error("Lỗi khi gọi API:", error));
    }, []);
    return (
        <Box>
            {/* Hero Section */}
            <Box
                sx={{
                    backgroundImage: `url(${backgroundImage})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                    height: "90vh",
                    display: "flex",
                    alignItems: "center",
                    position: "relative",
                    "&::before": {
                        content: '""',
                        position: "absolute",
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: "rgba(0, 0, 0, 0.4)",
                    },
                }}
            >
                <Container sx={{ position: "relative", zIndex: 1 }}>
                    <Box
                        sx={{
                            maxWidth: 600,
                            color: "white",
                            textShadow: "2px 2px 4px rgba(0,0,0,0.5)",
                        }}
                    >
                        <Typography variant="h2" gutterBottom>
                            Khám phá Việt Nam
                        </Typography>
                        <Typography variant="h5" gutterBottom>
                            Trải nghiệm những chuyến đi đáng nhớ cùng Monkey
                        </Typography>
                        <Button variant="contained" className="!bg-[#EC80B1] text-white px-6 py-2 rounded-xl hover:opacity-90 shadow-md">
                            Khám phá ngay
                        </Button>
                    </Box>
                </Container>
            </Box>

            <div className="bg-white p-8 md:flex gap-6">
                {/* Left side */}
                <div className="bg-pink-100 p-6 rounded-xl md:w-1/2 mb-6 md:mb-0">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">
                        <span className="italic font-signature text-pink-500">Doanh nghiệp & Du lịch – </span>
                        Tour Du thuyền Hạ Long: Kết nối doanh nghiệp, khám phá vẻ đẹp tự nhiên.
                    </h2>
                    <p className="text-sm text-gray-700 mb-6">
                        Với sự trải nghiệm thực tế, chúng tôi mong muốn đưa du thuyền Hạ Long trở thành một lựa chọn đầu tiên cho doanh nghiệp. Nhiều
                        chương trình du lịch hấp dẫn, đa dạng được kết hợp sẽ đem đến cho quý doanh nghiệp sự hài lòng và thuận tiện. Du thuyền Hạ
                        Long cũng sẽ là nơi mở ra giá trị và ấn tượng ý nghĩa dành cho nhân viên của quý doanh nghiệp. Bên cạnh đó, du thuyền Hạ Long
                        còn rất phù hợp cho những cuộc hội thảo, hợp tác đầu tư hay giao lưu của quý doanh nghiệp.
                    </p>
                    <button className="bg-pink-400 text-white px-5 py-2 rounded-full hover:bg-pink-500 transition">Liên hệ với chúng tôi →</button>
                </div>

                {/* Right side */}
                <div className="md:w-1/2 flex flex-col gap-4">
                    {/* Card 1 */}
                    <div className="flex items-start bg-pink-50 p-4 rounded-lg shadow-sm">
                        <img
                            src="https://picsum.photos/400/300" // Thay bằng ảnh thực tế
                            alt="Lịch trình"
                            className="w-16 h-16 object-cover rounded mr-4"
                        />
                        <div>
                            <h3 className="font-semibold text-gray-800">Lịch trình phù hợp với doanh nghiệp</h3>
                            <p className="text-sm text-gray-600">
                                Du thuyền sẽ sắp xếp lịch trình phù hợp với từng sự kiện của doanh nghiệp: du lịch của công ty...
                            </p>
                        </div>
                    </div>

                    {/* Card 2 */}
                    <div className="flex items-start bg-pink-50 p-4 rounded-lg shadow-sm">
                        <img
                            src="https://picsum.photos/400/300" // Thay bằng ảnh thực tế
                            alt="Đa dạng"
                            className="w-16 h-16 object-cover rounded mr-4"
                        />
                        <div>
                            <h3 className="font-semibold text-gray-800">Đa dạng trong sự lựa chọn du thuyền</h3>
                            <p className="text-sm text-gray-600">
                                Tùy vào nhu cầu của doanh nghiệp, chúng tôi sẽ tư vấn cung cấp du thuyền phù hợp ...
                            </p>
                        </div>
                    </div>

                    {/* Card 3 */}
                    <div className="flex items-start bg-pink-50 p-4 rounded-lg shadow-sm">
                        <img
                            src="https://picsum.photos/400/300" // Thay bằng ảnh thực tế
                            alt="Linh hoạt"
                            className="w-16 h-16 object-cover rounded mr-4"
                        />
                        <div>
                            <h3 className="font-semibold text-gray-800">Thời gian linh hoạt</h3>
                            <p className="text-sm text-gray-600">
                                Chúng tôi sẽ tư vấn thời gian linh hoạt nhất phù hợp với tính chất của sự kiện và lịch làm việc trước và sau du
                                chuyến...
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <Container className="mt-8">
                <Typography variant="h3" gutterBottom align="center">
                    Khách Sạn
                </Typography>
                <Grid container spacing={4}>
                    {hotels.map((hotel) => (
                        <Grid item xs={12} sm={6} md={4} key={hotel.hotelId}>
                            <KhachSanCardNho hotel={hotel} />
                        </Grid>
                    ))}
                </Grid>

                <div
                    style={{
                        display: "flex",
                        justifyContent: "center",
                        marginTop: "32px",
                    }}
                >
                    <Button
                        component={Link}
                        to="/tim-khach-san"
                        variant="outlined"
                        endIcon={<ArrowForwardIcon />}
                        style={{
                            borderRadius: "999px",
                            textTransform: "none",
                            padding: "10px 24px",
                            fontWeight: "500",
                            fontSize: "16px",
                            color: "#EC80B1",
                            borderColor: "#EC80B1",
                        }}
                    >
                        Xem tất cả Khách Sạn
                    </Button>
                </div>
            </Container>
            {/* Popular Tours Section */}
            {/* <Box sx={{ py: 8 }}>
        <Container>
          <Typography variant="h3" gutterBottom align="center">
            Tour Nổi Bật
          </Typography>
          <Grid container spacing={4}>
            {[1, 2, 3].map((item) => (
              <Grid item xs={12} md={4} key={item}>
                <Card>
                  <CardMedia
                    component="img"
                    height="200"
                    image={`/images/tour-${item}.jpg`}
                    alt={`Tour ${item}`}
                  />
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      Tour Du lịch Hạ Long
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Khám phá vẻ đẹp kỳ vĩ của Vịnh Hạ Long - Di sản thiên
                      nhiên thế giới
                    </Typography>
                    <Box
                      sx={{
                        mt: 2,
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                      }}
                    >
                      <Typography variant="h6" color="primary">
                        2,990,000đ
                      </Typography>
                      <Button variant="contained" color="primary">
                        Xem chi tiết
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box> */}
            {/* Popular Destinations */}
            {/* <Box sx={{ py: 8, bgcolor: "grey.100" }}>
        <Container>
          <Typography variant="h3" gutterBottom align="center">
            Điểm Đến Phổ Biến
          </Typography>
          <Grid container spacing={3}>
            {[1, 2, 3, 4, 5, 6].map((item) => (
              <Grid item xs={12} sm={6} md={4} key={item}>
                <Card>
                  <CardMedia
                    component="img"
                    height="200"
                    image={`/images/destination-${item}.jpg`}
                    alt={`Destination ${item}`}
                  />
                  <CardContent>
                    <Typography variant="h6">Hà Nội</Typography>
                    <Typography variant="body2" color="text.secondary">
                      36 tour
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box> */}
            {/* Blog Section */}
            {/* <Box sx={{ py: 8 }}>
        <Container>
          <Typography variant="h3" gutterBottom align="center">
            Tin Tức & Blog Du Lịch
          </Typography>
          <Grid container spacing={4}>
            {[1, 2, 3].map((item) => (
              <Grid item xs={12} md={4} key={item}>
                <Card>
                  <CardMedia
                    component="img"
                    height="200"
                    image={`/images/blog-${item}.jpg`}
                    alt={`Blog ${item}`}
                  />
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      Top 10 địa điểm du lịch không thể bỏ qua
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Khám phá những địa điểm du lịch tuyệt vời nhất Việt Nam
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box> */}
        </Box>
    );
};

export default Home;
