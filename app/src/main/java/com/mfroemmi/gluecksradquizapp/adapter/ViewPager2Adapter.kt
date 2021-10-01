package com.mfroemmi.gluecksradquizapp.adapter

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.mfroemmi.gluecksradquizapp.R
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel

class ViewPager2Adapter(
    var context: Context,
    var name: String,
    var item: QuestionSetModel,
    var questionSetLiveData: MutableLiveData<QuestionSetModel>,
    var loadOrDeleteLiveData: MutableLiveData<String>
) : RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>() {

    class ViewHolder(val context: Context, view: View, var name: String, var item: QuestionSetModel, var questionSetLiveData: MutableLiveData<QuestionSetModel>, var loadOrDeleteLiveData: MutableLiveData<String>) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var button: Button = view.findViewById(R.id.bt_load_delete)
        var arrow: ImageView = view.findViewById(R.id.iv_arrow_left)
        var layout: ConstraintLayout = view.findViewById(R.id.cl_load_delete)

        init {
            button.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            if (position == 0) {
                showLoadDialog()
            }
            if (position == 1) {
                showDeleteDialog()
            }
        }

        private fun showLoadDialog() {
            // Aufruf eines Dialogs zum Laden eines Fragensatzes
            val dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_load_question_set)
            dialog.findViewById<TextView>(R.id.question_set_load).text = item.name
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                loadOrDeleteLiveData.value = "load"
                questionSetLiveData.value = item
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.btn_no).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun showDeleteDialog() {
            // Aufruf eines Dialogs zum Löschen eines Fragensatzes
            val dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_delete_question_set)
            dialog.findViewById<TextView>(R.id.question_set_delete).text = item.name
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                loadOrDeleteLiveData.value = "delete"
                questionSetLiveData.value = item
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.btn_no).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.load_delete_item, parent, false)
        return ViewHolder(context, view, name, item, questionSetLiveData, loadOrDeleteLiveData)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.button.text = "Laden"
            holder.layout.setBackgroundResource(R.color.white)
            holder.arrow.visibility = View.VISIBLE
        }
        if (position == 1) {
            holder.button.text = "Löschen"
            holder.layout.setBackgroundResource(R.color.primary_red)
            holder.arrow.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return 2
    }



}