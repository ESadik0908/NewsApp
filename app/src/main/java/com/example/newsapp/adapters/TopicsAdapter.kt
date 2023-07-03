package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.databinding.TopicsItemBinding
import com.example.newsapp.models.TopicData
import kotlinx.android.synthetic.main.topics_item.view.*

//This adapter is used only for topic selection in the account menu
class TopicsAdapter(private val topicsList: MutableList<TopicData>) :
    RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {

    private var listener: TaskAdapterInterface? = null

    fun setListener(listener: TaskAdapterInterface) {
        this.listener = listener
    }

    inner class TopicsViewHolder(binding: TopicsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicsViewHolder {
        val binding = TopicsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicsViewHolder(binding)
    }

    //Here we set the text of each topic item
    override fun onBindViewHolder(holder: TopicsViewHolder, position: Int) {
        with(holder) {
            with(topicsList[position]) {
                itemView.tvTopic.text = this.topic

                itemView.itemDelete.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }
            }
        }

    }


    override fun getItemCount(): Int {
        return topicsList.size
    }

    //An interface is needed as we are accessing live asynchronous data
    interface TaskAdapterInterface {
        fun onDeleteTaskBtnClicked(topicData: TopicData)
    }


}