import React, { useState } from "react";
import config from "../../config";
import {
  Container,
  Grid,
  Paper,
  TextField,
  Button,
  Box,
  Typography,
} from "@mui/material";

const TimKhachSan = () => {
  const [hotels, setHotels] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4">Search Hotels</Typography>
    </Container>
  );
};

export default TimKhachSan;

