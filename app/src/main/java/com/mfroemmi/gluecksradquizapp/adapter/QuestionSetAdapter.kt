package com.mfroemmi.gluecksradquizapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mfroemmi.gluecksradquizapp.R
import com.mfroemmi.gluecksradquizapp.model.QuestionSetModel

class QuestionSetAdapter(
    private var context: Context,
    private val questionSetArrayList: ArrayList<QuestionSetModel>,
    private val questionSetLiveData: MutableLiveData<QuestionSetModel>,
    private var loadOrDeleteLiveData: MutableLiveData<String>
) : RecyclerView.Adapter<QuestionSetAdapter.QuestionSetViewHolder>() {

    class QuestionSetViewHolder(
        val context: Context,
        view: View,
        var questionSetLiveData: MutableLiveData<QuestionSetModel>,
        var loadOrDeleteLiveData: MutableLiveData<String>
    ) : RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = view.findViewById(R.id.question_set_name)
        val dateTextView: TextView = view.findViewById(R.id.question_set_date)

        // ViewPager2
        var viewPager: ViewPager2 = view.findViewById(R.id.vp_load_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionSetViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_set_item, parent, false)

        return QuestionSetViewHolder(context, adapterLayout, questionSetLiveData, loadOrDeleteLiveData)
    }

    override fun onBindViewHolder(holder: QuestionSetViewHolder, position: Int) {
        val item = questionSetArrayList[position]
        holder.nameTextView.text = item.name
        holder.dateTextView.text = item.date

        // ViewPager2
        val itemViewPager = initViewPager2(context, holder.itemView, item.name, item, holder.questionSetLiveData, holder.loadOrDeleteLiveData)
        holder.viewPager = itemViewPager
    }

    override fun getItemCount(): Int {
        return questionSetArrayList.size
    }

    private fun initViewPager2(context: Context, adapterLayout: View, name: String, item: QuestionSetModel, questionSetLiveData: MutableLiveData<QuestionSetModel>, loadOrDeleteLiveData: MutableLiveData<String>): ViewPager2 {
        val viewPager: ViewPager2 = adapterLayout.findViewById(R.id.vp_load_delete)
        val adapter = ViewPager2LoadDeleteAdapter(context, name, item, questionSetLiveData, loadOrDeleteLiveData)
        viewPager.adapter = adapter

        return viewPager
    }
}