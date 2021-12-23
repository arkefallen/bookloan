package com.example.py7.perpustakaan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Matrix;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.py7.perpustakaan.adapters.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    DBHelper dbHelper;
    TextView TvStatus;
    Button BtnProses;
    EditText TxID, TxNama, TxJudul, TxtglPinjam, TxtglKembali, TxStatus;
    long id;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);

        // Mengambil id dari proses intent
        id = getIntent().getLongExtra(DBHelper.row_id, 0);

        // Membuat form input data
        TxID = (EditText)findViewById(R.id.txID);
        TxNama = (EditText)findViewById(R.id.txNamaAnggota);
        TxJudul = (EditText)findViewById(R.id.txJudul);
        TxtglPinjam = (EditText)findViewById(R.id.txPinjam);
        TxtglKembali = (EditText)findViewById(R.id.txKembali);
        TxStatus = (EditText)findViewById(R.id.txStatus);

        TvStatus = (TextView)findViewById(R.id.tVStatus);
        BtnProses = (Button)findViewById(R.id.btnProses);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        getData();

        // Memunculkan dialog kalender untuk memilih tanggal kembali
        TxtglKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        // Button memproses pengembalian buku
        BtnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesKembali();
            }
        });

        ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setDisplayHomeAsUpEnabled(true);
    }

    private void prosesKembali() {

        // Melakukan prroses pengembalian buku

        // Membuat komponen alert sebagai konfirmasi pengembalian buku
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setMessage("Proses ke pengembalian buku?");
        builder.setCancelable(true);

        // Yang terjadi ketika memilih tombol 'Proses'
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Mengambil ID dari item
                String idpinjam = TxID.getText().toString().trim();

                String kembali = "Dikembalikan";

                // Melakukan perubahan status pengembalian menggunakan variabel 'kembali'
                ContentValues values = new ContentValues();
                values.put(DBHelper.row_status, kembali);
                dbHelper.updateData(values, id);

                // Memunculkan toast sebagai pemberitahuan bahwa proses pengembalian berhasil dilakukan
                Toast.makeText(AddActivity.this, "Proses Pengembalian Berhasil", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Yang terjadi ketika memilih tombol 'Batal'
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Membatalkan proses
                dialog.cancel();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDateDialog() {

        // Memunculkan dialog date untuk memilih tanggal kembali

        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                TxtglKembali.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getData() {
        // Untuk mengambil data daftar peminjaman buku yang ada

        // Dapetin tanggal peminjaman
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String tglPinjam = sdf1.format(c1.getTime());
        TxtglPinjam.setText(tglPinjam);


        Cursor cur = dbHelper.oneData(id);
        if(cur.moveToFirst()){

            // Mengambil setiap kolom yang ada di tabel database
            String idpinjam = cur.getString(cur.getColumnIndex(DBHelper.row_id));
            String nama = cur.getString(cur.getColumnIndex(DBHelper.row_nama));
            String judul = cur.getString(cur.getColumnIndex(DBHelper.row_judul));
            String pinjam = cur.getString(cur.getColumnIndex(DBHelper.row_pinjam));
            String kembali = cur.getString(cur.getColumnIndex(DBHelper.row_kembali));
            String status = cur.getString(cur.getColumnIndex(DBHelper.row_status));

            // Memasukkan data kolom pada objek form
            TxID.setText(idpinjam);
            TxNama.setText(nama);
            TxJudul.setText(judul);
            TxtglPinjam.setText(pinjam);
            TxtglKembali.setText(kembali);
            TxStatus.setText(status);

            // Validasi apakah ada objek
            if (TxID.equals("")){
                TvStatus.setVisibility(View.GONE);
                TxStatus.setVisibility(View.GONE);
                BtnProses.setVisibility(View.GONE);
            }else{
                TvStatus.setVisibility(View.VISIBLE);
                TxStatus.setVisibility(View.VISIBLE);
                BtnProses.setVisibility(View.VISIBLE);
            }

            // Validasi status untuk menampilkan tombol proses pengembalian
            if(status.equals("Dipinjam")){
                BtnProses.setVisibility(View.VISIBLE);
            }else {
                BtnProses.setVisibility(View.GONE);

                // Menonaktifkan form untuk diubah
                TxNama.setEnabled(false);
                TxJudul.setEnabled(false);
                TxtglKembali.setEnabled(false);
                TxStatus.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Membuat action menu di toolbar
        getMenuInflater().inflate(R.menu.add_menu, menu);

        // Inisialisasi status dan id dari tiap item peminjaman
        String idpinjam = TxID.getText().toString().trim();
        String status = TxStatus.getText().toString().trim();

        // Membuat tombol aksi
        MenuItem itemDelete = menu.findItem(R.id.action_delete);
        MenuItem itemClear = menu.findItem(R.id.action_clear);
        MenuItem itemSave = menu.findItem(R.id.action_save);

        // Melakukan pengecekan kondisi untuk menampilkan tombol Clear dan Delete
        if (idpinjam.equals("")){
            itemDelete.setVisible(false);
            itemClear.setVisible(true);
        }else {
            itemDelete.setVisible(true);
            itemClear.setVisible(false);
        }

        // Jika status sudah dikembalikan, tombol Simpan, Delete, dan Clear akan dihilangkan dari menu
        if(status.equals("Dikembalikan")){
            itemSave.setVisible(false);
            itemDelete.setVisible(false);
            itemClear.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                // Lakukan insert dan update jika tombol Simpan di klik
                insertAndUpdate();
        }

        switch (item.getItemId()){
            case R.id.action_clear:
                // Lakukan penghapusan isi form jika tombol Clear di klik
                TxNama.setText("");
                TxJudul.setText("");
                TxtglKembali.setText("");
        }

        switch (item.getItemId()){
            // Lakukan hapus data peminjaman jika tombol Delete di klik
            case R.id.action_delete:

                // Membuat notifikasi kalau data berhasil dihapus
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setMessage("Data ini akan dihapus");
                builder.setCancelable(true);

                // Memunculkan notifikasi ketika opsi Hapus dipilih
                builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteData(id);
                        Toast.makeText(AddActivity.this, "Terhapus", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                // Membatalkan proses ketika opsi Batal dipilih
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Memunculkan notifikasi di activity
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertAndUpdate(){
        // Untuk melakukan proses insert atau update data

        // Mengambil data dari form yang disubmit
        String idpinjam = TxID.getText().toString().trim();
        String nama = TxNama.getText().toString().trim();
        String judul = TxJudul.getText().toString().trim();
        String tglPinjam = TxtglPinjam.getText().toString().trim();
        String tglKembali = TxtglKembali.getText().toString().trim();
        String status = "Dipinjam";

        // Memasukkan data ke database
        ContentValues values = new ContentValues();

        values.put(DBHelper.row_nama, nama);
        values.put(DBHelper.row_judul, judul);
        values.put(DBHelper.row_kembali, tglKembali);
        values.put(DBHelper.row_status, status);

        // Memunculkan notifikasi jika ada form yang diisi tidak lengkap
        if (nama.equals("") || judul.equals("") || tglKembali.equals("")){
            Toast.makeText(AddActivity.this, "Isi Data Dengan Lengkap", Toast.LENGTH_SHORT).show();
        }else {
            if(idpinjam.equals("")){
                values.put(DBHelper.row_pinjam, tglPinjam);
                dbHelper.insertData(values);
            }else {
                dbHelper.updateData(values, id);
            }
            Toast.makeText(AddActivity.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
