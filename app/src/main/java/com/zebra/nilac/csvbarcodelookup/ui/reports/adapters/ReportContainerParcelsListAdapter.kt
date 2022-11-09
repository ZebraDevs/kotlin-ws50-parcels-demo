package com.zebra.nilac.csvbarcodelookup.ui.reports.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zebra.nilac.csvbarcodelookup.databinding.ReportContainerParcelsCountRowBinding
import com.zebra.nilac.csvbarcodelookup.databinding.ReportParcelsCountRowBinding
import com.zebra.nilac.csvbarcodelookup.models.ReportContainer
import com.zebra.nilac.csvbarcodelookup.models.StoredParcel

class ReportContainerParcelsListAdapter(
    var list: List<StoredParcel>,
    var callbacks: CallBacks?
) : RecyclerView.Adapter<ReportContainerParcelsListAdapter.ViewHolder>() {

    private lateinit var mInflater: LayoutInflater
    private lateinit var mContext: Context

    private var mParcels: List<StoredParcel> =
        if (list.isNotEmpty()) list.toMutableList() else ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mContext = parent.context
        mInflater = LayoutInflater.from(mContext)

        val view = ReportParcelsCountRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val container = mParcels[position]

        holder.mBinder.barcode.text = container.parcelBarcode

        holder.mBinder.root.setOnClickListener {
            callbacks?.onSelectedParcel(container)
        }
    }

    override fun getItemCount(): Int {
        return mParcels.size
    }

    fun notifyAdapter(items: List<StoredParcel>) {
        mParcels = items
        notifyDataSetChanged()
    }

    fun removeCallBacks() {
        this.callbacks = null
    }

    class ViewHolder internal constructor(binding: ReportParcelsCountRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var mBinder = binding
    }

    interface CallBacks {
        fun onSelectedParcel(parcel: StoredParcel)
    }
}