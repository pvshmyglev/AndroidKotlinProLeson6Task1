package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.RuntimeException
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class PostRepositoryHTTPImpl() : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeTokenListPosts = object : TypeToken <List<Post>> () {}
    private val typeTokenPost = object : TypeToken <Post> () {}
    override val data: MutableLiveData<List<Post>> = MutableLiveData(emptyList())

    companion object {

        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()

    }

    override fun getAll() : List<Post> {

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null!")
            }
            .let {
                gson.fromJson(it, typeTokenListPosts.type)
            }
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("Body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeTokenListPosts.type))
                    } catch(e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })
    }

    override fun getById(id: Int) : Post {

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts/${id}")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null!")
            }
            .let {
                gson.fromJson(it, typeTokenPost.type)
            }
    }

    override fun getByIdAsync(id: Int, callback: PostRepository.GetByIdCallback) {

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts/${id}")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("Body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeTokenPost.type))
                    } catch(e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
               }
            })

    }


    override fun likeById(id: Int) {

        val postOnServer = getById(id)

        val request =

        if (postOnServer.likeByMe == true) {

            Request.Builder()
                .url("${BASE_URL}/api/posts/${id}/likes")
                .delete()
                .build()

        }  else {

            val requestBody = "".toRequestBody()

            Request.Builder()
                .url("${BASE_URL}/api/posts/${id}/likes")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()

        }

        val result = client
            .newCall(request)
            .execute()

    }

    override fun likeByIdAsync(id: Int, callback: PostRepository.LikeByIdCallback) {


        val requestBody = "".toRequestBody()

        val request =
            Request.Builder()
                .url("${BASE_URL}/api/posts/${id}/likes")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()



        return client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch(e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })

    }

    override fun dislikeByIdAsync(id: Int, callback: PostRepository.DislikeByIdCallback) {

        val request =
            Request.Builder()
                .url("${BASE_URL}/api/posts/${id}/likes")
                .delete()
                .build()



        return client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch(e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })

    }






    override fun shareById(id: Int) {
    /*
        data.value = data.value?.map {post ->
            if (post.id != id) {

                post

            } else {

                val updatedPost = post.copy(countShare = post.countShare + 1)

                updatedPost

            }
        }
    */
    }


    override fun removeById(id: Int) {

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts/${id}")
            .delete()
            .build()

        client
        .newCall(request)
        .execute()

    }

    override fun removeByIdAsync(id: Int, callback: PostRepository.RemoveByIdCallback) {

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts/${id}")
            .delete()
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch(e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })

    }

    override fun saveNewPost(post: Post) {

        val requestBody = gson.toJson(post).toRequestBody()

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client
        .newCall(request)
        .execute()
    }

    override fun saveNewPostAsync(post: Post, callback: PostRepository.SaveNewPostCallback) {

        val requestBody = gson.toJson(post).toRequestBody()

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch(e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })
    }

    override fun editPost(post: Post) {

        val requestBody = gson.toJson(post).toRequestBody()

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client
        .newCall(request)
        .execute()

    }

    override fun editPostAsync(post: Post, callback: PostRepository.EditPostCallback) {

        val requestBody = gson.toJson(post).toRequestBody()

        val request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch(e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })

    }

}