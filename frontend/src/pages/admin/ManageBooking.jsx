import {
    Box,
    Typography,
    useTheme,
    Button,
    Modal,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "../../theme";
import Header from "../../components/admin/Header";
import { useState, useEffect } from "react";
import axios from "axios";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import CancelOutlinedIcon from "@mui/icons-material/CancelOutlined";
import PendingOutlinedIcon from "@mui/icons-material/PendingOutlined";
import VisibilityIcon from "@mui/icons-material/Visibility";
import config from "../../config";
import { axiosRequest } from "../../utils/axiosUtils";

const ManageBooking = () => {
    const theme = useTheme();
    const colors = tokens(theme.palette.mode);
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedBooking, setSelectedBooking] = useState(null);
    const [openModal, setOpenModal] = useState(false);
    const [confirmModalOpen, setConfirmModalOpen] = useState(false);
    const [selectedBookingForConfirm, setSelectedBookingForConfirm] = useState(null);
    const [confirmNote, setConfirmNote] = useState("");

    useEffect(() => {
        fetchBookings();
    }, []);

    const fetchBookings = async () => {
        try {
            const response = await axiosRequest({
                url: `${config.api.url}/api/booking/company`,
                method: "GET",
            });

            if (response.data && response.data.data) {
                const bookings = response.data.data.map((booking) => ({
                    ...booking,
                    id: `booking-${booking.bookingId}`,
                }));
                setBookings(bookings);
            } else {
                setBookings([]);
            }

            setLoading(false);
        } catch (error) {
            console.error("Error fetching bookings:", error);
            console.error("Error details:", {
                message: error.message,
                response: error.response?.data,
                status: error.response?.status,
            });
            setBookings([]);
            setLoading(false);
        }
    };

    const handleStatusChange = async (bookingId, newStatus) => {
        try {
            await axiosRequest({
                url: `${config.api.url}/api/booking/${bookingId}/status`,
                method: "PUT",
                data: { status: newStatus },
            });
            fetchBookings(); // Refresh data after update
        } catch (error) {
            console.error("Error updating booking status:", error);
        }
    };

    const handleViewDetails = (booking) => {
        setSelectedBooking(booking);
        setOpenModal(true);
    };

    const handleConfirmClick = (booking) => {
        setSelectedBookingForConfirm(booking);
        setConfirmModalOpen(true);
    };

    const handleConfirmSubmit = async () => {
        try {
            await axiosRequest({
                url: `${config.api.url}/api/booking/${selectedBookingForConfirm.bookingId}/status`,
                method: "PUT",
                data: {
                    status: "CONFIRMED",
                    note: confirmNote,
                },
            });
            setConfirmModalOpen(false);
            setConfirmNote("");
            fetchBookings();
        } catch (error) {
            console.error("Error confirming booking:", error);
        }
    };

    const columns = [
        {
            field: "bookingId",
            headerName: "Mã đặt chỗ",
            flex: 0.5,
        },
        {
            field: "customerName",
            headerName: "Tên khách hàng",
            flex: 1,
        },
        {
            field: "phone",
            headerName: "Số điện thoại",
            flex: 0.8,
        },
        {
            field: "email",
            headerName: "Email",
            flex: 1.5,
        },
        {
            field: "startDate",
            headerName: "Ngày nhận phòng",
            flex: 1,
        },
        {
            field: "endDate",
            headerName: "Ngày trả phòng",
            flex: 1,
        },
        {
            field: "state",
            headerName: "Trạng thái",
            flex: 1,
            renderCell: ({ row }) => {
                return (
                    <Box
                        width="100%"
                        m="0 auto"
                        p="5px"
                        display="flex"
                        justifyContent="center"
                        backgroundColor={
                            row.state === "CONFIRMED"
                                ? colors.greenAccent[600]
                                : row.state === "CANCELLED"
                                ? colors.redAccent[700]
                                : colors.blueAccent[700]
                        }
                        borderRadius="4px"
                    >
                        {row.state === "CONFIRMED" && <CheckCircleOutlineIcon />}
                        {row.state === "CANCELLED" && <CancelOutlinedIcon />}
                        {row.state === "PENDING" && <PendingOutlinedIcon />}
                        <Typography color={colors.grey[100]} sx={{ ml: "5px" }}>
                            {row.state === "CONFIRMED" ? "Đã xác nhận" : row.state === "CANCELLED" ? "Đã hủy" : "Đang chờ"}
                        </Typography>
                    </Box>
                );
            },
        },
        {
            field: "actions",
            headerName: "Thao tác",
            flex: 1.7,
            renderCell: ({ row }) => {
                return (
                    <Box
                        sx={{
                            display: "flex",
                            gap: "10px",
                            justifyContent: "center",
                            alignItems: "center",
                            width: "100%",
                            overflow: "visible",
                        }}
                    >
                        <Button variant="outlined" size="small" onClick={() => handleViewDetails(row)} color={colors.grey[100]}>
                            Chi tiết
                        </Button>
                        {row.state === "PENDING" && (
                            <>
                                <Button variant="outlined" size="small" onClick={() => handleConfirmClick(row)} color={colors.grey[100]}>
                                    Xác nhận
                                </Button>
                                <Button
                                    variant="outlined"
                                    size="small"
                                    onClick={() => handleStatusChange(row.bookingId, "CANCELLED")}
                                    color={colors.grey[100]}
                                >
                                    Hủy
                                </Button>
                            </>
                        )}
                    </Box>
                );
            },
        },
    ];

    return (
        <Box m="20px">
            <Header title="QUẢN LÝ ĐẶT CHỖ" subtitle="Quản lý đặt phòng và du thuyền" />
            <Box
                m="40px 0 0 0"
                height="75vh"
                sx={{
                    "& .MuiDataGrid-root": {
                        border: "none",
                    },
                    "& .MuiDataGrid-cell": {
                        borderBottom: "none",
                        overflow: "visible",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
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
                <DataGrid rows={bookings} columns={columns} loading={loading} pageSize={10} />
            </Box>

            <Modal open={openModal} onClose={() => setOpenModal(false)} aria-labelledby="booking-details-modal">
                <Box
                    sx={{
                        position: "absolute",
                        top: "50%",
                        left: "50%",
                        transform: "translate(-50%, -50%)",
                        width: "80%",
                        maxWidth: 800,
                        bgcolor: "background.paper",
                        boxShadow: 24,
                        p: 4,
                        borderRadius: 2,
                    }}
                >
                    {selectedBooking && (
                        <>
                            <Typography variant="h5" component="h2" gutterBottom>
                                Chi tiết đặt phòng
                            </Typography>

                            <Box mb={3}>
                                <Typography variant="h6" gutterBottom>
                                    Thông tin khách hàng
                                </Typography>
                                <Typography>Tên: {selectedBooking.customerName}</Typography>
                                <Typography>Số điện thoại: {selectedBooking.phone}</Typography>
                                <Typography>Email: {selectedBooking.email}</Typography>
                            </Box>

                            <Box mb={3}>
                                <Typography variant="h6" gutterBottom>
                                    Thông tin du thuyền
                                </Typography>
                                <Typography>Tên du thuyền: {selectedBooking.ship?.shipName}</Typography>
                                <Typography>Hãng: {selectedBooking.ship?.companyName}</Typography>
                                <Typography>Địa chỉ: {selectedBooking.ship?.address}</Typography>
                                <Typography>Ngày nhận phòng: {new Date(selectedBooking.startDate).toLocaleDateString("vi-VN")}</Typography>
                                <Typography>
                                    Ngày trả phòng:{" "}
                                    {selectedBooking.endDate ? new Date(selectedBooking.endDate).toLocaleDateString("vi-VN") : "Không có"}
                                </Typography>
                                <Typography>Số người lớn: {selectedBooking.adults}</Typography>
                                <Typography>Số trẻ em: {selectedBooking.children}</Typography>
                                {selectedBooking.specialRequest && <Typography>Yêu cầu đặc biệt: {selectedBooking.specialRequest}</Typography>}
                            </Box>

                            <Typography variant="h6" gutterBottom>
                                Danh sách phòng đã đặt
                            </Typography>
                            <TableContainer component={Paper} sx={{ mb: 3 }}>
                                <Table>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Tên phòng</TableCell>
                                            <TableCell>Diện tích</TableCell>
                                            <TableCell>Số người tối đa</TableCell>
                                            <TableCell align="right">Số lượng</TableCell>
                                            <TableCell align="right">Đơn giá</TableCell>
                                            <TableCell align="right">Thành tiền</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {selectedBooking.rooms?.map((item) => (
                                            <TableRow key={item.room.roomId}>
                                                <TableCell>{item.room.roomName}</TableCell>
                                                <TableCell>{item.room.size}m²</TableCell>
                                                <TableCell>{item.room.maxPersons} người</TableCell>
                                                <TableCell align="right">{item.quantity}</TableCell>
                                                <TableCell align="right">{item.room.roomPrice.toLocaleString("vi-VN")} VNĐ</TableCell>
                                                <TableCell align="right">
                                                    {(item.room.roomPrice * item.quantity).toLocaleString("vi-VN")} VNĐ
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>

                            <Box sx={{ textAlign: "right" }}>
                                <Typography variant="h6">Tổng tiền: {selectedBooking.totalAmount?.toLocaleString("vi-VN")} VNĐ</Typography>
                            </Box>

                            <Box sx={{ mt: 3, textAlign: "right" }}>
                                <Button variant="contained" onClick={() => setOpenModal(false)} sx={{ mr: 1 }}>
                                    Đóng
                                </Button>
                            </Box>
                        </>
                    )}
                </Box>
            </Modal>

            <Dialog open={confirmModalOpen} onClose={() => setConfirmModalOpen(false)}>
                <DialogTitle>Xác nhận đơn đặt chỗ</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>Bạn có chắc chắn muốn xác nhận đơn đặt chỗ này?</Typography>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Ghi chú xác nhận"
                        fullWidth
                        multiline
                        rows={4}
                        value={confirmNote}
                        onChange={(e) => setConfirmNote(e.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setConfirmModalOpen(false)}>Hủy</Button>
                    <Button onClick={handleConfirmSubmit} variant="contained" color="primary">
                        Xác nhận
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default ManageBooking;
