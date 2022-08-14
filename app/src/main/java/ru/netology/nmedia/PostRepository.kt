package ru.netology.nmedia

import androidx.lifecycle.LiveData

interface PostRepository {

    val data : LiveData<List<Post>>
    fun getAll() : List<Post>
    fun likeById(id: Int)
    fun shareById(id: Int)
    fun removeById(id: Int)
    fun saveNewPost(post: Post)
    fun editPost(post: Post)
    fun getById(id: Int) : Post

    fun getAllAsync(callback: GetAllCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    fun getByIdAsync(id: Int, callback: GetByIdCallback)
    interface GetByIdCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    fun likeByIdAsync(id: Int, callback: LikeByIdCallback)
    interface LikeByIdCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    fun dislikeByIdAsync(id: Int, callback: DislikeByIdCallback)
    interface DislikeByIdCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    fun removeByIdAsync(id: Int, callback: RemoveByIdCallback)
    interface RemoveByIdCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    fun saveNewPostAsync(post: Post, callback: SaveNewPostCallback)
    interface SaveNewPostCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    fun editPostAsync(post: Post, callback: EditPostCallback)
    interface EditPostCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

}