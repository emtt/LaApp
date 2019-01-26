package laapp.emt.com.core.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import laapp.emt.com.core.model.Contacto;

@Dao
public interface ContactoDao {
    String TABLE_NAME = "contacto";

    @Query("SELECT * FROM " + TABLE_NAME + " ORDER BY nombre ASC")
    LiveData<List<Contacto>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME)
    List<Contacto> getContactos();

    @Query("SELECT * FROM " + TABLE_NAME + " where telefono = :telefono LIMIT 1")
    LiveData<Contacto> findByTelefono(String telefono);

    @Query("SELECT * FROM " + TABLE_NAME + " where uid = :uid LIMIT 1")
    LiveData<Contacto> findById(String uid);

    @Insert
    void insert(Contacto contacto);

    @Update
    void update(Contacto contacto);

    @Delete
    void delete(Contacto contacto);

}
