package com.krnventures.covid_19_2

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.krnventures.covid_19_2.dto.EssentialsDTO

class EssentialsDiffCallback(private val newRows : List<EssentialsDTO>, private val oldRows : List<EssentialsDTO>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRow = oldRows[oldItemPosition]
        val newRow = newRows[newItemPosition]
        return oldRow.state == newRow.state
    }

    override fun getOldListSize(): Int = oldRows.size

    override fun getNewListSize(): Int = newRows.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRow = oldRows[oldItemPosition]
        val newRow = newRows[newItemPosition]
        return oldRow == newRow
    }
}