import React from "react";
import { Container, Typography, Grid, Card, CardContent, Box } from "@mui/material";
import BusinessIcon from "@mui/icons-material/Business";
import EventAvailableIcon from "@mui/icons-material/EventAvailable";
import DirectionsBoatIcon from "@mui/icons-material/DirectionsBoat";
import AccessTimeIcon from "@mui/icons-material/AccessTime";

const DoanhNghiep = () => {
    const features = [
        {
            icon: <EventAvailableIcon sx={{ fontSize: 40 }} />,
            title: "Lịch trình phù hợp với yêu cầu của doanh nghiệp",
            description:
                "Du thuyền sẽ sắp xếp lịch trình phù hợp với từng sự kiện của doanh nghiệp: du lịch của công ty tri ân nhân viên, hội thảo hay làm việc với đối tác",
        },
        {
            icon: <DirectionsBoatIcon sx={{ fontSize: 40 }} />,
            title: "Đa dạng trong sự lựa chọn các du thuyền",
            description:
                "Tùy vào nhu cầu của doanh nghiệp, chúng tôi sẽ tư vấn cung cấp du thuyền phù hợp về: số lượng phòng nghỉ, boong tàu rộng rãi hay chi phí hợp lý.",
        },
        {
            icon: <AccessTimeIcon sx={{ fontSize: 40 }} />,
            title: "Thời gian linh hoạt",
            description:
                "Chúng tôi sẽ tư vấn thời gian linh hoạt nhất phù hợp với tính chất của sự kiện và lịch làm việc trước và sau chuyến đi của quý doanh nghiệp.",
        },
    ];

    return (
        <Container maxWidth="lg" sx={{ py: 8 }}>
            {/* Hero Section */}
            <Box sx={{ mb: 8, textAlign: "center" }}>
                <Typography variant="h3" component="h1" gutterBottom>
                    🐒D Luffy - Tour Du thuyền Hạ Long cho Doanh Nghiệp
                </Typography>
                <Typography variant="h6" color="text.secondary" paragraph>
                    Kết nối doanh nghiệp, khám phá vẻ đẹp tự nhiên
                </Typography>
            </Box>

            {/* Main Content */}
            <Box sx={{ mb: 6 }}>
                <Typography variant="body1" paragraph>
                    Với sự trải nghiệm thực tế,
                    <b> Công ty TNHH Du lịch và Dịch vụ 🐒D Luffy </b>
                    mong muốn đưa du thuyền Hạ Long trở thành một lựa chọn đầu tiên cho doanh nghiệp. Nhiều chương trình du lịch hấp dẫn, đa dạng được
                    kết hợp sẽ đem đến cho quý doanh nghiệp sự hài lòng và thuận tiện.
                </Typography>
            </Box>

            {/* Features Grid */}
            <Grid container spacing={4}>
                {features.map((feature, index) => (
                    <Grid item xs={12} md={4} key={index}>
                        <Card sx={{ height: "100%", display: "flex", flexDirection: "column" }}>
                            <CardContent sx={{ flexGrow: 1, textAlign: "center" }}>
                                <Box sx={{ mb: 2, color: "primary.main" }}>{feature.icon}</Box>
                                <Typography gutterBottom variant="h5" component="h2">
                                    {feature.title}
                                </Typography>
                                <Typography>{feature.description}</Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            {/* Contact Information */}
            <Box sx={{ mt: 8, textAlign: "center" }}>
                <Typography variant="h5" gutterBottom>
                    Liên hệ với chúng tôi
                </Typography>
                <Typography variant="body1">Hotline: 0922222016</Typography>
                <Typography variant="body1">Email: info@mixivivu.com</Typography>
                <Typography variant="body1">Địa chỉ: Tầng 7, số nhà 25, ngõ 38 phố Yên Lãng, phường Láng Hạ, quận Đống Đa, TP. Hà Nội</Typography>
            </Box>
        </Container>
    );
};

export default DoanhNghiep;
