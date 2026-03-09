package com.example.wearablecollector.logic

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class BertTokenizer(context: Context) {
    private val vocab = mutableMapOf<String, Int>()
    
    init {
        try {
            val inputStream = context.assets.open("vocab.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var index = 0
            reader.forEachLine { line ->
                vocab[line.trim()] = index
                index++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun tokenize(text: String, maxLength: Int = 128): Pair<LongArray, LongArray> {
        val tokens = mutableListOf<Long>()
        val inputIds = LongArray(maxLength) { 0 }
        val attentionMask = LongArray(maxLength) { 0 }
        
        // [CLS] token
        tokens.add(vocab["[CLS]"]?.toLong() ?: 101L)
        
        val words = text.lowercase().split(Regex("\\s+"))
        for (word in words) {
            if (tokens.size >= maxLength - 1) break
            
            // Simplified WordPiece
            if (vocab.containsKey(word)) {
                tokens.add(vocab[word]!!.toLong())
            } else {
                // TODO: Full WordPiece subword splitting if needed
                tokens.add(vocab["[UNK]"]?.toLong() ?: 100L)
            }
        }
        
        // [SEP] token
        if (tokens.size < maxLength) {
            tokens.add(vocab["[SEP]"]?.toLong() ?: 102L)
        }
        
        for (i in tokens.indices) {
            if (i < maxLength) {
                inputIds[i] = tokens[i]
                attentionMask[i] = 1L
            }
        }
        
        return Pair(inputIds, attentionMask)
    }
}
