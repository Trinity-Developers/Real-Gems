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
import com.trinitydevelopers.realgemsadmin.fragments.AddDropDownFragment
import com.trinitydevelopers.realgemsadmin.fragments.SearchFragment
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class ExploreFragment : Fragment() {
    private lateinit var binding:FragmentExploreBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var gemsAdapter: GemsAdapter
    private var gemsList = mutableListOf<Gems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentExploreBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        setupRecyclerView()
        fetchGemsData()
        binding.edtExploreSearch.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, SearchFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    private fun setupRecyclerView() {
        gemsAdapter = GemsAdapter(requireContext(), gemsList)
        binding.gemsRV.layoutManager = GridLayoutManager(context, 2)
        binding.gemsRV.adapter = gemsAdapter
    }
    private fun fetchGemsData() {
        firestore.collection("gems")
            .get()
            .addOnSuccessListener { documents ->
                gemsList.clear()
                for (document in documents) {
                    val gem = document.toObject(Gems::class.java)
                    gemsList.add(gem)
                }
                gemsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ExploreFragment", "Error getting documents: ", exception)
            }
    }
}