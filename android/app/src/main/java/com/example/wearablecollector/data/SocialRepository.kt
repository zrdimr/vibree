package com.example.wearablecollector.data

import kotlinx.coroutines.flow.Flow

class SocialRepository(private val postDao: PostDao) {

    // Source of truth for posts (from local DB)
    val allPosts: Flow<List<Post>> = postDao.getAllPosts()
    val publicPosts: Flow<List<Post>> = postDao.getPublicPosts()

    suspend fun addPost(post: Post) {
        // 1. Save to local DB first (Offline-first)
        postDao.insert(post)
        
        // 2. TODO: If public, sync to Firebase
        if (post.isPublic) {
            syncToCloud(post)
        }
    }

    suspend fun deletePost(post: Post) {
        postDao.delete(post)
    }

    private fun syncToCloud(post: Post) {
        // Placeholder for Firebase/Remote logic
        // println("Syncing post to cloud: ${post.id}")
    }
}
