package com.trinitydevelopers.realgemsadmin.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.adapter.SearchAdapter
import com.trinitydevelopers.realgemsadmin.databinding.FragmentSearchBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class SearchFragment : Fragment() {
    private lateinit var bindingSearch: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private var gemsList = mutableListOf<Gems>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindingSearch=FragmentSearchBinding.inflate(inflater,container,false)
        return bindingSearch.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        bindingSearch.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchTerm = s.toString().trim().lowercase()
                if (searchTerm.isNotEmpty()) {
                    performSearch(searchTerm)
                } else {
                    displayEmptyState()
                }
            }
        })
    }


    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(requireContext(), gemsList)
        bindingSearch.searchRV.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
        }
    }

    private fun performSearch(searchTerm: String) {
        firestore.collection("gems")
            .whereEqualTo("nameId", searchTerm)
            .get()
            .addOnSuccessListener { documents ->

                Log.d("SearchFragment", "Searching for term: $searchTerm")
                gemsList.clear()
                for (document in documents) {
                    val gem = document.toObject(Gems::class.java)
                    gemsList.add(gem)
                }
                if (gemsList.isEmpty()) {
                    displayEmptyState()
                } else {
                    displaySearchResults()
                    searchAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                // Handle failures gracefully
                displayEmptyState()
            }
    }

    private fun displaySearchResults() {
        bindingSearch.searchRV.visibility = View.VISIBLE
        bindingSearch.textViewEmpty.visibility = View.GONE
    }

    private fun displayEmptyState() {
        bindingSearch.searchRV.visibility = View.GONE
        bindingSearch.textViewEmpty.visibility = View.VISIBLE
    }
}