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
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import CancelOutlinedIcon from "@mui/icons-material/CancelOutlined";
import PendingOutlinedIcon from "@mui/icons-material/PendingOutlined";
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
      fetchBookings();
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
              {row.state === "CONFIRMED"
                ? "Đã xác nhận"
                : row.state === "CANCELLED"
                ? "Đã hủy"
                : "Đang chờ"}
            </Typography>
          </Box>
        );
      },
    },
  ];

  return null;
};

export default ManageBooking;

