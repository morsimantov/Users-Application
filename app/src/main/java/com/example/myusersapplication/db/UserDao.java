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

    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAllUsersLiveData();

    @Query("SELECT * FROM user LIMIT :limit OFFSET :offset")
    LiveData<List<User>> getUsersWithPaging(int limit, int offset);

    @Query("SELECT * FROM user WHERE id = :id")
    User getUserById(int id);

    @Query("UPDATE user SET email = :email, first_name = :first_name, last_name = :last_name, avatar = :avatar WHERE id = :id")
    void updateUserById(int id, String email, String first_name, String last_name, String avatar);

    @Query("DELETE FROM user WHERE id = :id")
    int deleteUser(int id);

    @Query("SELECT * FROM user WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT COUNT(*) FROM user")
    int getUserCount();
}