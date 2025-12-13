// src/utils/toastUtils.js hoặc toastHandler.js

import { toast } from "react-toastify";

export const handleErrorToast = (error, defaultMessage = "Đã có lỗi xảy ra") => {
  if (!error) {
    toast.error(defaultMessage);
    return;
  }

  const status = error.response?.status;
  const serverMessage = error.response?.data?.message;

  switch (status) {
    case 400:
      toast.error(serverMessage || "Yêu cầu không hợp lệ");
      break;
    case 401:
      toast.error("Bạn cần đăng nhập để tiếp tục");
      break;
    case 403:
      toast.error("Bạn không có quyền truy cập");
      break;
    case 404:
      toast.error("Không tìm thấy tài nguyên");
      break;
    case 500:
      toast.error("Lỗi server! Vui lòng thử lại sau");
      break;
    default:
      toast.error(serverMessage || defaultMessage);
  }
};
