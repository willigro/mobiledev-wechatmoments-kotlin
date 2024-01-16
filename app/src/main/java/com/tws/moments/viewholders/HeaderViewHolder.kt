//package com.tws.moments.viewholders
//
//import androidx.recyclerview.widget.RecyclerView
//import com.tws.moments.TWApplication
//import com.tws.moments.api.entry.UserBean
//import com.tws.moments.databinding.ItemMomentHeadBinding
//
//class HeaderViewHolder(private val binding: ItemMomentHeadBinding) :
//    RecyclerView.ViewHolder(binding.root) {
//    private var imageLoader = TWApplication.imageLoader
//
//    fun bind(userBean: UserBean?) {
//        userBean?.also {
//            binding.tvUserNickname.text = userBean.nick
//            imageLoader.displayImage(userBean.profileImage, binding.ivUserProfile)
//            imageLoader.displayImage(userBean.avatar, binding.ivUserAvatar)
//        }
//    }
//}