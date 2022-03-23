package com.example.sleepaid.Database.Answer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AnswerDao {
    @Query("SELECT * FROM Answer ORDER BY questionId, optionId")
    Single<List<Answer>> getAll();

    @Query("SELECT value FROM Option INNER JOIN Answer ON Option.optionId = Answer.optionId WHERE Answer.questionId IN (:questionIds) ORDER BY Answer.questionId")
    Single<List<String>> loadValuesByQuestionIds(int[] questionIds);

    @Query("SELECT * FROM Answer INNER JOIN Question ON Question.questionId = Answer.questionId WHERE Question.questionnaireId IN (:questionnaireIds) ORDER BY answerId")
    Single<List<String>> loadAllByQuestionnaireIds(int[] questionnaireIds);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Answer> answers);
}
