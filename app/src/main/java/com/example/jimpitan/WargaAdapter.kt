package com.example.jimpitan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class WargaAdapter(val mContext: Context,val layoutResId: Int, val wargaList: List<Warga>) :ArrayAdapter<Warga> (
    mContext,
    layoutResId,
    wargaList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mContext)

        val view: View = layoutInflater.inflate(layoutResId, null)

        val nama : TextView = view.findViewById(R.id.tvNama)

        val warga = wargaList[position]

        nama.text = warga.nama

        return view
    }
}