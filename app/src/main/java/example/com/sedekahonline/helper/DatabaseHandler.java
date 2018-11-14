package example.com.sedekahonline.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import example.com.sedekahonline.model.Sedekah;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sedekahonline";
    private static final String TABEL_SEDEKAH = "sedekah";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTabelSedekah = "CREATE TABLE " + TABEL_SEDEKAH +
                "( id_sedekah INTEGER PRIMARY KEY, nama TEXT, nohp TEXT, email TEXT, " +
                "alamat TEXT, gambar TEXT, latitude TEXT, longitude TEXT)";

        db.execSQL(queryTabelSedekah);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABEL_SEDEKAH);
    }

    public void tambahSedekah(Sedekah s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nama", s.getNama());
        values.put("nohp", s.getNoHp());
        values.put("email", s.getEmail());
        values.put("alamat", s.getAlamat());
        values.put("gambar", s.getGambar());
        values.put("latitude", s.getLatitude());
        values.put("longitude", s.getLongitude());
        db.insert(TABEL_SEDEKAH, null, values);
        db.close();
    }

    public List<Sedekah> daftarSedekah() {
        List<Sedekah> daftar = new ArrayList<>();
        String query = "SELECT * FROM " + TABEL_SEDEKAH + " ORDER BY nama";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Sedekah s = new Sedekah(
                        c.getInt(0),c.getString(1), c.getString(2), c.getString(3), c.getString(4),
                        c.getBlob(5), c.getString(6), c.getString(7));
                daftar.add(s);
            } while (c.moveToNext());
        }
        return daftar;
    }


    public Sedekah detailSedekah(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABEL_SEDEKAH + " WHERE id_sedekah = '"
                + id + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null)
            c.moveToFirst();
        Sedekah s = new Sedekah(
                c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                c.getString(4), c.getBlob(5), c.getString(6), c.getString(7));
        return s;
    }

    public void tambahData() {

        tambahSedekah(new Sedekah("Data1", "0812345678916", "data1@test.com", "-7.7379662,110.3964295","-7.7379662","110.3964295"));
        tambahSedekah(new Sedekah("Data2", "0812345678927", "data2@test.com", "-7.7379662,110.3942408","-7.7379662","110.3942408"));
        tambahSedekah(new Sedekah("Data3", "0812345678938", "data3@test.com", "-7.7522484,110.3989306","-7.7522484","110.3989306"));
        tambahSedekah(new Sedekah("Data4", "0812345678949", "data4@test.com", "-7.818623,110.374634","-7.818623","110.374634"));
        tambahSedekah(new Sedekah("Data 5", "0812345678950", "data5@test.com", "-7.8261753,110.2905731","-7.8261753","110.2905731"));


    }


}
