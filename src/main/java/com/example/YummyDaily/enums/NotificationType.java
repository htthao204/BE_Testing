package com.example.YummyDaily.enums;

public enum NotificationType {
    // User Notifications
    RECIPE_APPROVED("Công thức được duyệt"),
    RECIPE_REJECTED("Công thức bị từ chối"),
    RECIPE_EDIT_REQUEST("Yêu cầu chỉnh sửa công thức"),
    USER_FOLLOWED("Có người theo dõi bạn"),
    RATING_RECEIVED("Công thức nhận được đánh giá"),
    RATING_UPDATED("Đánh giá công thức được cập nhật"), // Thêm
    RATING_DELETED("Đánh giá công thức bị xóa"), // Thêm
    GENERAL_USER("Thông báo chung gửi cho người dùng"),
    PASSWORD_CHANGED("Thay đổi mật khẩu"),
    RECIPE_LIKED("Công thức của bạn được yêu thích"),
    RECIPE_UPDATED("Công thức đã được cập nhật"),

    // Admin Notifications
    NEW_RECIPE_SUBMISSION("Có công thức mới chờ duyệt"),
    RECIPE_REPORTED("Công thức bị báo cáo"),
    USER_FEEDBACK("Góp ý hoặc khiếu nại từ người dùng"),
    GENERAL_ADMIN("Thông báo chung gửi cho admin"),
    NEW_USER_REGISTERED("Có người dùng mới đăng ký"),
    USER_REPORTED("Có người dùng bị báo cáo"),

    // System Notifications
    SYSTEM_ALERT("Cảnh báo hệ thống"),
    SYSTEM_MAINTENANCE("Bảo trì hệ thống"),
    WELCOME_MESSAGE("Chào mừng bạn đến với Yummy Daily"),
    NEW_FEATURE("Tính năng mới được cập nhật"),
    POLICY_UPDATE("Chính sách sử dụng được cập nhật");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}