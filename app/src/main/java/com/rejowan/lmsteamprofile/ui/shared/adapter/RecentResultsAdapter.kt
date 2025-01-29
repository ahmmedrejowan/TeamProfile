package com.rejowan.lmsteamprofile.ui.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rejowan.lmsteamprofile.R
import com.rejowan.lmsteamprofile.data.remote.response.MatchResult
import com.rejowan.lmsteamprofile.databinding.ItemSingleMatchBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecentResultsAdapter(private val list: List<MatchResult>) : RecyclerView.Adapter<RecentResultsAdapter.MatchViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecentResultsAdapter.MatchViewHolder {
        val binding = ItemSingleMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentResultsAdapter.MatchViewHolder, position: Int) {
        val result = list[position]

        holder.binding.teamOneName.text = result.teamName
        holder.binding.teamTwoName.text = result.oppTeamName

        result.teamLogo?.let {
            Glide.with(holder.binding.root.context).load(it).placeholder(R.drawable.img_placeholder_portrait)
                .error(R.drawable.img_placeholder_portrait).into(holder.binding.teamOneImage)
        }

        result.oppLogo?.let {
            Glide.with(holder.binding.root.context).load(it).placeholder(R.drawable.img_placeholder_portrait)
                .error(R.drawable.img_placeholder_portrait).into(holder.binding.teamTwoImage)
        }

        try {


            val initDate = result.dateTime

            initDate?.let { iDate ->
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date: Date? = inputFormat.parse(iDate)
                val formattedDate = outputFormat.format(date!!)
                holder.binding.date.text = formattedDate
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val resultString = result.matchInfo

        resultString?.let { mRs ->

            if (mRs.contains(result.teamName.toString())) {
                holder.binding.teamOneWin.visibility = ViewGroup.VISIBLE
            }
            if (mRs.contains(result.oppTeamName.toString())) {
                holder.binding.teamTwoWin.visibility = ViewGroup.VISIBLE
            }
            holder.binding.result.text = mRs
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MatchViewHolder(val binding: ItemSingleMatchBinding) : RecyclerView.ViewHolder(binding.root)

}