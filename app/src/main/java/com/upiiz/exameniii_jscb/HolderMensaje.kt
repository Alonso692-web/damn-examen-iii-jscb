package com.upiiz.exameniii_jscb

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class HolderMensaje (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvMensaje: TextView = itemView.findViewById(R.id.tv_mensaje)
    val tvHora: TextView = itemView.findViewById(R.id.tv_hora)
    val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_usuario)
    val ivFoto: CircleImageView = itemView.findViewById(R.id.iv_foto_usuario)
    val ivFotoEnviada: ImageView = itemView.findViewById(R.id.iv_foto_Enviada)






}