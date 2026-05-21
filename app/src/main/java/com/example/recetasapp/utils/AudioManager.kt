package com.example.recetasapp.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.Log
import java.io.IOException
import kotlin.random.Random

/**
 * Clase encargada de la gestión de la reproducción de audio en la aplicación.
 * Permite reproducir pistas aleatorias de la carpeta de assets y sonidos específicos de celebración.
 *
 * @property context Contexto de la aplicación necesario para acceder a los assets.
 */
class AudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var audioFiles: List<String> = emptyList()
    private var currentTrack: String? = null
    private var lastPosition: Int = 0
    private var isManuallyPaused: Boolean = false

    init {
        loadAudioFiles()
    }

    /**
     * Carga la lista de archivos de audio disponibles en la carpeta 'audios' de los assets.
     */
    private fun loadAudioFiles() {
        try {
            val files = context.assets.list("audios") ?: emptyArray()
            // Filtramos Celebracion.mp3 para que no suene en el aleatorio
            audioFiles = files.filter { !it.equals("Celebracion.mp3", ignoreCase = true) }
                .map { "audios/$it" }
        } catch (e: IOException) {
            Log.e("AudioManager", "Error cargando lista de audios", e)
        }
    }

    /**
     * Comprueba si se está reproduciendo audio actualmente.
     */
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    /**
     * Reproduce una pista de audio aleatoria de la lista cargada.
     */
    fun playRandomAudio() {
        if (audioFiles.isNotEmpty() && !isPlaying()) {
            val randomIndex = Random.nextInt(audioFiles.size)
            playAsset(audioFiles[randomIndex])
        }
    }

    /**
     * Reproduce el sonido específico de celebración.
     */
    fun playCelebration() {
        playAsset("audios/Celebracion.mp3")
    }

    /**
     * Reproduce un archivo de audio específico desde los assets.
     * @param fileName Ruta relativa del archivo dentro de assets.
     * @param seekTo Posición inicial de reproducción en milisegundos.
     */
    private fun playAsset(fileName: String, seekTo: Int = 0) {
        stop() 
        currentTrack = fileName
        isManuallyPaused = false
        
        try {
            val afd: AssetFileDescriptor = context.assets.openFd(fileName)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setOnCompletionListener { 
                    it.release() 
                    mediaPlayer = null
                    currentTrack = null
                    lastPosition = 0
                }
                prepare()
                if (seekTo > 0) seekTo(seekTo)
                start()
            }
        } catch (e: Exception) {
            Log.e("AudioManager", "Error al reproducir $fileName", e)
        }
    }

    /**
     * Pausa la reproducción actual y guarda la posición.
     */
    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                lastPosition = it.currentPosition
                it.pause()
                isManuallyPaused = true
            }
        }
    }

    /**
     * Reanuda la reproducción pausada o inicia una nueva pista aleatoria.
     */
    fun resume() {
        if (isManuallyPaused && currentTrack != null) {
            mediaPlayer?.start()
            isManuallyPaused = false
        } else if (currentTrack == null) {
            playRandomAudio()
        }
    }

    /**
     * Detiene la reproducción y libera los recursos del MediaPlayer.
     */
    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Ignorar
        }
        mediaPlayer = null
        // No reseteamos currentTrack aquí para permitir resume() si es pausa lógica
    }
    
    /**
     * Detiene completamente el audio y resetea el estado del track actual.
     */
    fun hardStop() {
        stop()
        currentTrack = null
        lastPosition = 0
        isManuallyPaused = false
    }
}
