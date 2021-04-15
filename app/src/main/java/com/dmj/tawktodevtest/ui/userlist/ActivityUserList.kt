package com.dmj.tawktodevtest.ui.userlist

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dmj.tawktodevtest.R
import com.dmj.tawktodevtest.data.models.ProfileViewModel
import com.dmj.tawktodevtest.data.models.User
import com.dmj.tawktodevtest.data.models.UserViewModel
import com.dmj.tawktodevtest.data.network.AppConfig
import com.dmj.tawktodevtest.data.network.UserService
import com.dmj.tawktodevtest.utils.CustomLinearLayoutManager
import com.dmj.tawktodevtest.utils.NetworkUtil
import com.dmj.tawktodevtest.utils.NetworkUtil.TOKEN
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_user_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream


class ActivityUserList : AppCompatActivity(), SearchView.OnQueryTextListener {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AppConfig.baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service: UserService = retrofit.create(UserService::class.java)
    private var loading = true
    private var list = arrayListOf<User>().toMutableList()
    private var listOffline = ArrayList<User>()
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mProfileViewModel: ProfileViewModel
    private lateinit var listAdapter: UserListAdapter
    private var isOnline = false
    private var pastVisiblesItems: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var isFirstLoad: Boolean = true
    private val RECORD_REQUEST_CODE: Int = 1001


    private val networkChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = context?.let { NetworkUtil.getConnectivityStatusString(it) }
            if (intent != null) {
                if ("android.net.conn.CONNECTIVITY_CHANGE" == intent.action) {
                    if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                        isOnline = false
                        textView_internetIndicator.visibility = View.VISIBLE
                    } else {
                        isOnline = true
                        textView_internetIndicator.visibility = View.GONE
                        if (baseContext != null && !isFirstLoad)
                            getUserListOnline()
                    }
                }
                isFirstLoad = false
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        return if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
            false
        } else {
            true
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), RECORD_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("ActivityUserList", "Permission has been denied by user")
                } else {
                    Log.i("ActivityUserList", "Permission has been granted by user")
                    getUserListOffline()
                    getUserListOnline()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mProfileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        listAdapter = UserListAdapter(list, this@ActivityUserList)
        edittext_search.queryHint = "Search"
        setListeners()
        recyclerView_userList.adapter = listAdapter
        recyclerView_userList.layoutManager = CustomLinearLayoutManager(this@ActivityUserList)
        recyclerView_userList.setHasFixedSize(true)
        recyclerView_userList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount =
                        (recyclerView_userList.layoutManager as LinearLayoutManager).childCount
                    totalItemCount =
                        (recyclerView_userList.layoutManager as LinearLayoutManager).itemCount
                    pastVisiblesItems =
                        (recyclerView_userList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            if (isOnline)
                                loadMore(totalItemCount)
                        }
                    }
                }
            }
        })

        getUserListOffline()
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkChangeReceiver)
    }

    private fun setListeners() {
        edittext_search.setOnQueryTextListener(this)
    }

    private fun getUserListOffline() {
        Log.e("xxx", "xxx getUserListOffline")
        if (!hasStoragePermission())
            return

        val userListLiveData: LiveData<List<User>> = mUserViewModel.getAllUsers()
        userListLiveData.observe(this) { userList ->
            userList.let {
                list.clear()
                listOffline.clear()
                Log.i(
                    "xxx",
                    "xxx getUserListOffline list size " + list.size + " listOffline size " + listOffline.size
                );
                for (user in userList) {
                    user.isOnline = isOnline
                    if (!user.notes.isNullOrEmpty())
                        Log.i(
                            "xxx",
                            "xxx getUserListOffline " + user.login + " has notes " + user.notes
                        )
                    listOffline.add(user)
                    list.add(user)
                }
                Log.i(
                    "xxx",
                    "xxx getUserListOffline list size " + list.size + " listOffline size " + listOffline.size
                );
                listAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getUserListOnline() {
        Log.e(
            "xxx",
            "xxx getUserListOnline " + "xxx list size " + list.size + " listOffline size " + listOffline.size
        )
        if (!hasStoragePermission())
            return

        val call = service.getUserList(TOKEN, 0)
        call.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                if (response.code() == 200) {
                    for (user in response.body()!!) {
                        val u = hasNotes(user.login.toString())
                        user.notes = u.notes
                        user.isOnline = isOnline
                        saveImageLocally(user)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
            }

        })
    }

    private fun loadMore(index: Int) {
        list.add(User(login = "this_is_a_loading_view"))
        val loadingIndex = list.size - 1
        listAdapter.notifyItemInserted(loadingIndex)

        val call = service.getUserList(TOKEN, index)
        call.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                if (response.code() == 200) {
                    if (loadingIndex > list.size)
                        return

                    list.removeAt(loadingIndex)
                    listAdapter.notifyItemRemoved(loadingIndex)

                    for (user in response.body()!!) {
                        val u = hasNotes(user.login.toString())
                        user.notes = u.notes
                        user.isOnline = isOnline
                        saveImageLocally(user)
                    }
                    loading = true
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
            }

        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            val mUserListLiveData = mUserViewModel.searchUser(newText)
            mUserListLiveData.observe(this) { searchedList ->
                searchedList.let {
                    list.clear()
                    for (user in searchedList) {
                        list.add(user)
                    }
                    listAdapter.notifyDataSetChanged()
                }
                mUserListLiveData.removeObservers(this@ActivityUserList)
            }
        }
        return true
    }

    private fun hasNotes(username: String): User {
        for (u in listOffline) {
            if (u.login.equals(username) && !u.notes.isNullOrEmpty())
                return u
        }
        return User()
    }

    private fun saveImageLocally(user: User): User {
        Glide.with(this@ActivityUserList)
            .asBitmap()
            .load(user.avatar_url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    var outputStream: OutputStream? = null
                    val bitmap: Bitmap = resource
                    val filepath: File =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val dir = File(filepath.absolutePath.toString() + "/Demo/")
                    dir.mkdir()
                    val file =
                        File(dir, user.login + ".jpg")
                    try {
                        outputStream = FileOutputStream(file)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream?.flush()
                    outputStream?.close()
                    user.imageLocal = file.absolutePath
                    mUserViewModel.addUser(user)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        return user
    }
}
