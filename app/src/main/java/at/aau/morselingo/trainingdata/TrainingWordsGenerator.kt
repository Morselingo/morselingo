package at.aau.morselingo.trainingdata

import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.random.Random

class TrainingWordsGenerator(private val repository: WordsRepository, private val random: Random = Random)
{
    companion object
    {
        private const val ALPHABET_SIZE = 26.0
    }

    /**
     * Builds a list of training words for the given [level] and [lang].
     *
     * @param level Game level (must be > 0).
     * @param lang Language code used to select the asset file (e.g., `"de"`, `"en"`).
     * @param allowedChars List of allowed characters (one character per entry). Only words composed
     * of these characters are considered.
     * @return A randomly sampled list of words that satisfy language/character/length constraints.
     *         Returns an empty list if no words match.
     *
     * @throws IllegalArgumentException if [level] <= 0 or if the internal constraints in
     * [calculateAccelerationReduction] are violated.
     */
    suspend fun generate(level: Int, lang: String, allowedChars: List<String>): List<String>
    {
        validateLevel(level)

        val minLength = calculateMinLengthPerLevel(level, allowedChars.size)
        val pool = repository.getFilteredWords(minLength, lang, allowedChars)
        if (pool.isEmpty()) return emptyList()

        val amountOfWords = calculateAmountOfWordsPerLevel(level)
        return pickRandom(pool, amountOfWords)
    }

    /**
     * Computes the **minimum word length** for a given [level], taking into account how many
     * distinct characters are currently allowed.
     *
     * The curve follows:
     * ```
     * minLen(level) = minLengthCap - (minLengthCap - start) * e^(-level / accelerator)
     * ```
     *
     * Where:
     * - `start` is the starting minimum (e.g., 2).
     * - `minLengthCap` is the upper cap the length approaches (e.g., 6).
     * - `accelerator = 100 - accelerationReduction`, with the reduction proportional to how many
     *    characters are unlocked (see [calculateAccelerationReduction]).
     *
     * Intuition:
     * - With few unlocked characters, the accelerator stays high ⇒ **slow** increase.
     * - As more characters unlock, the accelerator decreases ⇒ **faster** approach to the cap.
     *
     * @param level Current level (> 0).
     * @param numberOfAllowedChars How many distinct characters are available at this level.
     * @return Minimum length as an integer (rounded to nearest).
     */
    private fun calculateMinLengthPerLevel(level: Int, numberOfAllowedChars: Int) : Int
    {
        val accelerationReduction = calculateAccelerationReduction(numberOfAllowedChars)

        // Minimum length at game start
        val start = 2

        // Upper cap for minimum length
        val minLengthCap = 6

        // Controls the growth speed: higher values = slower increase
        val accelerator = 100 - accelerationReduction

        val value = minLengthCap - (minLengthCap - start) * exp(-level.toDouble() / accelerator)
        return value.roundToInt()
    }

    /**
     * Calculates the number of words for a given level.
     *
     * The calculation uses an exponential curve that slowly converges to a fixed maximum (`wordCap`).
     *
     * Mathematical formula:
     * ```
     * words = wordCap - (wordCap - start) * e^(-level / accelerator)
     * ```
     *
     * - At level 1, the number of words starts close to `start`.
     * - With each level increase, the value rises slowly.
     * - At high levels, the value approaches `wordCap` asymptotically (but never exceeds it).
     *
     * @param level The current level (must be >= 1).
     * @return The calculated number of words, rounded to the nearest integer.
     */
    private fun calculateAmountOfWordsPerLevel(level: Int): Int
    {
        // Minimum number of words at the starting level
        val start = 3

        // Maximum number of words that can be reached as the level increases
        val wordCap = 20

        // Controls the growth speed: higher values = slower increase
        val accelerator = 62.5

        val value = wordCap - (wordCap - start) * exp(-level.toDouble() / accelerator)
        return value.roundToInt()
    }

    /**
     * Picks [count] random words from [pool] without duplicates.
     *
     * If [count] >= pool size, returns the entire pool in random order; otherwise, returns a random
     * subset of size [count].
     *
     * @param pool Source list of candidate words (already filtered).
     * @param count Desired number of words to pick.
     * @return A randomly shuffled list of words, of size `min(count, pool.size)`.
     */
    private fun pickRandom(pool: List<String>, count: Int): List<String>
    {
        if (count >= pool.size) return pool.shuffled(random)
        return pool.shuffled(random).take(count)
    }

    /**
     * Computes how much the **accelerator** should be reduced based on the number of
     * unlocked/allowed characters.
     *
     * The reduction is a percentage of the base accelerator (here modeled with 100):
     * ```
     * accelerationReduction = (numberOfAllowedChars / ALPHABET_SIZE) * reductionPercentage
     * ```
     * with `reductionPercentage = 50.0`, yielding a reduction in **[0, 50]** as the unlocked
     * fraction goes from 0 to 1.
     *
     * A safety constraint is enforced to avoid extreme inputs:
     * ```
     * sizeRestraint = ALPHABET_SIZE * (1 / (reductionPercentage / 100))
     * ```
     * With ALPHABET_SIZE=26 and reductionPercentage=50, this is 52. The function requires
     * `numberOfAllowedChars < sizeRestraint`.
     *
     * @param numberOfAllowedChars Count of distinct unlocked characters.
     * @return A reduction value in the range [0.0, 50.0] under normal conditions.
     * @throws IllegalArgumentException if `numberOfAllowedChars >= sizeRestraint`.
     */
    private fun calculateAccelerationReduction(numberOfAllowedChars: Int): Double
    {
        val reductionPercentage = 50.0

        val sizeRestraint = ALPHABET_SIZE * (1 / (reductionPercentage / 100.0))

        require(numberOfAllowedChars.toDouble() < sizeRestraint)
        {
            "size of allowedChars too big! Must be < $sizeRestraint (was $numberOfAllowedChars)!"
        }

        return (numberOfAllowedChars.toDouble() / ALPHABET_SIZE) * reductionPercentage
    }

    private fun validateLevel(level: Int)
    {
        require(level > 0) { "level must be > 0 (was $level)!" }
    }
}