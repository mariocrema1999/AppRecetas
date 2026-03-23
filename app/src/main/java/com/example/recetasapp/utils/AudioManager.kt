package com.example.recetasapp.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import java.io.IOException
import kotlin.random.Random

class AudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    fun playRandomJoke() {
        // Only if not already playing or to interrupt? 
        // Let's assume jokes interrupt or play if idle.
        // For timers, it usually interrupts.
        val jokeIndex = Random.nextInt(1, 26) // 1 to 25. Assume we have 25 jokes.
        playAsset("audios/joke_$jokeIndex.mp3")
    }

    fun playCelebration() {
        playAsset("audios/celebration.mp3")
    }

    fun playWelcome() {
         playAsset("audios/welcome.mp3")
    }

    private fun playAsset(fileName: String) {
        stop() // Stop previous
        
        try {
            // Check if file exists roughly by trying to open it
            // In a real app we might cache the list of assets.
            
            val afd: AssetFileDescriptor = context.assets.openFd(fileName)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setOnCompletionListener { 
                    it.release() 
                    mediaPlayer = null
                }
                prepare()
                start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Fail silently or log
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
    }
}
