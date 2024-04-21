package com.mamh.smartwardrobe.ui.cloth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mamh.smartwardrobe.databinding.FragmentClothBinding


@Suppress("NAME_SHADOWING")
class ClothFragment : Fragment() {
    //本fragment的view model
    private lateinit var clothViewModel: ClothViewModel

    private var _binding: FragmentClothBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClothBinding.inflate(inflater, container, false)

        clothViewModel =
            ViewModelProvider(this, ClothViewModelFactory(requireActivity().application))
                .get(ClothViewModel::class.java)

        binding.viewmodel = clothViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }


}