import React from "react";
import { Box, Typography, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/admin/Header";

const mockRows = [
    {
        id: 1,
        bookingId: "BK001",
        customerName: "Nguyễn Văn A",
        phone: "0900000001",
        email: "a@example.com",
        startDate: "2025-01-01",
        endDate: "2025-01-03",
        state: "CONFIRMED",
    },
    {
        id: 2,
        bookingId: "BK002",
        customerName: "Trần Thị B",
        phone: "0900000002",
        email: "b@example.com",
        startDate: "2025-02-10",
        endDate: "2025-02-12",
        state: "PENDING",
    },
    {
        id: 3,
        bookingId: "BK003",
        customerName: "Lê Văn C",
        phone: "0900000003",
        email: "c@example.com",
        startDate: "2025-03-05",
        endDate: "2025-03-07",
        state: "CANCELLED",
    },
];

const ManageBooking = () => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);

    const columns = [
        { field: "bookingId", headerName: "Mã đặt chỗ", flex: 0.7 },
        { field: "customerName", headerName: "Tên khách hàng", flex: 1.2 },
        { field: "phone", headerName: "Số điện thoại", flex: 1 },
        { field: "email", headerName: "Email", flex: 1.5 },
        { field: "startDate", headerName: "Ngày nhận phòng", flex: 1 },
        { field: "endDate", headerName: "Ngày trả phòng", flex: 1 },
        {
            field: "state",
            headerName: "Trạng thái",
            flex: 1,
            renderCell: ({ row }) => {
                let label = "Đang chờ";
                let bg = colors.blueAccent[700];

                if (row.state === "CONFIRMED") {
                    label = "Đã xác nhận";
                    bg = colors.greenAccent[600];
                } else if (row.state === "CANCELLED") {
                    label = "Đã hủy";
                    bg = colors.redAccent[700];
                }

                return (
                    <Box px={1} py={0.5} borderRadius={1} display="flex" justifyContent="center" alignItems="center" sx={{ backgroundColor: bg }}>
                        <Typography variant="body2" color={colors.grey[100]}>
                            {label}
                        </Typography>
                    </Box>
                );
            },
        },
    ];

    return (
        <Box m="20px">
            <Header title="QUẢN LÝ ĐẶT CHỖ (ĐƠN GIẢN)" subtitle="Danh sách demo đặt phòng, không gọi API" />
            <Box
                m="40px 0 0 0"
                height="70vh"
                sx={{
                    "& .MuiDataGrid-root": {
                        border: "none",
                    },
                    "& .MuiDataGrid-cell": {
                        borderBottom: "none",
                    },
                    "& .MuiDataGrid-columnHeaders": {
                        backgroundColor: colors.blueAccent[700],
                        borderBottom: "none",
                    },
                    "& .MuiDataGrid-virtualScroller": {
                        backgroundColor: colors.primary[400],
                    },
                    "& .MuiDataGrid-footerContainer": {
                        borderTop: "none",
                        backgroundColor: colors.blueAccent[700],
                    },
                }}
            >
                <DataGrid rows={mockRows} columns={columns} pageSize={5} />
            </Box>
        </Box>
    );
};

export default ManageBooking;
