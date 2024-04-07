package org.fct.persistence.impl

/**
 * A sparse Fenwick tree for range-sum queries.
 *
 * This tree has (0, Long.MAX_VALUE) as the domain for its keys, and uses O(N*log M) space where
 * N is the number of keys, and M is the maximum key used. Update operations take O(log M), while
 * range query operations take O(log R) where R is difference between the largest key and the
 * requested threshold.
 */
class SparseFenwickTree {
    private val tree = HashMap<Long, Int>()
    private val values = HashMap<Long, Int>()
    private var maxIndex = 0L
    private var totalSum = 0

    /**
     * Overwrite value stored at the given index.
     */
    fun set(index: Long, value: Int) {
        require(index > 0) { "Index has to be positive. "}
        add(index, value - values.getOrDefault(index, 0))
    }

    /**
     * @return sum of values associated with keys less than or equal to [index].
     */
    fun prefixSum(index: Long) : Int {
        require(index > 0) { "Index has to be positive. "}
        return totalSum - suffixSum(index + 1)
    }

    /**
     * Add [delta] to the value stored at [index].
     */
    fun add(index: Long, delta: Int) {
        require(index > 0) { "Index has to be positive. "}
        if (delta == 0) return
        
        totalSum += delta
        values[index] = values.getOrDefault(index, 0) + delta
        maxIndex = maxOf(maxIndex, index)
        
        var i = index
        while (i > 0) {
            tree[i] = tree.getOrDefault(i, 0) + delta
            i -= i and -i
        }
    }

    /**
     * @return sum of values associated with keys greater than or equal to [index].
     */
    fun suffixSum(index: Long) : Int {
        require(index > 0) { "Index has to be positive. "}
        var result = 0
        var i = index
        while ((0 < i) && (i <= maxIndex)) {
            result += tree[i] ?: 0
            i += i and -i
        }
        return result
    }
}