package com.example.environmentalstudies.User.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.environmentalstudies.R;
//import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;


public class PdfViewFrag extends Fragment {

    private static final String ARG_PAGE = "page";
    private Bitmap pageBitmap;


    public PdfViewFrag() {
        // Required empty public constructor
    }

    public static PdfViewFrag newInstance(Bitmap pageBitmap) {
        PdfViewFrag fragment = new PdfViewFrag();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PAGE, pageBitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageBitmap = getArguments().getParcelable(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedaInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_pdf_view, container, false);

        ImageView pdfPageImageView = view.findViewById(R.id.pdfPageImageView);
        pdfPageImageView.setImageBitmap(pageBitmap);

        return view;
    }

}