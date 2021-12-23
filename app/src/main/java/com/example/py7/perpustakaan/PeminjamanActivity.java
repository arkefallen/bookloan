package com.example.py7.perpustakaan;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.py7.perpustakaan.adapters.CustomCursorAdapter;
import com.example.py7.perpustakaan.adapters.DBHelper;

import org.w3c.dom.Text;

public class PeminjamanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView Is;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peminjaman);

        // Bikin toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bikin floating button untuk pindah ke AddActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PeminjamanActivity.this, AddActivity.class));
            }
        });

        // Mengambil data daftar peminjaman

        // Inisialisasi objek dbhelper untuk mengambil data dari database sqlite
        dbHelper = new DBHelper(this);

        // Inisialisasi ListView berdasarkan id list_pinjam
        Is = (ListView)findViewById(R.id.list_pinjam);

        // Inisialisasi tiap listview ketika di klik
        Is.setOnItemClickListener(this);

        // Melakukan proses ambil data
        setupListView();

    }

    private void setupListView() {

        // Mengambil semua data peminjaman yang nantinya akan ditampilkan dalam activity peminjaman

        // Mengambil data lewat dbhelper yang akan disimpan kedalam cursor
        Cursor cursor = dbHelper.allData();
        CustomCursorAdapter customCursorAdapter = new CustomCursorAdapter(this, cursor, 1);
        Is.setAdapter(customCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_peminjaman, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long I) {

        // Melakukan perpindahan activity ketika item listview di klik

        // Ambil ID item yang di click
        TextView getID = (TextView)view.findViewById(R.id.listID);
        final long id = Long.parseLong(getID.getText().toString());

        // Mencari data berdasarkan ID yang diambil
        Cursor cur = dbHelper.oneData(id);
        cur.moveToFirst();

        // Pindah ke AddActivity sambil mengirim data dari yang telah dicari
        Intent idpinjam = new Intent(PeminjamanActivity.this, AddActivity.class);
        idpinjam.putExtra(DBHelper.row_id, id);
        startActivity(idpinjam);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupListView();
    }
}
