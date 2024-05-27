package com.example.environmentalstudies.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.environmentalstudies.Admin.Adapter.AddMCQAdapter;
import com.example.environmentalstudies.Admin.Model.AddMcqModel;
import com.example.environmentalstudies.Login;
import com.example.environmentalstudies.R;
import com.example.environmentalstudies.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Addmcq extends AppCompatActivity {

    DatabaseReference reference;
    int index=1;

    ArrayList<AddMcqModel> arrayList;

    List<String> Que;
    List<String> op1;
    List<String> op2;
    List<String> op3;
    List<String> op4;
    List<String> ans;
    List<String> unit;

    RelativeLayout submitLayout,selectExcelLayout;

    private static final int PICK_EXCEL_REQUEST_CODE = 123;

    RecyclerView excelmcqRVL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmcq);

        reference= FirebaseDatabase.getInstance().getReference();

        Que = new ArrayList<>();
        op1 = new ArrayList<>();
        op2 = new ArrayList<>();
        op3 = new ArrayList<>();
        op4 = new ArrayList<>();
        ans = new ArrayList<>();
        unit = new ArrayList<>();

        selectExcelLayout= findViewById(R.id.selectExcelLayout);
        submitLayout = findViewById(R.id.submitLayout);

        arrayList = new ArrayList<>();

        excelmcqRVL = findViewById(R.id.excelmcqRVL);
        excelmcqRVL.setLayoutManager(new LinearLayoutManager(Addmcq.this));
        excelmcqRVL.setHasFixedSize(false);

        findViewById(R.id.registerStudentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(Addmcq.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait..");
                progressDialog.show();


                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        for (int l = 0;l<arrayList.size();l++){
                            String que = arrayList.get(l).getQue();
                            String o1 = arrayList.get(l).getOp1();
                            String o2 = arrayList.get(l).getOp2();
                            String o3 = arrayList.get(l).getOp3();
                            String o4 = arrayList.get(l).getOp4();
                            String ans = arrayList.get(l).getAns();
                            String un = arrayList.get(l).getUnit();

                            Log.d("YourTag", "Question: " + que);
                            Log.d("YourTag", "Option 1: " + o1);
                            Log.d("YourTag", "Option 2: " + o2);
                            Log.d("YourTag", "Option 3: " + o3);
                            Log.d("YourTag", "Option 4: " + o4);
                            Log.d("YourTag", "Answer: " + ans);
                            Log.d("YourTag", "Unit: " + un);

                            AddMCQData(que, o1,o2,o3,o4,ans,un);


                        }
                        progressDialog.dismiss();
                        //Intent intent = new Intent(getApplicationContext(), Addmcq.class);
                        //startActivity(intent);
                        Toast.makeText(Addmcq.this, "Questions Uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },2000);

            }
        });
        findViewById(R.id.selectexcelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickExcelFile();

            }
        });

    }

    private void AddMCQData(String que, String o1, String o2, String o3, String o4, String ans, String un) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {


                AddMcqModel model=new AddMcqModel(que,o1,o2,o3,o4,ans,un);
                reference.child("Quizzes").child(un).push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        index=index+1;
                        //Toast.makeText(Addmcq.this, "index="+index, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Addmcq.this, "error "+e, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }
    private void pickExcelFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Allow all file types
        String[] mimeTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_EXCEL_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_EXCEL_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the URI of the selected file
                Uri uri = data.getData();

                // Get the file name
                String fileName = getFileName(uri);
                //String selectedFileUri = data.getData().toString();

                try {
                    UploadData(uri);
                } catch (IOException e) {
                    Log.d("Selected Excel file:", "Cell Value: " + e.getMessage());
                }

                Log.d("Selected Excel file:", "Cell Value: " + fileName);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Addmcq.this,AdminDashboard.class));
        finish();
    }

    private void UploadData(Uri excelFilePath) throws IOException {
        DataFormatter dataFormatter = new DataFormatter();
        arrayList.clear();
        Que.clear();
        op1.clear();
        op2.clear();
        op3.clear();
        op4.clear();
        ans.clear();
        unit.clear();

        InputStream inputStream = getContentResolver().openInputStream(excelFilePath);

        assert inputStream != null;
        org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(inputStream);
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getPhysicalNumberOfCells() >= 7) {
                org.apache.poi.ss.usermodel.Cell[] cells = new org.apache.poi.ss.usermodel.Cell[7];
                for (int i = 0; i < 7; i++) {
                    cells[i] = row.getCell(i);
                    Log.e("file ", cells[i].toString());
                }

                Que.add(dataFormatter.formatCellValue(cells[0]));
                op1.add(dataFormatter.formatCellValue(cells[1]));
                op2.add(dataFormatter.formatCellValue(cells[2]));
                op3.add(dataFormatter.formatCellValue(cells[3]));
                op4.add(dataFormatter.formatCellValue(cells[4]));
                ans.add(dataFormatter.formatCellValue(cells[5]));
                unit.add(dataFormatter.formatCellValue(cells[6]));

            } else {
                Log.e("Error", "Row does not have enough columns");
            }
        }
        for (int l = 1;l<Que.size();l++){
            String sn = Que.get(l);
            String rn = op1.get(l);
            String cn = op2.get(l);
            String y = op3.get(l);
            String un = op4.get(l);
            String pw = ans.get(l);
            String pn = unit.get(l);

            arrayList.add(new AddMcqModel(sn,rn,cn,y,un,pw,pn));
        }
        excelmcqRVL.setAdapter(new AddMCQAdapter(arrayList));
        selectExcelLayout.setVisibility(View.GONE);
        submitLayout.setVisibility(View.VISIBLE);


        workbook.close();
        inputStream.close();
    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}