package com.example.newsapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.adapters.TopicsAdapter
import com.example.newsapp.databinding.FragmentAccountBinding
import com.example.newsapp.models.TopicData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_spotlight.*
import kotlinx.android.synthetic.main.topics_item.*


class AccountFragment : Fragment(), TopicsAdapter.TaskAdapterInterface {
    private lateinit var binding: FragmentAccountBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference

    private lateinit var topicsAdapter: TopicsAdapter
    private lateinit var topicsList: MutableList<TopicData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getDataFromDb()


        //Button to add topic based on the text entered into the edit text box
        ivAddTopic.setOnClickListener {
            val topic = etTopics.text.toString()
            if (topic.isNotEmpty()) {
                addTopic(topic)
                etTopics.text = null
            } else {
                Toast.makeText(context, "Please enter a topic", Toast.LENGTH_SHORT).show()
            }
        }


        //Sign Out and go to sign in screen
        buttSignOut.setOnClickListener() {
            firebaseAuth.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    //initialise the firebase authentication, firebase data and RecyclerView
    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
            .child(firebaseAuth.currentUser?.uid.toString()).child("topics")

        binding.rvTopics.hasFixedSize()
        binding.rvTopics.layoutManager = LinearLayoutManager(context)

        topicsList = mutableListOf()
        topicsAdapter = TopicsAdapter(topicsList)
        topicsAdapter.setListener(this)
        binding.rvTopics.adapter = topicsAdapter

    }

    //Gets the data from the database to display on the topics recycler view
    private fun getDataFromDb() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                topicsList.clear()
                for (topicsSnapshot in snapshot.children) {
                    val topic = topicsSnapshot.key?.let {
                        TopicData(it, topicsSnapshot.value.toString())
                    }
                    if (topic != null) {
                        topicsList.add(topic)
                    }
                }
                topicsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //Add the topic setting both the key and value as the input, this ensures unique values only
    private fun addTopic(topic: String) {
        databaseReference.child(topic).setValue(topic)
    }


    //Remove a topic from the database
    override fun onDeleteTaskBtnClicked(topicData: TopicData) {
        databaseReference.child(topicData.topicId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }


}



