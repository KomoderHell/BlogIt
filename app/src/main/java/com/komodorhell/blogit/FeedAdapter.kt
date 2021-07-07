package com.komodorhell.blogit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.feed_list_item.view.*
import java.sql.Time

class FeedAdapter(val posts: List<PostModel>) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {


    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(post: PostModel) {
            itemView.textViewNameOfPost.text = post.title
            itemView.textViewTime.text = Time(post.timestamp).toString()
            Glide.with(itemView.context).load(post.userImage).into(itemView.imageViewProfilePic)
            Glide.with(itemView.context).load(post.image).into(itemView.imageViewPostPic)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedAdapter.FeedViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_list_item, parent, false)
        return FeedViewHolder(v)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: FeedAdapter.FeedViewHolder, position: Int) {
        holder.bind(posts[position])

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, posts[position].description, Toast.LENGTH_SHORT)
                .show()
        }
    }


}