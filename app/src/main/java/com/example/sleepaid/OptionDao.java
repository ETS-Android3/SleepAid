package com.example.sleepaid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OptionDao {
    @Query("SELECT * FROM Option")
    List<Option> getAll();

    @Query("SELECT * FROM Option WHERE optionId IN (:optionIds)")
    List<Option> loadAllByIds(int[] optionIds);

    @Query("SELECT * FROM Option WHERE questionId = (:questionIds)")
    List<Option> loadAllByQuestionIds(int[] questionIds);

    @Insert
    void insertAll(Option... options);

    @Delete
    void delete(Option option);
}
