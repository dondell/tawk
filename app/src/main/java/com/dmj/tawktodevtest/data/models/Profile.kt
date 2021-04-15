package com.dmj.tawktodevtest.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "profile_table")
data class Profile(
    @SerializedName("login") var login: String? = "",
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") var id: Int? = 0,
    @SerializedName("node_id") var node_id: String? = "",
    @SerializedName("avatar_url") var avatar_url: String? = "",
    @SerializedName("gravatar_id") var gravatar_id: String? = "",
    @SerializedName("url") var url: String? = "",
    @SerializedName("html_url") var html_url: String? = "",
    @SerializedName("followers_url") var followers_url: String? = "",
    @SerializedName("following_url") var following_url: String? = "",
    @SerializedName("gists_url") var gists_url: String? = "",
    @SerializedName("starred_url") var starred_url: String? = "",
    @SerializedName("subscriptions_url") var subscriptions_url: String? = "",
    @SerializedName("organizations_url") var organizations_url: String? = "",
    @SerializedName("repos_url") var repos_url: String? = "",
    @SerializedName("events_url") var events_url: String? = "",
    @SerializedName("received_events_url") var received_events_url: String? = "",
    @SerializedName("type") var type: String? = "",
    @SerializedName("site_admin") var site_admin: Boolean? = false,
    @SerializedName("name") var name: String? = "",
    @SerializedName("company") var company: String? = "",
    @SerializedName("blog") var blog: String? = "",
    @SerializedName("location") var location: String? = "",
    @SerializedName("email") var email: String? = "",
    @SerializedName("hireable") var hireable: String? = "",
    @SerializedName("bio") var bio: String? = "",
    @SerializedName("twitter_username") var twitter_username: String? = "",
    @SerializedName("public_repos") var public_repos: Int? = 0,
    @SerializedName("public_gists") var public_gists: Int? = 0,
    @SerializedName("followers") var followers: Int? = 0,
    @SerializedName("following") var following: Int? = 0,
    @SerializedName("created_at") var created_at: String? = "",
    @SerializedName("updated_at") var updated_at: String? = "",
    @SerializedName("notes") public var notes: String? = ""
)
