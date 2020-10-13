package com.example.jimpitan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

//    private lateinit var etNama : EditText
//    private lateinit var btnSave : Button

    private lateinit var ref: DatabaseReference
    private lateinit var wargaList: MutableList<Warga>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ref = FirebaseDatabase.getInstance().getReference("warga")

//        etNama = findViewById(R.id.etNama)
//        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener(this)

        wargaList = mutableListOf()

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    wargaList.clear()
                    for (h in p0.children){
                        val warga = h.getValue(Warga::class.java)
                        if (warga != null) {
                            wargaList.add(warga)
                        }
                    }

                    val adapater = WargaAdapter(applicationContext, R.layout.item_warga, wargaList)
                    lvWarga.adapter = adapater
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    override fun onClick(p0: View?) {
        saveData()
    }

    private fun saveData() {
        val nama = etNama.text.toString().trim()

        if (nama.isEmpty()){
            etNama.error = "Isi Nama"
            return
        }



        val wargaId = ref.push().key

        val warga = Warga(wargaId,nama)

        if (wargaId != null) {
            ref.child(wargaId).setValue(warga).addOnCompleteListener {
                Toast.makeText(applicationContext, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}