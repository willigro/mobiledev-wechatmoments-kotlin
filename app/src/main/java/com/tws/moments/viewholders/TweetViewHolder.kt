package com.tws.moments.viewholders

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tws.moments.TWApplication
import com.tws.moments.adapters.CommentsAdapter
import com.tws.moments.adapters.ImagesAdapter
import com.tws.moments.api.entry.CommentsBean
import com.tws.moments.api.entry.ImagesBean
import com.tws.moments.api.entry.SenderBean
import com.tws.moments.api.entry.TweetBean
import com.tws.moments.databinding.LayoutBaseTweetBinding
import com.tws.moments.utils.dip
import com.tws.moments.views.itemdecoration.ImagesDecoration
import com.tws.moments.views.itemdecoration.MarginItemDecoration

private const val IMAGE_SPAN_COUNT = 3

class TweetViewHolder(private val binding: LayoutBaseTweetBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        setupCommentsView(binding.rvComments)
        addTweetImagesView()
    }

    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var commentsAdapter: CommentsAdapter
    private var imageLoader = TWApplication.imageLoader

    fun bind(tweet: TweetBean) {
        renderSenderContent(tweet.sender)
        renderTextContent(tweet.content)
        renderImages(tweet.images)
        renderComments(tweet.comments)
    }

    private fun renderComments(comments: List<CommentsBean>?) {
        commentsAdapter.comments = comments
    }

    private fun renderSenderContent(sender: SenderBean?) {
        binding.tvSenderNickname.text = sender?.nick
        imageLoader.displayImage(sender?.avatar, binding.ivSenderAvatar)
    }

    private fun renderTextContent(content: String?) {
        binding.tvTweetContent.text = content
    }

    private fun renderImages(imagesBean: List<ImagesBean>?) {
        if (imagesBean.isNullOrEmpty()) {
            binding.simpleImageView.visibility = View.GONE
            binding.imagesRecyclerView.visibility = View.GONE
            return
        }

        binding.imagesRecyclerView.layoutManager = if (imagesBean.size == 4) {
            GridLayoutManager(itemView.context, 2, RecyclerView.HORIZONTAL, false)
        } else {
            GridLayoutManager(itemView.context, IMAGE_SPAN_COUNT, RecyclerView.VERTICAL, false)
        }

        if (imagesBean.size == 1) {
            binding.simpleImageView.visibility = View.VISIBLE
            binding.imagesRecyclerView.visibility = View.GONE

            val url = imagesBean[0].url

            imageLoader.displayImage(
                url, binding.simpleImageView
            )

            binding.simpleImageView.tag = url

            imagesAdapter.images = null
        } else {
            binding.simpleImageView.visibility = View.GONE
            binding.imagesRecyclerView.visibility = View.VISIBLE
            imagesAdapter.images = imagesBean
                .asSequence()
                .map { it.url ?: "" }
                .filter { it.isNotEmpty() }
                .toList()
        }
    }

    private fun setupCommentsView(commentsView: RecyclerView) {
        commentsView.layoutManager = LinearLayoutManager(itemView.context)
        commentsAdapter = CommentsAdapter()
        commentsView.adapter = commentsAdapter

        commentsView.addItemDecoration(
            MarginItemDecoration(
                RecyclerView.VERTICAL,
                itemView.context.dip(5)
            )
        )
    }

    private fun addTweetImagesView() {
        imagesAdapter = ImagesAdapter()
        binding.imagesRecyclerView.addItemDecoration(ImagesDecoration(itemView.context.dip(5)))
        binding.imagesRecyclerView.adapter = imagesAdapter
    }
}