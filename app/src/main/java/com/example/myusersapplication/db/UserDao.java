package com.example.myusersapplication.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;
import androidx.room.Query;

import com.example.myusersapplication.models.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUsers(List<User> users);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users LIMIT :limit OFFSET :offset")
    List<User> getUsersWithPaging(int limit, int offset);

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User user);

    @Query("UPDATE users SET email = :email, first_name = :first_name, last_name = :last_name, avatar = :avatar WHERE id = :id")
    void updateUserById(int id, String email, String first_name, String last_name, String avatar);

    @Query("DELETE FROM users WHERE id = :id")
    int deleteUser(int id);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
}