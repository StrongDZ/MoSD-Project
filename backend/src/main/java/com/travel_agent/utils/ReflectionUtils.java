package com.travel_agent.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.travel_agent.exceptions.ReflectionException;

public class ReflectionUtils {

    // Phương thức cập nhật đối tượng thông qua Reflection
    public static <T> void updateEntityFields(T entity, Object dto) throws ReflectionException {
        // Lấy tất cả các trường trong DTO
        Field[] fields = dto.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Đảm bảo có thể truy cập vào các trường private

            try {
                // Lấy giá trị của trường
                Object value = field.get(dto);

                // Kiểm tra nếu giá trị không null
                if (value != null) {
                    // Lấy tên trường và tìm setter tương ứng trong Entity
                    String fieldName = field.getName();
                    String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    try {
                        // Tìm phương thức setter trong Entity
                        Method setterMethod = entity.getClass().getMethod(setterName, field.getType());

                        // Gọi phương thức setter để cập nhật giá trị
                        setterMethod.invoke(entity, value);

                    } catch (NoSuchMethodException e) {
                        // Ném exception nếu không tìm thấy setter
                        throw new ReflectionException("No setter found for field: " + fieldName, e);
                    } catch (Exception e) {
                        // Ném exception nếu có lỗi khi gọi setter
                        throw new ReflectionException("Error invoking setter for field: " + fieldName, e);
                    }
                }
            } catch (IllegalAccessException e) {
                // Ném exception nếu không thể truy cập trường
                throw new ReflectionException("Error accessing field: " + field.getName(), e);
            }
        }
    }
}
