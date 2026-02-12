package com.example.wearablecollector.data

import kotlinx.coroutines.flow.Flow

class SocialRepository(private val postDao: PostDao) {

    // Source of truth for posts (from local DB)
    val allPosts: Flow<List<Post>> = postDao.getAllPosts()
    val publicPosts: Flow<List<Post>> = postDao.getPublicPosts()

    suspend fun addPost(post: Post) {
        // 1. Run AI Analysis (on IO thread by default mostly, but good to be safe)
        // In a real app with heavy model, use withContext(Dispatchers.Default)
        val analysis = com.example.wearablecollector.logic.StressAnalyzer.analyze(post.content)
        
        val analyzedPost = post.copy(
            stressScore = analysis.stressScore,
            sentiment = analysis.sentiment
        )
        
        // 2. Save to local DB first (Offline-first)
        postDao.insert(analyzedPost)
        
        // 3. TODO: If public, sync to Firebase
        if (analyzedPost.isPublic) {
            syncToCloud(analyzedPost)
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
