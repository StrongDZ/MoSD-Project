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

  return null;
};

export default ManageBooking;

