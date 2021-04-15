package com.dmj.tawktodevtest.ui.userlist

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dmj.tawktodevtest.R
import com.dmj.tawktodevtest.data.models.User
import com.dmj.tawktodevtest.data.models.UserViewModel
import com.dmj.tawktodevtest.ui.profile.ActivityProfile
import com.dmj.tawktodevtest.utils.BitmapHelper.invertColors
import kotlinx.android.synthetic.main.item_loading.view.*
import kotlinx.android.synthetic.main.item_user.view.*


class UserListAdapter(
    private val userList: List<User>,
    private val activityUserList: ActivityUserList
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.imageView_profileImage
        val username: TextView? = itemView.textView_username
        val details: TextView? = itemView.textView_details
        val notes: ImageView? = itemView.imageView_notes
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.progressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val itemView: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            UserViewHolder(itemView)
        } else {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserViewHolder) {
            val user = userList[position]
            val imageURL = user.avatar_url
            val checkIfPositionIsEveryFourth = position % 4;
            Glide.with(activityUserList)
                .asBitmap()
                .load(user.imageLocal)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        if (checkIfPositionIsEveryFourth == 3) {
                            resource.apply {
                                holder.imageView.setImageBitmap(resource)
                                invertColors().apply { holder.imageView.setImageBitmap(this) }
                            }
                        } else {
                            holder.imageView.setImageBitmap(resource)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

            holder.username?.text = user.login
            holder.details?.text = user.repos_url
            holder.itemView.setOnClickListener {
                val profileIntent =
                    Intent(activityUserList, ActivityProfile::class.java).apply {
                        putExtra("user_data", user)
                        putExtra("isOnline", user.isOnline)
                        putExtra(
                            "isInvertImage", when (checkIfPositionIsEveryFourth) {
                                3 -> true
                                else -> false
                            }
                        )
                    }
                activityUserList.startActivity(profileIntent)
            }
            if (TextUtils.isEmpty(user.notes))
                holder.notes?.visibility = View.INVISIBLE
            else
                holder.notes?.visibility = View.VISIBLE
        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true;
        }
    }

    override fun getItemCount() = userList.size

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (userList[position].login.equals("this_is_a_loading_view")) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

}