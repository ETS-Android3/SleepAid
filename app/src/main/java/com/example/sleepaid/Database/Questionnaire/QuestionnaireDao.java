package com.example.sleepaid.Database.Questionnaire;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sleepaid.Database.Question.Question;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface QuestionnaireDao {
    @Query("SELECT * FROM Questionnaire ORDER BY questionnaireId")
    Single<List<Questionnaire>> getAll();

    @Query("SELECT * FROM Questionnaire WHERE questionnaireId IN (:questionnaireIds) ORDER BY questionnaireId")
    Single<List<Questionnaire>> loadAllByIds(int[] questionnaireIds);

    @Query("SELECT * FROM Questionnaire WHERE name IN (:questionnaireNames) ORDER BY questionnaireId")
    Single<List<Questionnaire>> loadAllByNames(int[] questionnaireNames);

    @Insert
    Completable insert(List<Question> questions);
}
