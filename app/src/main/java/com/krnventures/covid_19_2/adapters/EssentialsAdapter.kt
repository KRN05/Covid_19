package com.krnventures.covid_19_2.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.krnventures.covid_19_2.EssentialsDiffCallback
import com.krnventures.covid_19_2.R
import com.krnventures.covid_19_2.dto.EssentialsDTO
import kotlinx.android.synthetic.main.item_rv.view.*
import kotlin.collections.ArrayList

class EssentialsAdapter(
    private val context: Context,
    private var mEssentials: List<EssentialsDTO>
) : RecyclerView.Adapter<EssentialsAdapter.MyViewHolder>() {

    private var filteredEssentials: List<EssentialsDTO> = mEssentials
    private var filtering = false


    //to inflate the layout, nothing to do with the items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false)
        return MyViewHolder(view)
    }

    fun filterEssentials(mStates: String, mCategory: String, sposition: Int, cposition: Int) {

        filtering = true
        // you are given a string of state from spinner, now tell recyclerview view that show these states only.
        val newEssentials: List<EssentialsDTO>
        if (mStates == "All States") {
            newEssentials =
                mEssentials.filter { it.category == mCategory } as ArrayList<EssentialsDTO>
        } else if (mCategory == "All Categories") {
            newEssentials = mEssentials.filter { it.state == mStates } as ArrayList<EssentialsDTO>
        }
        else {
            newEssentials =
                mEssentials.filter { it.state == mStates && it.category == mCategory } as ArrayList<EssentialsDTO>
        }
        DiffUtil.calculateDiff(EssentialsDiffCallback(newEssentials, mEssentials), false)
            .dispatchUpdatesTo(this)
        filteredEssentials = newEssentials

        if(mCategory == "All Categories" && mStates == "All States"){
            filtering = false
        }
    }

    override fun getItemCount(): Int {
        if (filtering) {
            return filteredEssentials.size
        }
        return mEssentials.size
    }

    override fun getItemViewType(position: Int): Int {

        if (filtering) {
            filteredEssentials[position].state //.type.ordinal
        } else {
            mEssentials[position].state
        }

        return position
    }

    //the place where you load this title or address
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val essentialRow: EssentialsDTO = if (filtering) {
            filteredEssentials[position]
        } else {
            mEssentials[position]
        }
        holder.title.text = essentialRow.category
        holder.address.text = essentialRow.nameoftheorganisation
        holder.contact.text = essentialRow.state
    }

    class MyViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

        val title = containerView.txt_essential_title;
        val address = containerView.txt_essential_location;
        val contact = containerView.txt_essential_contact;

    }

    fun clearFilter() {
        filtering = false
        //filteredEssentials.clear()
    }

//    fun updateEssentials(photos : List<EssentialsDTO>) {
//        DiffUtil.calculateDiff(EssentialsDiffCallback(photos, mEssentials), false).dispatchUpdatesTo(this)
//        mEssentials = photos
//        clearFilter()
//    }

//    fun removeRow(row : Int) {
//        if (filtering) {
//            filteredEssentials.removeAt(row)
//        } else {
//            mEssentials.removeAt(row)
//        }
//        notifyItemRemoved(row)
//    }

}