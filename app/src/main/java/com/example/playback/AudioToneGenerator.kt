package com.example.playback

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

object AudioToneGenerator {

    /**
     * Generates a playable PCM 16-bit 44.1kHz stereo/mono WAV file for demo and showcase tracks.
     * Produces warm synth chords with subtle frequency modulation.
     */
    fun getOrCreateSampleAudio(context: Context, filename: String, durationSeconds: Int, baseFreqHz: Double): File {
        val cacheDir = File(context.cacheDir, "sonora_samples")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val targetFile = File(cacheDir, filename)
        if (targetFile.exists() && targetFile.length() > 44) {
            return targetFile
        }

        try {
            val sampleRate = 44100
            val numSamples = sampleRate * durationSeconds.coerceIn(3, 40) // capped for quick generation
            val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)

            val twoPi = 2.0 * Math.PI
            val freq1 = baseFreqHz
            val freq2 = baseFreqHz * 1.5 // Perfect fifth
            val freq3 = baseFreqHz * 1.25 // Major third

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                // Smooth envelope: gentle fade in (0.5s) and fade out (1.0s)
                val fadeIn = (time / 0.5).coerceIn(0.0, 1.0)
                val fadeOut = ((durationSeconds - time) / 1.0).coerceIn(0.0, 1.0)
                val env = fadeIn * fadeOut

                // Harmonic synth chord
                val sampleValue = (
                    0.5 * sin(twoPi * freq1 * time) +
                    0.3 * sin(twoPi * freq2 * time) +
                    0.2 * sin(twoPi * freq3 * time)
                ) * env * 30000.0

                val shortVal = sampleValue.toInt().coerceIn(-32768, 32767).toShort()
                buffer.putShort(shortVal)
            }

            val pcmData = buffer.array()
            FileOutputStream(targetFile).use { fos ->
                writeWavHeader(fos, pcmData.size, sampleRate, 1, 16)
                fos.write(pcmData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return targetFile
    }

    private fun writeWavHeader(out: FileOutputStream, pcmDataSize: Int, sampleRate: Int, channels: Int, bitsPerSample: Int) {
        val totalDataLen = pcmDataSize + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8

        val header = ByteArray(44)
        // RIFF chunk
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        // WAVE
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        // 'fmt '
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        // Subchunk1Size (16 for PCM)
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        // AudioFormat (1 for PCM)
        header[20] = 1
        header[21] = 0
        // NumChannels
        header[22] = channels.toByte()
        header[23] = 0
        // SampleRate
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        // ByteRate
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        // BlockAlign
        header[32] = (channels * bitsPerSample / 8).toByte()
        header[33] = 0
        // BitsPerSample
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        // 'data'
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        // Subchunk2Size
        header[40] = (pcmDataSize and 0xff).toByte()
        header[41] = ((pcmDataSize shr 8) and 0xff).toByte()
        header[42] = ((pcmDataSize shr 16) and 0xff).toByte()
        header[43] = ((pcmDataSize shr 24) and 0xff).toByte()

        out.write(header, 0, 44)
    }
}
