package com.inauth.codechallenge.tasks

import android.Manifest
import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.inauth.codechallenge.R
import com.inauth.codechallenge.databinding.TasksFragBinding
import com.inauth.codechallenge.util.hideKeyboard
import com.inauth.codechallenge.util.setupSnackbar


/**
 * After the user searches for a GIF, displays a grid of GIFs if there is any.
 */

class TasksFragment : Fragment() {

    private val codePermissionLocation = 101
    private lateinit var viewDataBinding: TasksFragBinding

    companion object {
        fun newInstance() = TasksFragment()
        private const val TAG = "TasksFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewDataBinding = TasksFragBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as TasksActivity).obtainViewModel()
        }
        setHasOptionsMenu(false)

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupGetPrettyJsonButton()
        setupGetLocationInfoButton()
        setupGetAppsInfoButton()
    }

    private fun setupGetPrettyJsonButton() {
        viewDataBinding.getPrettyJson.let {
            it.setOnClickListener { view ->
                viewDataBinding.viewmodel?.search()
                view.hideKeyboard()
            }
        }
    }

    private fun setupGetLocationInfoButton() {
        viewDataBinding.getLocationInfo.let {
            it.setOnClickListener { view ->
                if (Build.VERSION.SDK_INT >= 23) {
                    if (isPermissionGranted(
                            view.context,
                            codePermissionLocation,
                            R.string.message_location_requirement,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        viewDataBinding.viewmodel?.getLastLocationInfo()
                    }
                }
                else {
                    viewDataBinding.viewmodel?.getLastLocationInfo()
                }

                view.hideKeyboard()
            }
        }
    }

    private fun setupGetAppsInfoButton() {
        viewDataBinding.getAppsInfo.let {
            it.setOnClickListener { view ->
                if (Build.VERSION.SDK_INT >= 23) {
                    val granted: Boolean
                    val appOps = view.context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    val mode =
                        appOps.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            android.os.Process.myUid(), view.context.packageName
                        )

                    granted = if (mode == AppOpsManager.MODE_DEFAULT) {
                        view.context.checkCallingOrSelfPermission(
                            Manifest.permission.PACKAGE_USAGE_STATS) === PackageManager.PERMISSION_GRANTED
                    } else {
                        mode == AppOpsManager.MODE_ALLOWED
                    }
                    if(granted) {
                        viewDataBinding.viewmodel?.getApplicationsInfo()
                    }
                    else{
                        val dialog =
                            AlertDialog.Builder(view.context)
                                .setTitle(view.context.getString(R.string.title_requirement))
                                .setMessage(view.context.getString(R.string.message_usage_stats_requirement))
                                .setPositiveButton(view.context.getString(R.string.action_continue)) { _, _ ->
                                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                }
                                .setNegativeButton(view.context.getString(R.string.action_cancel)) { _, _ -> }
                                .create()
                        dialog.show()
                    }
                }
                else {
                    viewDataBinding.viewmodel?.getApplicationsInfo()
                }

                view.hideKeyboard()
            }
        }
    }

    private fun isPermissionGranted(context: Context, reqCode: Int, @StringRes message: Int, vararg perm: String) : Boolean {
        val permissions = perm.toList().all {
            ContextCompat.checkSelfPermission(context,it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!permissions) {
            if(perm.toList().any {
                    shouldShowRequestPermissionRationale(it)
                }) {
                val dialog =
                    AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.title_requirement))
                        .setMessage(context.getString(message))
                        .setPositiveButton(context.getString(R.string.action_continue)) { _, _ ->
                            requestPermissions(perm, reqCode)
                        }
                        .setNegativeButton(context.getString(R.string.action_cancel)) { _, _ -> }
                        .create()
                dialog.show()
            } else {
                requestPermissions(perm, reqCode)
            }
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            codePermissionLocation -> {
                if(!grantResults.toList().any{ it != PackageManager.PERMISSION_GRANTED }){
                    viewDataBinding.viewmodel?.getLastLocationInfo()
                }
            }
        }
    }

}