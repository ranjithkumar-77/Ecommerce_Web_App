package com.ecommerce.service;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.PasswordUtil;

import java.sql.SQLException;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public User registerUser(String firstName, String lastName, String email, String password, String phone)
            throws SQLException {
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.length() < 8) {
            return null;
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(hashedPassword);
        user.setPhone(phone != null ? phone.trim() : "");
        user.setCountry("India");
        userDAO.createUser(user);
        return user;
    }

    public User loginUser(String email, String password) {
        if (email == null || password == null) return null;
        User user = userDAO.getUserByEmail(email.trim().toLowerCase());
        if (user == null) return null;
        if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public boolean updateProfile(int userId, String firstName, String lastName, String phone) {
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty()) {
            return false;
        }
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setPhone(phone != null ? phone.trim() : "");
        return userDAO.updateUser(user);
    }

    public boolean updateAddress(int userId, String line1, String line2, String city,
                                  String state, String pincode, String country) {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        user.setAddressLine1(line1);
        user.setAddressLine2(line2);
        user.setCity(city);
        user.setState(state);
        user.setPincode(pincode);
        user.setCountry(country);
        return userDAO.updateUser(user);
    }

    public boolean changePassword(int userId, String oldPassword, String newPasswordPlain) {
        if (oldPassword == null || newPasswordPlain == null || newPasswordPlain.length() < 8) {
            return false;
        }
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
            return false;
        }
        String newHash = PasswordUtil.hashPassword(newPasswordPlain);
        return userDAO.updatePassword(userId, newHash);
    }
}
