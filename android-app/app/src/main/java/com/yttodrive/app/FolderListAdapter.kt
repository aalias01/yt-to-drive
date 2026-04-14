package com.yttodrive.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yttodrive.app.databinding.ItemDriveFolderBinding
import com.yttodrive.app.models.DriveFolder

class FolderListAdapter(
    private val onFolderClick: (DriveFolder) -> Unit,
) : ListAdapter<DriveFolder, FolderListAdapter.Holder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemDriveFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return Holder(binding, onFolderClick)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(
        private val binding: ItemDriveFolderBinding,
        private val onFolderClick: (DriveFolder) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: DriveFolder) {
            binding.folderName.text = folder.name
            binding.root.setOnClickListener { onFolderClick(folder) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DriveFolder>() {
            override fun areItemsTheSame(a: DriveFolder, b: DriveFolder) = a.id == b.id

            override fun areContentsTheSame(a: DriveFolder, b: DriveFolder) = a == b
        }
    }
}
