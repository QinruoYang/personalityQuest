package edu.uw.ischool.qy54.personalityquest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.navigation.fragment.findNavController

class Compare : Fragment() {

    private lateinit var mbtiRecyclerView: RecyclerView
    private lateinit var mbtiAdapter: MBTIAdapter
    private lateinit var quizViewModel: QuizViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.compare, container, false)

        mbtiRecyclerView = view.findViewById(R.id.mbtiRecyclerView)
        mbtiRecyclerView.layoutManager = LinearLayoutManager(context)
        quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

        val dividerItemDecoration = DividerItemDecoration(mbtiRecyclerView.context, DividerItemDecoration.VERTICAL)
        mbtiRecyclerView.addItemDecoration(dividerItemDecoration)

        mbtiAdapter = MBTIAdapter(emptyList()) { userResult ->
            if (quizViewModel.mbtiResult.value.isNullOrEmpty()) {
                Toast.makeText(context, "Please take the quiz first.", Toast.LENGTH_SHORT).show()
            } else {
                navigateToComparisonFragment(userResult)
            }
        }
        mbtiRecyclerView.adapter = mbtiAdapter

        fetchData()

        return view
    }

    private fun fetchData() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("results")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userResults = mutableListOf<UserResult>()
                dataSnapshot.children.forEach { snapshot ->
                    val name = snapshot.child("name").getValue(String::class.java)
                    val mbtiType = snapshot.child("mbtiType").getValue(String::class.java)
                    if (name != null && mbtiType != null) {
                        userResults.add(UserResult(name, mbtiType))
                    }
                }
                mbtiAdapter.updateData(userResults)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Failed to load data: ${databaseError.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToComparisonFragment(userResult: UserResult) {
        val bundle = Bundle().apply {
            putSerializable("userResult", userResult) // Assuming UserResult is Serializable
            putString("currentUserMBTI", quizViewModel.mbtiResult.value)
        }
        findNavController().navigate(R.id.action_navigationCompare_to_comparisonDetailFragment, bundle)
    }

}
