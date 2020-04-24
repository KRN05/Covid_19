package com.krnventures.covid_19_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.krnventures.covid_19_2.adapters.EssentialsAdapter
import com.krnventures.covid_19_2.dto.EssentialsDTO
import com.krnventures.covid_19_2.dto.EssentialsListDTO
import com.krnventures.covid_19_2.network.ApiInterface
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private var mAdapter: EssentialsAdapter? = null
    private var mEssentials: MutableList<EssentialsDTO> = ArrayList()
    private var currentCategoryPosition = 0
    private var currentStatePosition = 0
    private var currentState="All States"
    private var currentCategory="All Categories"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_essentials.layoutManager = LinearLayoutManager(this)

        setupStatesSpinner(spinner)
        setupCategorySpinner(spinner2)

        buttonGo.setOnClickListener {
            intent = Intent(applicationContext, StatsActivity::class.java)
            startActivity(intent)
        }

        apiCall()

    }

    private fun apiCall() {
        mAdapter = EssentialsAdapter(this, mEssentials)
        rv_essentials.adapter = mAdapter

        val apiInterface = ApiInterface.create().getEssentials()

        apiInterface.enqueue(
            object : Callback<EssentialsListDTO> {
                override fun onFailure(call: Call<EssentialsListDTO>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<EssentialsListDTO>,
                    response: Response<EssentialsListDTO>
                ) {

                    val essentialsItems = response.body()
                    if (essentialsItems != null) {
                        mEssentials.addAll(essentialsItems.resources!!)
                        mAdapter!!.notifyDataSetChanged()
                    }
                }

            }
        )
    }

    private fun setupStatesSpinner(spinner: Spinner) {
        // Camera spinner
        val essentialsStrings = resources.getStringArray(R.array.spinner_states)
        val spinnerEssentialsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_states,
            android.R.layout.simple_spinner_item
        )
        spinnerEssentialsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerEssentialsAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if(rv_essentials.adapter!=null && currentStatePosition!=position) {
                    (rv_essentials.adapter as EssentialsAdapter).filterEssentials(essentialsStrings[position], currentCategory)
                    currentState=essentialsStrings[position]
                    currentStatePosition=position
                }
                currentStatePosition = position
            }
        }
    }

    private fun setupCategorySpinner(spinner: Spinner) {
        // Camera spinner
        val essentialsStrings = resources.getStringArray(R.array.spinner_category)
        val spinnerEssentialsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_category,
            android.R.layout.simple_spinner_item
        )
        spinnerEssentialsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerEssentialsAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if(rv_essentials.adapter!=null && currentCategoryPosition!=position) {
                    (rv_essentials.adapter as EssentialsAdapter).filterEssentials(currentState, essentialsStrings[position])
                    currentCategory=essentialsStrings[position]
                    currentCategoryPosition=position
                }
                currentCategoryPosition = position
            }
        }
    }
}

