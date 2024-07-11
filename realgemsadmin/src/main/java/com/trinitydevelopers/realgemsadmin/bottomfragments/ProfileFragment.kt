package com.trinitydevelopers.realgemsadmin.bottomfragments

import android.os.Bundle
import android.provider.ContactsContract
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.databinding.FragmentProfileBinding
import com.trinitydevelopers.realgemsadmin.fragments.AddDropDownFragment
import com.trinitydevelopers.realgemsadmin.fragments.EditProfileFragment
import com.trinitydevelopers.realgemsadmin.pojos.Profile


class ProfileFragment : Fragment() {
private lateinit var binding:FragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        val text = "Real Gems"
        val spannableString = SpannableString(text)

        // Set color for "Constitution"
        val saffronColor = ContextCompat.getColor(requireContext(), R.color.red)
        spannableString.setSpan(
            ForegroundColorSpan(saffronColor),
            0,
            4,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set color for "O F"
        val blueColor = ContextCompat.getColor(requireContext(), R.color.purple)
        spannableString.setSpan(
            ForegroundColorSpan(blueColor),
            5,
            8,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the SpannableString to a TextView
        binding.textView27.text = spannableString


        // Assuming you have a user ID or use authentication to retrieve the current user's profile
        val userId = "currentUserId" // Replace with your logic to get the current user ID

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject<Profile>()
                    // Bind profile data to UI elements
                    binding.profileName.text = profile?.name
                    binding.profileContactInfo.text = profile?.contact
                    binding.profileAderess.text = profile?.address
                    // Load profile image using Picasso or Glide
                     Picasso.get().load(profile?.profileImageUrl).into(binding.profileImg)
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }

        binding.addDropDownData.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, AddDropDownFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.pofilEditIcon.setOnClickListener {

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, EditProfileFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}