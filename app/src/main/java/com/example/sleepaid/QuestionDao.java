package com.example.sleepaid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM Question")
    List<Question> getAll();

    @Query("SELECT * FROM Question WHERE questionId IN (:questionIds)")
    List<Question> loadAllByIds(int[] questionIds);

    @Insert
    void insertAll(Question... questions);

    @Delete
    void delete(Question question);
}
