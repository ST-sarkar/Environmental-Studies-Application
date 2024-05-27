package com.example.environmentalstudies.User.Fragment;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.ULocale;
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
import com.example.environmentalstudies.User.Adapter.BookMarkAdapter;
import com.example.environmentalstudies.User.Adapter.DownloadAdapter;
import com.example.environmentalstudies.User.Adapter.StudyAdapter;
import com.example.environmentalstudies.User.Model.NoteDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BookTabFrag extends Fragment {

    RecyclerView recyclerView;
    String from;
    //List<PPTUpModel> pdfList = new ArrayList<>();
    List<NoteDataModel> finalList=new ArrayList<>();
    List<String> keyList=new ArrayList<>();
    StudyAdapter adapter;
    private long downloadId;
    String newFileName,topic,description,type;

    public BookTabFrag(String from) {
        // Required empty public constructor
        this.from=from;
    }
    public BookTabFrag() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register receiver for download completion
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(downloadReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_book_tab, container, false);

        recyclerView = view.findViewById(R.id.rec_bookmark);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        setAdapter();

       // recyclerView.setAdapter(adapter);

        fetchData();
        return view;
    }

    private void fetchData() {
        String loc;
        if(from.equals("NOTE"))
        {
            loc="Note_Uploads";
        } else if (from.equals("PROJECT")) {
            loc="Project_Uploads";
        }else if(from.equals("QBANK")){
            loc="QBank_Uploads";
        }else {
            loc="Note_Uploads";
        }

        Log.e("book","loc= "+loc+"from="+from);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

// Apply query to filter data where the "book" child node has a value of true
        Query query = reference.child("BookMarks").child(uid).orderByChild("location").equalTo(loc);

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
                    keyList.addAll(keys);
                    fetchPDFWithKeys(loc);

                } else {
                    Toast.makeText(getContext(), "Data not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void fetchPDFWithKeys(String location) {
        // Assuming 'location' is the path in your Firebase database where the data is stored.
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(location);
        //List<PPTUpModel> dataList = new ArrayList<>(); // Replace 'DataModel' with your actual data model class

        for (String key : keyList) {
            databaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    PPTUpModel data = dataSnapshot.getValue(PPTUpModel.class);
                    if (data != null) {
                        //dataList.add(data);
                        Log.e("book","onDatachange");
                        NoteDataModel model=new NoteDataModel(data.getUri(),data.getTopic(),data.getDesc(),data.getFilename(),data.getUploadDate(),data.getType(),true);
                        finalList.add(model);
                       // pdfList.add(model);
                    }

                    // Check if all data has been fetched, based on keyList size
                    if (finalList.size() == keyList.size()) {
                        // All data fetched, update UI here (e.g., refresh your adapter)
                        // Note: Make sure to call notifyDataSetChanged() on your adapter to refresh the data
                        Log.e("book","setting adapter");
                        //setBookAdapter();
                        adapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors

                }
            });
        }
    }

    private void setAdapter() {
        adapter = new StudyAdapter(finalList, keyList,getContext(), "Bookmark", new StudyAdapter.BookItemClickListener() {
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


    /*private void setBookAdapter() {
      // if (!pdfList.isEmpty()) {
            //Log.e("book","List not empty and keylist size="+keyList.size());
        Log.e("book","in setting adapter");
            adapter1 = new BookMarkAdapter(pdfList, keyList, getActivity(), "bookmark");
            recyclerView.setAdapter(adapter1);

        //}else {
          //  Toast.makeText(getContext(), "list is empty", Toast.LENGTH_SHORT).show();
        //}
    }

     */


    private long downloadPdfFile(int position) {
        NoteDataModel model = finalList.get(position);
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
        NoteDataModel model = finalList.get(position);
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