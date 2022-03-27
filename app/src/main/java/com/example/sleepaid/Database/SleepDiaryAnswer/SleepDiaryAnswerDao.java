package com.example.sleepaid.Database.SleepDiaryAnswer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.sleepaid.Database.Answer.Answer;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SleepDiaryAnswerDao {
    @Query("SELECT * FROM SleepDiaryAnswer ORDER BY date, questionId")
    Single<List<SleepDiaryAnswer>> getAll();

    @Query("SELECT * FROM SleepDiaryAnswer WHERE SleepDiaryAnswer.questionId IN (:questionIds) ORDER BY date, questionId")
    Single<List<SleepDiaryAnswer>> loadAllByQuestionIds(int[] questionIds);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<SleepDiaryAnswer> sleepDiaryAnswers);
}
