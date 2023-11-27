package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Compare : Fragment() {

    private lateinit var mbtiRecyclerView: RecyclerView
    private lateinit var mbtiAdapter: MBTIAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.compare, container, false)

        mbtiRecyclerView = view.findViewById(R.id.mbtiRecyclerView)
        mbtiRecyclerView.layoutManager = LinearLayoutManager(context)

        val dividerItemDecoration = DividerItemDecoration(mbtiRecyclerView.context, DividerItemDecoration.VERTICAL)
        mbtiRecyclerView.addItemDecoration(dividerItemDecoration)

        mbtiAdapter = MBTIAdapter(emptyList()) { userResult ->
            // Handle item click, toast fgor now
            Toast.makeText(context, "MBTI Type: ${userResult.mbtiType}", Toast.LENGTH_SHORT).show()
        }
        mbtiRecyclerView.adapter = mbtiAdapter

//        fetchData()
        loadMockData()

        return view
    }

    private fun fetchData() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("userResults")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userResults = mutableListOf<UserResult>()
                dataSnapshot.children.forEach { snapshot ->
                    val userResult = snapshot.getValue(UserResult::class.java)
                    userResult?.let { userResults.add(it) }
                }
                mbtiAdapter.updateData(userResults)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // some err handling
            }
        })
    }

    private fun loadMockData() {
        val mockData = listOf(
            UserResult(name = "Alice", mbtiType = "INTJ", percentages = mapOf("I" to 60, "N" to 70, "T" to 80, "J" to 75)),
            UserResult(name = "Bob", mbtiType = "ENFP", percentages = mapOf("E" to 65, "N" to 55, "F" to 60, "P" to 70)),
        )
        mbtiAdapter.updateData(mockData)
    }
}
