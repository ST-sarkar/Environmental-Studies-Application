package com.example.environmentalstudies.User;

import static java.security.AccessController.getContext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.environmentalstudies.R;
import com.example.environmentalstudies.RoomDatabase.DownloadedFileViewModel;
import com.example.environmentalstudies.User.Fragment.PdfViewFrag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadedPDFViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_downloaded_pdfview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String filename=getIntent().getStringExtra("filename");
        // Get the Downloads directory path
        File downloadsFolder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        // Create a file object with the path to the PDF file
        File pdfFile = new File(downloadsFolder, filename);

        // Check if the file exists
        if (pdfFile.exists()) {
            // Generate a content URI for the file
            Uri pdfUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile);

            // Create an intent to view the PDF file
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(pdfUri, "application/pdf");
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Try to start the intent to view the PDF
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                // If no application is available to view PDF, show a toast message
                Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If the file does not exist, show a toast message
            Toast.makeText(this, "File not found - " + filename, Toast.LENGTH_SHORT).show();
            //calling delete method to delete record from room database which was not found
            DownloadedFileViewModel downloadedFileViewModel = new ViewModelProvider(this).get(DownloadedFileViewModel.class);
            downloadedFileViewModel.deleteFile(filename);
        }

        // Finish the activity
        finish();
    }


}