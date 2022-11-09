package com.zebra.nilac.csvbarcodelookup.ui.reports.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zebra.nilac.csvbarcodelookup.databinding.ReportContainerParcelsCountRowBinding
import com.zebra.nilac.csvbarcodelookup.models.ReportContainer

class ReportsSummaryListAdapter(
    list: List<ReportContainer>,
    private var callbacks: CallBacks?
) : RecyclerView.Adapter<ReportsSummaryListAdapter.ViewHolder>() {

    private lateinit var mInflater: LayoutInflater
    private lateinit var mContext: Context

    private var mContainers: List<ReportContainer> =
        if (list.isNotEmpty()) list.toMutableList() else ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mContext = parent.context
        mInflater = LayoutInflater.from(mContext)

        val view = ReportContainerParcelsCountRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val container = mContainers[position]

        holder.mBinder.containerName.text = container.name
        holder.mBinder.parcelsCount.text = container.parcelsCount.toString()

        holder.mBinder.root.setOnClickListener {
            callbacks?.onSelectedContainer(container)
        }
    }

    override fun getItemCount(): Int {
        return mContainers.size
    }

    fun notifyAdapter(items: List<ReportContainer>) {
        mContainers = items
        notifyDataSetChanged()
    }

    fun removeCallBacks() {
        this.callbacks = null
    }

    class ViewHolder internal constructor(binding: ReportContainerParcelsCountRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var mBinder = binding
    }

    interface CallBacks {
        fun onSelectedContainer(container: ReportContainer)
    }
}