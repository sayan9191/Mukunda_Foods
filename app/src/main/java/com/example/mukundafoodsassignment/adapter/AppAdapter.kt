package com.example.mukundafoodsassignment.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mukundafoodsassignment.databinding.ItemAppBinding


class AppAdapter(
    private var appList: List<ApplicationInfo>,
    private val packageManager: PackageManager,
    private val context: Context
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    inner class AppViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = appList[position]
        holder.binding.apply {
            appIcon.setImageDrawable(appInfo.loadIcon(packageManager))
            appName.text = appInfo.loadLabel(packageManager).toString()

            // Launch app
            root.setOnClickListener {
                val intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
                if (intent != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Unable to launch app", Toast.LENGTH_SHORT).show()
                }
            }

            // Uninstall app
            btnUninstall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:${appInfo.packageName}")
                context.startActivity(intent)

                // Delay to allow system dialog action to complete, then refresh the app list
                root.postDelayed({ refreshAppList() }, 3000)
            }

            // Update app
            btnUpdate.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=${appInfo.packageName}")
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = appList.size

    private fun refreshAppList() {
        val updatedList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // Exclude system apps
        appList = updatedList
        notifyDataSetChanged()
    }

    fun updateList(newList: List<ApplicationInfo>) {
        appList = newList
        notifyDataSetChanged()
    }
}