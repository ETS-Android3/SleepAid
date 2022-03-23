package com.example.sleepaid.Database.Option;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface OptionDao {
    @Query("SELECT * FROM Option ORDER BY questionId, optionId")
    Single<List<Option>> getAll();

    @Query("SELECT * FROM Option INNER JOIN Question ON Option.questionId = Question.questionId WHERE Question.questionnaireId IN (:questionnaireIds) ORDER BY optionId")
    Single<List<Option>> loadAllByQuestionnaireIds(int[] questionnaireIds);

    @Insert
    Completable insert(List<Option> options);
}
