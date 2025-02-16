package com.threedev.appconvertor.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedev.appconvertor.data.models.WebBuilderApkInfo
import com.threedev.appconvertor.databinding.*
import java.util.Calendar

class AppAdapter(private val listener: AppClickListener) :
    ListAdapter<WebBuilderApkInfo, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_PENDING = 0
        private const val VIEW_TYPE_PROCESS = 1
        private const val VIEW_TYPE_COMPLETE = 2
        private const val VIEW_TYPE_PROCESS_BUNDLE = 3
        private const val VIEW_TYPE_COMPLETE_BUNDLE = 4
        private const val VIEW_TYPE_DEPLOY_PLAYSTORE = 5
    }

    private val handler = android.os.Handler()

    override fun getItemViewType(position: Int): Int {
        val app = getItem(position)
        return when (app.status) {
            0 -> VIEW_TYPE_PENDING
            1 -> VIEW_TYPE_PROCESS
            2 -> VIEW_TYPE_COMPLETE
            3 -> VIEW_TYPE_PROCESS_BUNDLE
            4 -> VIEW_TYPE_COMPLETE_BUNDLE
            5 -> VIEW_TYPE_DEPLOY_PLAYSTORE
            else -> throw IllegalArgumentException("Invalid status type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PENDING -> {
                val binding = ItemPendingAppsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PendingAppViewHolder(binding)
            }
            VIEW_TYPE_PROCESS -> {
                val binding = ItemProcessingAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProcessAppViewHolder(binding)
            }
            VIEW_TYPE_COMPLETE -> {
                val binding = ItemCompletedAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CompleteAppViewHolder(binding)
            }
            VIEW_TYPE_PROCESS_BUNDLE -> {
                val binding = ItemProcessingAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProcessBundleViewHolder(binding)
            }
            VIEW_TYPE_COMPLETE_BUNDLE -> {
                val binding = ItemCompletedAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CompleteBundleViewHolder(binding)
            }
            VIEW_TYPE_DEPLOY_PLAYSTORE -> {
                val binding = ItemDeployePlaystoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DeployPlayStoreViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val app = getItem(position)
        when (holder) {
            is PendingAppViewHolder -> holder.bind(app)
            is ProcessAppViewHolder -> holder.bind(app)
            is CompleteAppViewHolder -> holder.bind(app)
            is ProcessBundleViewHolder -> holder.bind(app)
            is CompleteBundleViewHolder -> holder.bind(app)
            is DeployPlayStoreViewHolder -> holder.bind(app)
        }
    }

    inner class PendingAppViewHolder(private val binding: ItemPendingAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: WebBuilderApkInfo) {
            binding.appName.text = app.appName
            binding.tvStatusPackage.text = app.packageName
            binding.btnstart.setOnClickListener { listener.onGetStarted(app) }
            binding.btnremove.setOnClickListener { listener.onInstructionClick(app) }
        }
    }

    inner class ProcessAppViewHolder(private val binding: ItemProcessingAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: WebBuilderApkInfo) {
            binding.appName.text = app.appName
            binding.tvStatusPackage.text = app.packageName
            startCountdown(app.delivery_date, binding.tvTimeLeft)
        }
    }

    inner class CompleteAppViewHolder(private val binding: ItemCompletedAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: WebBuilderApkInfo) {
            binding.tvAppname.text = app.appName
            binding.tvStatusPackage.text = app.packageName
            binding.btnGetApk.setOnClickListener { listener.onGetStarted(app) }
        }
    }

    inner class ProcessBundleViewHolder(private val binding: ItemProcessingAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: WebBuilderApkInfo) {
            binding.appName.text = app.appName
            binding.tvStatusPackage.text = app.packageName
            startCountdown(app.delivery_date, binding.tvTimeLeft)
        }
    }

    inner class CompleteBundleViewHolder(private val binding: ItemCompletedAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: WebBuilderApkInfo) {
            binding.tvAppname.text = app.appName
            binding.tvStatusPackage.text = app.packageName
            binding.btnGetApk.setOnClickListener { listener.onGetStarted(app) }
        }
    }

    inner class DeployPlayStoreViewHolder(private val binding: ItemDeployePlaystoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(app: WebBuilderApkInfo) {
            binding.tvAppname.text = app.appName
            binding.tvStatusPackage.text = app.packageName
        }
    }

    private fun startCountdown(deliveryDate: Long, timeLeftView: android.widget.TextView) {
        val updateTime = object : Runnable {
            override fun run() {
                val currentTime = Calendar.getInstance().timeInMillis
                val leftTime = deliveryDate - currentTime

                if (leftTime > 0) {
                    val hours = (leftTime / (1000 * 60 * 60)) % 24
                    val minutes = (leftTime / (1000 * 60)) % 60
                    val seconds = (leftTime / 1000) % 60
                    timeLeftView.text = "$hours hrs $minutes mins $seconds secs left"
                    handler.postDelayed(this, 1000)
                } else {
                    timeLeftView.text = "Time expired"
                }
            }
        }
        handler.post(updateTime)
    }

    class DiffCallback : DiffUtil.ItemCallback<WebBuilderApkInfo>() {
        override fun areItemsTheSame(oldItem: WebBuilderApkInfo, newItem: WebBuilderApkInfo): Boolean {
            return oldItem.appName == newItem.appName
        }

        override fun areContentsTheSame(oldItem: WebBuilderApkInfo, newItem: WebBuilderApkInfo): Boolean {
            return oldItem == newItem
        }
    }

    interface AppClickListener {
        fun onInstructionClick(app: WebBuilderApkInfo)
        fun onGetStarted(app: WebBuilderApkInfo)
    }
}
