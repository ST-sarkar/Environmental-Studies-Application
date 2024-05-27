package com.example.environmentalstudies.User.Fragment;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.environmentalstudies.R;


public class StudyMaterialFrag extends Fragment {

    CardView quiz,notes,ppt,projects,books,Q_banks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_study_material, container, false);

        quiz=view.findViewById(R.id.quiz);
        notes=view.findViewById(R.id.notes);
        ppt=view.findViewById(R.id.ppt);
        projects=view.findViewById(R.id.project);
        books=view.findViewById(R.id.book);
        Q_banks=view.findViewById(R.id.Q_bank);

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizHomeFrag quizHomeFrag = new QuizHomeFrag();
                setStudyFragment(quizHomeFrag,"");            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewNotesFrag viewNotesFrag=new ViewNotesFrag();
                setStudyFragment(viewNotesFrag,"NOTES");
                }
        });

        ppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPPTFrag frag=new ViewPPTFrag();
                setStudyFragment(frag,"PPT");
            }
        });

        projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewNotesFrag viewNotesFrag=new ViewNotesFrag();
                setStudyFragment(viewNotesFrag,"PROJECTS");
            }
        });

        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookBuyFragment fragment=new BookBuyFragment();
                setStudyFragment(fragment,"");
            }
        });

        Q_banks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewNotesFrag viewNotesFrag=new ViewNotesFrag();
                setStudyFragment(viewNotesFrag,"QBANKS");
            }
        });


        return  view;
    }

    void setStudyFragment(Fragment fragment, String from){

        Bundle bundle=new Bundle();
        bundle.putString("from",from);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();

    }

}