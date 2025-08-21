package at.aau.morselingo.trainingdata

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/**
 * Repository responsible for loading and filtering training words
 * from the app's asset directory.
 *
 * The expected file structure inside `assets/trainingdata/data`:
 *
 * ```
 * ["word1", "word2", "word3", ...]
 * ```
 *
 * @param context Android context to access app assets.
 */
class WordsRepository(private val context: Context)
{
    /**
     * Loads words from the JSON file for the given language
     * and filters them by minimum word length and allowed characters.
     *
     * @param minLength The minimum word length (must be > 0).
     * @param lang The language identifier (e.g., `"de"`, `"en"`).
     * @param allowedChars List of allowed characters (one character per entry).
     *
     * @return A list of words that match the filtering criteria.
     *
     * @throws IllegalArgumentException If validation for parameters fails.
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getFilteredWords(minLength: Int, lang: String, allowedChars: List<String>): List<String>
    {
        return withContext(Dispatchers.IO)
        {
            validateMinLength(minLength)
            validateAllowedChars(allowedChars)

            val filePath = getFilePath(lang)
            val allowed = getAllowedCharsAsSet(allowedChars)

            try
            {
                val all = loadAllWords(filePath)
                filterWords(all, minLength, allowed)
            }
            catch (e: java.io.FileNotFoundException)
            {
                Log.e("WordsRepository", "File not found: $filePath", e)
                emptyList()
            }
        }
    }

    /**
     * Builds the relative path for the JSON file in the assets folder
     * based on the given language identifier.
     */
    private fun getFilePath(lang: String): String
    {
        return "trainingdata/data/words-$lang.json"
    }

    /**
     * Loads and parses all words from the given JSON asset file.
     *
     * @param filePath The relative path to the file in assets.
     * @return A list of words converted to lowercase.
     */
    @OptIn(ExperimentalSerializationApi::class)
    private fun loadAllWords(filePath: String): List<String>
    {
        return context.assets.open(filePath).use { input ->
            val all: List<String> = Json.decodeFromStream(input)
            all.asSequence()
                .map { it.lowercase() }
                .toList()
        }
    }

    /**
     * Filters the provided list of words by:
     * - minimum length
     * - allowed characters (words must only contain characters from the set)
     *
     * @param loadedWords The full list of loaded words.
     * @param minLength Minimum word length.
     * @param allowed Set of allowed characters.
     * @return Filtered list of words.
     */
    private fun filterWords(loadedWords: List<String>, minLength: Int, allowed: Set<String>): List<String>
    {
        return loadedWords.asSequence()
                .filter { it.length >= minLength }
                .filter { word -> word.all { ch -> allowed.contains(ch.toString()) } }
                .toList()
    }

    /**
     * Converts the list of allowed characters into a `Set`
     * for faster lookups during filtering.
     */
    private fun getAllowedCharsAsSet(allowedChars: List<String>): Set<String>
    {
        return allowedChars.map { it.lowercase() }.toSet()
    }

    /**
     * Validates that the minimum word length is greater than zero.
     *
     * @throws IllegalArgumentException if `minLength` <= 0.
     */
    private fun validateMinLength(minLength: Int)
    {
        require(minLength > 0) { "minLength must be > 0 (was $minLength)!" }
    }

    /**
     * Validates that the allowed characters list:
     * - is not empty
     * - only contains single-character strings
     *
     * @throws IllegalArgumentException if the list is invalid.
     */
    private fun validateAllowedChars(allowedChars: List<String>)
    {
        require(allowedChars.isNotEmpty()) { "allowedChars must not be empty!" }

        val multiCharTokens = allowedChars.filter { it.codePointCount(0, it.length) != 1 }
        require(multiCharTokens.isEmpty())
        {
            "allowedChars contains multi-character entries: $multiCharTokens!"
        }
    }
}