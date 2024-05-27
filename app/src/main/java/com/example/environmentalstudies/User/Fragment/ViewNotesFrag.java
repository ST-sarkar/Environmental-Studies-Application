package com.example.environmentalstudies.User.Fragment;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.environmentalstudies.Admin.Model.PPTUpModel;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.RoomDatabase.DownloadedFile;
import com.example.environmentalstudies.RoomDatabase.DownloadedFileViewModel;
import com.example.environmentalstudies.User.Adapter.StudyAdapter;
import com.example.environmentalstudies.User.Model.NoteDataModel;
import com.example.environmentalstudies.User.Model.StudentData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ViewNotesFrag extends Fragment{
    RecyclerView recyclerView;
    String from="-";
    String location;
    List<PPTUpModel> pdfList = new ArrayList<>();
    List<NoteDataModel> finalList=new ArrayList<>();
    List<String> keyList=new ArrayList<>();
    List<String> bookKeyList=new ArrayList<>();
    StudyAdapter adapter;
    private long downloadId;
    String newFileName,topic,description,type;

    public ViewNotesFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = getArguments().getString("from", "NOTES");
        }

        // Register receiver for download completion
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(downloadReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_notes, container, false);

        recyclerView = view.findViewById(R.id.rec_study_material);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchFiles();

        return view;
    }

    private void fetchFiles() {
        pdfList.clear();
        location = getLocationBasedOnFrom();
        List<PPTUpModel> pdfs=new ArrayList<>();
        List<String> keys=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(location);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PPTUpModel model = dataSnapshot.getValue(PPTUpModel.class);
                        pdfs.add(model);
                        keys.add(dataSnapshot.getKey());
                    }
                    pdfList.addAll(pdfs);
                    keyList.addAll(keys);
                    fetchBookMarks();
                    //setAdapter();
                } else {
                    Toast.makeText(getContext(), "Data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    void fetchBookMarks(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        StudentData studentData= new StudentData();
        studentData.setLoginSharedPreferences(getContext());
        String uid=studentData.getUid();


// Apply query to filter data where the "book" child node has a value of true
        Query query = reference.child("BookMarks").child(uid).orderByChild("location").equalTo(location);

        List<String> keys=new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.e("book info","exist");
                        //PPTUpModel model = dataSnapshot.getValue(PPTUpModel.class);

                        // pdfList.add(model);
                        keys.add(dataSnapshot.getKey());
                    }
                    //setBookAdapter();
                    bookKeyList.addAll(keys);


                } else {
                    Toast.makeText(getContext(), "Data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });
        finalDataList();
    }

    private void finalDataList() {
        for (int i=0;i< keyList.size();i++){
            PPTUpModel model=pdfList.get(i);
            NoteDataModel model1=new NoteDataModel(model.getUri(),model.getTopic(),model.getDesc(),model.getFilename(),model.getUploadDate(),model.getType(),false);
            if(bookKeyList.contains(keyList.get(i))){
                model1.setBook(true);
            }
            finalList.add(model1);
        }
        setAdapter();
    }


    private String getLocationBasedOnFrom() {
        switch (from) {
            case "NOTES":
                return "Note_Uploads";
            case "PROJECTS":
                return "Project_Uploads";
            case "QBANKS":
                return "QBank_Uploads";
            default:
                return "Note_Uploads";
        }
    }

    private void setAdapter() {
        adapter = new StudyAdapter(finalList, keyList,getContext(), "viewnotes", new StudyAdapter.BookItemClickListener() {
            @Override
            public void onDownloadClick(View view, int position) {
                downloadId=downloadPdfFile(position);
            }

            @Override
            public void onViewClick(View view, int position) {
                viewPdfFile(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private long downloadPdfFile(int position) {
        PPTUpModel model = pdfList.get(position);
        newFileName=model.getFilename();
        topic=model.getTopic();
        description=model.getDesc();
        type=model.getType();

        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(model.getUri());
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle("Downloading PDF") // Title of the Download Notification
                .setDescription("Downloading document...") // Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Visibility of the download Notification
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true); // Set if download is allowed on roaming network

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getContext(), DIRECTORY_DOWNLOADS, model.getFilename());

        return downloadManager.enqueue(request);
    }

    // Broadcast Receiver to get notification of download completion
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == id) {
                // Your download has completed!
                Toast.makeText(context, "Download completed", Toast.LENGTH_LONG).show();

                // Post download actions or method calls
                DownloadedFileViewModel downloadedFileViewModel = new ViewModelProvider(getActivity()).get(DownloadedFileViewModel.class);
                DownloadedFile newFile = new DownloadedFile(newFileName,topic,description,type);
                downloadedFileViewModel.insert(newFile);

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(downloadReceiver);
    }


    private void viewPdfFile(int position) {
        PPTUpModel model = pdfList.get(position);
        Uri uri = Uri.parse(model.getUri());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooser = Intent.createChooser(intent, "Open PDF");
        try {
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No PDF reader found. Please install one to view the document.", Toast.LENGTH_LONG).show();
        }
    }
}