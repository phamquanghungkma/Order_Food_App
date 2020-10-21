package com.tofukma.orderapp.View.RestaurantUI

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tofukma.orderapp.R
import com.tofukma.orderapp.ViewModel.restaurant.RestaurantViewModel

class RestaurantFragment : Fragment() {

    companion object {
        fun newInstance() =
            RestaurantFragment()
    }

    private lateinit var viewModel: RestaurantViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurant, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
        // TODO: Use the ViewModel
    }

}