package com.example.mukundafoodsassignment.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

import androidx.recyclerview.widget.GridLayoutManager
import com.example.mukundafoodsassignment.R
import com.example.mukundafoodsassignment.adapter.AppAdapter
import com.example.mukundafoodsassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appList: List<ApplicationInfo>
    private lateinit var filteredList: List<ApplicationInfo>
    private lateinit var adapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch installed apps
        appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
        filteredList = appList

        // Setup RecyclerView
        adapter = AppAdapter(filteredList, packageManager, this)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        // Setup Search Bar
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterApps(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterApps(query: String) {
        filteredList = appList.filter {
            it.loadLabel(packageManager).toString().contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }
}
