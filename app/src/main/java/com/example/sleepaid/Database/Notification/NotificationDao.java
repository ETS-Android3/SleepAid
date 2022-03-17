package com.example.sleepaid.Database.Notification;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM Notification ORDER BY time")
    LiveData<List<Notification>> getAll();

    @Query("SELECT * FROM Notification WHERE notificationId IN (:notificationIds) ORDER BY notificationId")
    Single<List<Notification>> loadAllByIds(int[] notificationIds);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Notification> notifications);

    @Update
    Completable update(List<Notification> notifications);

    @Delete
    Completable delete(List<Notification> notifications);
}
