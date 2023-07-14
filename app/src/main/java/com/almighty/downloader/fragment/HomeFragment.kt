package com.almighty.downloader.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.almighty.downloader.SearchInputActivity
import com.almighty.downloader.databinding.FragmentHomeLayoutBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editText.setOnClickListener {
            val searchInput = Intent(requireContext(), SearchInputActivity::class.java)
            val option = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                binding.editText,
                "search_input"
            )
            startActivity(searchInput, option.toBundle())
        }
    }

}