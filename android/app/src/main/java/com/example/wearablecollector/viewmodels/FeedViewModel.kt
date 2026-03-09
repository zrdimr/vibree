package com.example.wearablecollector.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wearablecollector.data.Post
import com.example.wearablecollector.data.SocialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: SocialRepository) : ViewModel() {

    // Expose all posts as a StateFlow for Compose
    val allPosts: StateFlow<List<Post>> = repository.allPosts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    fun addPost(content: String, isPublic: Boolean) {
        viewModelScope.launch {
            // Analyze stress from context before saving
            val aiStressScore = com.example.wearablecollector.logic.StressAnalysisAgent.analyzeText(content)
            
            val newPost = Post(
                userId = "current_user", 
                content = content,
                timestamp = System.currentTimeMillis(),
                isPublic = isPublic,
                stressScore = if (aiStressScore != -1) aiStressScore else null
            )
            repository.addPost(newPost)
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }
}

class FeedViewModelFactory(private val repository: SocialRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
