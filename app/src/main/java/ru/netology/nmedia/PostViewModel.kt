package ru.netology.nmedia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException

class PostViewModel (application: Application) : AndroidViewModel(application), PostInteractionCommands{

    private val repository : PostRepository = PostRepositoryHTTPImpl()

    private val _data = MutableLiveData(FeedModel())
    val data : LiveData<FeedModel>
        get() = _data

    private val emptyPost = Post(
        0,
        "",
        "",
        "",
        "",
        "",
        false,
        0,
        0,
        0
    )

    val editedPost = MutableLiveData(emptyPost)
    val openedPost = MutableLiveData(emptyPost)

    private val _postUpdated = SingleLiveEvent<Post>()

    val postUpdated: LiveData<Post>
        get() = _postUpdated

    fun updatedPost(post: Post) {

        if (post.id == 0) {

            loadPosts()

        } else {


            repository.getByIdAsync(post.id, object : PostRepository.GetByIdCallback {

                override fun onSuccess(post: Post) {
                    _data.value?.posts?.map { thisPost -> if (thisPost.id == post.id) { post } else { thisPost } }
                        ?.let{ postsList ->
                            _data.postValue(FeedModel(posts = postsList, empty = postsList.isEmpty()))
                        }
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }

            })


        }

    }

    fun loadPosts() {

        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {

            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }


        })

    }

    private fun setObserveEditOpenPost(id: Int) {

        if (editedPost.value?.id != 0 && editedPost.value?.id == id) {

            data.value?.posts?.map { post ->
                if (post.id == editedPost.value?.id) { editedPost.value = post }
            }

        }

        if (openedPost.value?.id != 0 && openedPost.value?.id == id) {

            data.value?.posts?.map { post ->
                if (post.id == openedPost.value?.id) { openedPost.value = post }
            }

        }

    }

    override fun onLike(post: Post) {

        repository.getByIdAsync(post.id, object : PostRepository.GetByIdCallback {

            override fun onSuccess(post: Post) {

                if (post.likeByMe) {

                    repository.dislikeByIdAsync(post.id, object : PostRepository.DislikeByIdCallback {

                        override fun onSuccess() {
                            _postUpdated.postValue(post)
                            setObserveEditOpenPost(post.id)
                        }

                        override fun onError(e: Exception) {

                        }

                    })


                } else {

                    repository.likeByIdAsync(post.id, object : PostRepository.LikeByIdCallback {

                        override fun onSuccess() {
                            _postUpdated.postValue(post)
                            setObserveEditOpenPost(post.id)
                        }

                        override fun onError(e: Exception) {

                        }

                    })

                }


            }

            override fun onError(e: Exception) {

            }

        })

    }

    override fun onShare(post: Post) {



    }

    override fun onRemove(post: Post) {

        repository.removeByIdAsync(post.id, object : PostRepository.RemoveByIdCallback {

            override fun onSuccess() {
                _postUpdated.postValue(emptyPost)

                onCancelEdit()
                onCancelOpen()
            }

            override fun onError(e: Exception) {

            }

        })

    }

    override fun onEditPost(post: Post) {

        editedPost.value = post

    }

    override fun onSaveContent(newContent: String) {

        val text = newContent.trim()

        editedPost.value?.let { thisEditedPost ->

            if (thisEditedPost.content != text) {

                val postForSaved = thisEditedPost.copy(content = text)

                if (thisEditedPost.id == 0) {

                    repository.saveNewPostAsync(postForSaved, object : PostRepository.SaveNewPostCallback {

                        override fun onSuccess() {
                            _postUpdated.postValue(emptyPost)
                        }

                        override fun onError(e: Exception) {

                        }

                    })

                } else {

                    repository.editPostAsync(postForSaved, object : PostRepository.EditPostCallback {

                        override fun onSuccess() {
                            _postUpdated.postValue(postForSaved)
                        }

                        override fun onError(e: Exception) {

                        }

                    })

                }

            }

            editedPost.value = emptyPost

            setObserveEditOpenPost(thisEditedPost.id)

        }


    }

    override fun onCancelEdit() {

        editedPost.value = emptyPost

    }

    override fun onOpenPost(post: Post) {

        openedPost.value = post

    }

    override fun onCancelOpen() {

        openedPost.value = emptyPost

    }

}
