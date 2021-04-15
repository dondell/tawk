package com.dmj.tawktodevtest.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") var id: Int? = null,
    @SerializedName("login") var login: String? = "",
    @SerializedName("node_id") var node_id: String? = null,
    @SerializedName("avatar_url") var avatar_url: String? = null,
    @SerializedName("gravatar_id") var gravatar_id: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("html_url") var html_url: String? = null,
    @SerializedName("followers_url") var followers_url: String? = null,
    @SerializedName("following_url") var following_url: String? = null,
    @SerializedName("gists_url") var gists_url: String? = null,
    @SerializedName("starred_url") var starred_url: String? = null,
    @SerializedName("subscriptions_url") var subscriptions_url: String? = null,
    @SerializedName("organizations_url") var organizations_url: String? = null,
    @SerializedName("repos_url") var repos_url: String? = null,
    @SerializedName("events_url") var events_url: String? = null,
    @SerializedName("received_events_url") var received_events_url: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("site_admin") var site_admin: Boolean? = false,
    @ColumnInfo(name = "isOnline")
    @SerializedName("isOnline") var isOnline: Boolean? = false,
    @ColumnInfo(name = "notes")
    @SerializedName("notes") var notes: String? = null,
    @ColumnInfo(name = "imageLocal")
    @SerializedName("imageLocal") var imageLocal: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(login)
        parcel.writeString(node_id)
        parcel.writeString(avatar_url)
        parcel.writeString(gravatar_id)
        parcel.writeString(url)
        parcel.writeString(html_url)
        parcel.writeString(followers_url)
        parcel.writeString(following_url)
        parcel.writeString(gists_url)
        parcel.writeString(starred_url)
        parcel.writeString(subscriptions_url)
        parcel.writeString(organizations_url)
        parcel.writeString(repos_url)
        parcel.writeString(events_url)
        parcel.writeString(received_events_url)
        parcel.writeString(type)
        parcel.writeValue(site_admin)
        parcel.writeValue(isOnline)
        parcel.writeString(notes)
        parcel.writeString(imageLocal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
