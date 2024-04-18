package com.mamh.smartwardrobe.ui.cloth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.mamh.smartwardrobe.R


@Suppress("NAME_SHADOWING")
class ClothFragment : Fragment() {
    //本fragment的view model
    private lateinit var clothViewModel: ClothViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_cloth, container, false)
    }


}