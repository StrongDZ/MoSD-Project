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

  return null;
};

export default ManageBooking;

