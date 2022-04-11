package com.hcl.kandy.cpaas.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.hcl.kandy.cpaas.R
import com.hcl.kandy.cpaas.databinding.LayoutProgressBinding

class ProgressUtils(private val context: Context) {
    var builder = AlertDialog.Builder(context)
    var alertDialog: AlertDialog? = null
    fun showDialog() {
        val inflater = LayoutInflater.from(context)
        val binding: LayoutProgressBinding? =
            DataBindingUtil.inflate(inflater, R.layout.layout_progress, null, false)
        binding?.ivLoader?.let {
            Glide.with(context)
                .load(R.drawable.loader)
                .into(it)
        }
        builder.setView(binding?.root)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog?.show()
        alertDialog?.window?.setLayout(
            convertDpToPixel(90F, context).toInt(),
            convertDpToPixel(90F, context).toInt()
        )
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun dismissDialog() {
        alertDialog?.dismiss()
    }
}