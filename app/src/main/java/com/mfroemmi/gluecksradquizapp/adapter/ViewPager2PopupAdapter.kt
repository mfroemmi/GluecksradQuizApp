package com.mfroemmi.gluecksradquizapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mfroemmi.gluecksradquizapp.R

class ViewPager2PopupAdapter(var currentQuestion: ArrayList<String>) : RecyclerView.Adapter<ViewPager2PopupAdapter.ViewHolder>() {

    class ViewHolder(view: View, private var currentQuestion: ArrayList<String>) : RecyclerView.ViewHolder(view) {
        var popupText: TextView = view.findViewById(R.id.tv_popup_question)
        var popupArrowLeft: ImageView = view.findViewById(R.id.iv_popup_arrow)
        var popupArrowRight: ImageView = view.findViewById(R.id.iv_popup_arrow_right)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.question_answer_item, parent, false)
        return ViewHolder(view, currentQuestion)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.popupText.text = currentQuestion[0]
            holder.popupArrowLeft.visibility = View.VISIBLE
            holder.popupArrowRight.visibility = View.GONE
        }
        if (position == 1) {
            holder.popupText.text = currentQuestion[1]
            holder.popupArrowLeft.visibility = View.GONE
            holder.popupArrowRight.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}