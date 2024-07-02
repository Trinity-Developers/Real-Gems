package com.trinitydevelopers.realgemsadmin.bottomfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.databinding.FragmentAddBinding
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            val name=binding.edtGemStoneName.text.toString()
            val cut=binding.edtGemsCut.text.toString()
            val origin=binding.edtGemsOrigin.text.toString()
            val shape=binding.edtGemsShape.text.toString()
            val type=binding.edtGemsTypes.text.toString()
            val composition=binding.edtGemsComposition.text.toString()
            val treatment=binding.edtGemsTreatment.text.toString()
            val color=binding.edtGemsColor.text.toString()
            val carats=binding.edtGemsCarates.text.toString()

            if (name.isEmpty()||cut.isEmpty()||origin.isEmpty()||shape.isEmpty()||
                type.isEmpty()||composition.isEmpty()||treatment.isEmpty()||color.isEmpty()||carats.isEmpty()) {
                Toast.makeText(requireContext(), "please fill all details", Toast.LENGTH_SHORT).show()
            }
            else{
                val gemsData = Gems(name, cut, origin, shape, type, composition, treatment, color, carats)
                Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                val db = FirebaseFirestore.getInstance()
                db.collection("Gems").add(gemsData).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Added Successfully", Toast.LENGTH_SHORT)
                        .show()
                }.addOnFailureListener {
                    Log.e("gems", "$gemsData")
                    Toast.makeText(requireContext(), "Failed to add Gems", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}