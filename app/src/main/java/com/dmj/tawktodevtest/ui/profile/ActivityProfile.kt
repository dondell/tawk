package com.dmj.tawktodevtest.ui.profile

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dmj.tawktodevtest.R
import com.dmj.tawktodevtest.data.models.Profile
import com.dmj.tawktodevtest.data.models.ProfileViewModel
import com.dmj.tawktodevtest.data.models.User
import com.dmj.tawktodevtest.data.network.AppConfig
import com.dmj.tawktodevtest.data.network.UserService
import com.dmj.tawktodevtest.utils.BitmapHelper.invertColors
import com.dmj.tawktodevtest.utils.NetworkUtil.TOKEN
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActivityProfile : AppCompatActivity() {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AppConfig.baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service: UserService = retrofit.create(UserService::class.java)
    private lateinit var mProfileViewModel: ProfileViewModel
    var user: User? = User()
    var profile: Profile? = Profile()
    private var isOnline: Boolean = false
    private var isInvertImage: Boolean = false
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mProfileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        if (null != intent) {
            user = intent.getParcelableExtra("user_data")
            isOnline = intent.getBooleanExtra("isOnline", false)
            isInvertImage = intent.getBooleanExtra("isInvertImage", false)
        }
        if (user != null) {
            //actionbar
            val actionbar = supportActionBar
            //set actionbar title
            actionbar!!.title = user!!.login
            //set back button
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setDisplayHomeAsUpEnabled(true)

            progressDialog = ProgressDialog(this@ActivityProfile)
            progressDialog.setTitle("Profile")
            progressDialog.setMessage("Getting profile data, please wait")
            progressDialog.show()

            if (!isOnline) {
                checkOfflineData(!isOnline)
            } else {
                fetchUserDetails(user!!)
            }

            button_save.setOnClickListener {
                profile?.notes = edittext_notes.text.toString()
                profile?.let { it1 -> mProfileViewModel.addProfile(it1) }
                onAlertDialog(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        profile?.notes = edittext_notes.text.toString()
        profile?.let { it1 -> mProfileViewModel.addProfile(it1) }
        onBackPressed()
        return true
    }

    private fun checkOfflineData(setData: Boolean) {
        val profileLiveData: LiveData<List<Profile>> =
            mProfileViewModel.searchUser(user?.login!!)
        profileLiveData.observe(this@ActivityProfile) { prof ->
            if (prof.isNotEmpty()) {
                profile = prof[0]
                if (setData)
                    setData(profile!!)
                mProfileViewModel.addProfile(profile!!)
            } else {
                setData(profile!!)
            }

            Glide.with(this@ActivityProfile)
                .asBitmap()
                .load(user!!.imageLocal)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        if (isInvertImage) {
                            resource.apply {
                                imageView_profileImage.setImageBitmap(resource)
                                invertColors().apply {
                                    imageView_profileImage.setImageBitmap(
                                        this
                                    )
                                }
                            }
                        } else {
                            imageView_profileImage.setImageBitmap(resource)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

            progressDialog.dismiss()
            if (isOnline)
                fetchUserDetails(user!!)
            profileLiveData.removeObservers(this@ActivityProfile)
        }
    }

    private fun fetchUserDetails(user: User) {
        progressDialog.show()
        if (!user.login.isNullOrEmpty()) {
            val call = service.getUserProfile(TOKEN, user.login!!)
            call.enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        profile = response.body()
                        if (profile != null) {
                            Glide.with(this@ActivityProfile)
                                .asBitmap()
                                .load(profile!!.avatar_url)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        if (isInvertImage) {
                                            resource.apply {
                                                imageView_profileImage.setImageBitmap(resource)
                                                invertColors().apply {
                                                    imageView_profileImage.setImageBitmap(
                                                        this
                                                    )
                                                }
                                            }
                                        } else {
                                            imageView_profileImage.setImageBitmap(resource)
                                        }
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }
                                })

                            profile!!.notes = edittext_notes.text.toString()
                            mProfileViewModel.addProfile(profile!!)
                            setData(profile!!)
                        } else {
                            setData(Profile())
                        }
                        progressDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    progressDialog.dismiss()
                }
            })
        }
    }

    private fun setData(profile: Profile) {
        textView_followersCount.text = String.format("Followers: %s", profile.followers.toString())
        textView_followingCount.text = String.format("following: %s", profile.following.toString())
        textView_name.text = String.format("Name: %s", profile.name)
        textView_company.text = String.format("Company: %s", profile.company)
        textView_blog.text = String.format("Blog: %s", profile.blog)
        textView_location.text = String.format("Location: %s", profile.location)
        textView_email.text = String.format("Email: %s", profile.email)
        textView_bio.text = String.format("Bio: %s", profile.bio)
        edittext_notes.setText(user?.notes)
    }

    private fun onAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Note")
        builder.setMessage("Note has been saved successfully!")
        builder.setPositiveButton(
            "Ok"
        ) { _, _ ->
        }
        builder.show()
    }
}
