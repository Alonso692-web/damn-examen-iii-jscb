package com.upiiz.exameniii_jscb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth



class MessageAdapter : RecyclerView.Adapter<HolderMensaje>() {

    val messages: MutableList<Message> = mutableListOf()


     fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)


    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderMensaje {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_mensajes, parent, false)
        return HolderMensaje(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: HolderMensaje, position: Int) {
        val message = messages[position]
        holder.tvMensaje.text = message.mensaje
        holder.tvHora.text = message.hora
        holder.tvNombre.text = message.nombre
        if (message.type_mensaje == "image") {
            holder.ivFotoEnviada.visibility = View.VISIBLE
            holder.tvMensaje.visibility = View.GONE
            Glide.with(holder.itemView.context).load(message.foto).into(holder.ivFotoEnviada)

        }
        else {
            holder.ivFotoEnviada.visibility = View.GONE
            holder.tvMensaje.visibility = View.VISIBLE
        }

    }




}