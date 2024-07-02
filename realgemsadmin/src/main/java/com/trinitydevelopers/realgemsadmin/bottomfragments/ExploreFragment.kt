package com.trinitydevelopers.realgemsadmin.bottomfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.GemsAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentExploreBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class ExploreFragment : Fragment() {
    private lateinit var binding:FragmentExploreBinding
    private var gemsList: MutableList<Gems> = mutableListOf()
    private lateinit var gemsAdapter: GemsAdapter
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentExploreBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db= FirebaseFirestore.getInstance()
        setUpRecyclerView()
        loadGemsData()

    }

    private fun loadGemsData() {
        db.collection("Gems").get().addOnSuccessListener { querySnapshot ->
            gemsList.clear()
            for (document in querySnapshot.documents) {
                val gems = document.toObject(Gems::class.java)
                gems?.let {
                    gemsList.add(it)
                }
            }
            Log.d("ExploreFragment", "Gems List: $gemsList") // Log the retrieved data
            gemsAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            Log.e("ExploreFragment", "Error getting documents: ", exception) // Log any errors
        }
    }

    private fun setUpRecyclerView() {
        gemsAdapter = GemsAdapter(requireContext(), gemsList)
        binding.gemsRV.adapter = gemsAdapter
        binding.gemsRV.layoutManager = GridLayoutManager(requireContext(), 2)
    }
}