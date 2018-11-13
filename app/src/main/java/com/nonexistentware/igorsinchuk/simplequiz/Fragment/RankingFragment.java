package com.nonexistentware.igorsinchuk.simplequiz.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nonexistentware.igorsinchuk.simplequiz.Common.Common;
import com.nonexistentware.igorsinchuk.simplequiz.Interface.RankingCallback;
import com.nonexistentware.igorsinchuk.simplequiz.Model.QuestionScore;
import com.nonexistentware.igorsinchuk.simplequiz.Model.Ranking;
import com.nonexistentware.igorsinchuk.simplequiz.R;

public class RankingFragment extends Fragment {

    View myFragment;

    FirebaseDatabase database;
    DatabaseReference questionScore, rankingTbl;

    int sum = 0;

    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference("Question_Score");
        rankingTbl = database.getReference("Ranking");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myFragment = inflater.inflate(R.layout.fragment_ranking, container, false);

        updateScore(Common.currentUser.getUserName(), new RankingCallback<Ranking>() {
            @Override
            public void callback(Ranking ranking) {
                rankingTbl.child(ranking.getUserName())
                        .setValue(ranking);
                showRanking();
            }
        });

        return myFragment;
    }

    private void showRanking() {
        rankingTbl.orderByChild("score")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                            Ranking local = data.getValue(Ranking.class);
                            Log.d("DEBUG", local.getUserName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateScore(final String userName, RankingCallback<Ranking> callback) {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data:dataSnapshot.getChildren()) {
                            QuestionScore question = data.getValue(QuestionScore.class);
                            sum+= Integer.parseInt(question.getScore());
                        }

                        Ranking ranking = new Ranking(userName, sum);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
