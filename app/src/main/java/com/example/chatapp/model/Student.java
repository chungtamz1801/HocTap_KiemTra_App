package com.example.chatapp.model;

import java.util.ArrayList;
import java.util.List;

// Cho kế thừa User để có sẵn: id, email, password, name (từ class cha)
public class Student extends User {

    // Các thuộc tính riêng của Student mà User không có
    private boolean active;
    private double score;
    private List<String> examIds = new ArrayList<>();

    // Constructor rỗng (Bắt buộc để Firebase map dữ liệu)
    public Student() {
        super(); // Gọi constructor rỗng của User
    }

    // Constructor đầy đủ (dùng khi tạo mới sinh viên lúc đăng ký)
    public Student(String id, String password, String email, String name, boolean active) {
        super(id, password, email, name); // Đẩy thông tin cơ bản lên class User quản lý
        this.active = active;
    }

    // Constructor dùng để lưu điểm (như code cũ của bạn)
    public Student(float point, List<String> examIds) {
        super();
        this.score = point;
        this.examIds = examIds;
    }

    // --- GETTER & SETTER ---
    // Lưu ý: Không cần tạo get/set cho id, name, email, password nữa vì class User đã có rồi (do kế thừa)

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<String> getExamIds() {
        return examIds;
    }

    public void setExamIds(List<String> examIds) {
        this.examIds = examIds;
    }
}