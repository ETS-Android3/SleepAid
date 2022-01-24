package com.example.sleepaid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AnswerDao {
    @Query("SELECT * FROM Answer")
    List<Answer> getAll();

    @Query("SELECT * FROM Answer WHERE answerId IN (:answerIds)")
    List<Answer> loadAllByIds(int[] answerIds);

    @Query("SELECT * FROM Answer WHERE optionId IN (:optionIds)")
    List<Answer> loadAllByOptionIds(int[] optionIds);

    @Query("SELECT * FROM Answer WHERE questionId IN (:questionIds)")
    List<Answer> loadAllByQuestionIds(int[] questionIds);

    @Insert
    void insertAll(Answer... answers);

    @Delete
    void delete(Answer answers);
}
