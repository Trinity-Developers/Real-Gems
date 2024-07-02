package com.trinitydevelopers.realgemsadmin.bottomfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.databinding.FragmentProfileBinding
import com.trinitydevelopers.realgemsadmin.fragments.AddDropDownFragment


class ProfileFragment : Fragment() {
private lateinit var binding:FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addDropDownData.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, AddDropDownFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}