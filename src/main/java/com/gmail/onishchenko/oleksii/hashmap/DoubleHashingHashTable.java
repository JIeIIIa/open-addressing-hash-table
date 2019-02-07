package com.gmail.onishchenko.oleksii.hashmap;

import java.util.Arrays;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public class DoubleHashingHashTable implements HashTable {
    /**
     * Entry for Hash table.
     */
    static class Entry {
        private final int key;
        private long value;

        Entry(int key, long value) {
            this.key = key;
            this.value = value;
        }

        final int getKey() {
            return key;
        }

        final long getValue() {
            return value;
        }

        final void setValue(long newValue) {
            value = newValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return key == entry.key &&
                    value == entry.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        public final String toString() {
            return key + "=" + value;
        }
    }

    /**
     * The default capacity - MUST be a prime number.
     */
    static final int DEFAULT_CAPACITY = 17;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a prime number <= 1<<30.
     */
    static final int MAX_CAPACITY = 2147483629; //max prime number that is smaller than Integer.MAX_VALUE

    /**
     * The number of key-value mappings contained in this hash table.
     */
    private int size;

    /**
     * The table that stores key-value pairs
     */
    private Entry[] table;

    /**
     * The number that is used to calculate the second hash function.
     * This number is the largest prime number, less than the capacity.
     */
    private int primeForHash;

    /**
     * Constructs an empty <tt>DoubleHashingHashTable</tt> with the
     * capacity that is a prime number greater than or equal to the
     * desiredCapacity.
     *
     * @param desiredCapacity the desired capacity. The capacity
     *                        will be a prime number greater than or equal to the
     *                        desiredCapacity.
     * @throws IllegalArgumentException if the initial capacity is not positive
     *                                  or greater than <tt>MAX_CAPACITY</tt>.
     */
    public DoubleHashingHashTable(int desiredCapacity) {
        if (desiredCapacity <= 0 || desiredCapacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Illegal desired capacity: " + desiredCapacity);
        }
        int capacity;
        if (isPrime(desiredCapacity)) {
            capacity = desiredCapacity;
        } else {
            capacity = nextPrime(desiredCapacity);
        }
        table = new Entry[capacity];
        if (capacity < 3) {
            primeForHash = 1;
        } else {
            primeForHash = prevPrime(capacity);
        }
    }

    /**
     * Constructs an empty <tt>DoubleHashingHashTable</tt> with the default
     * capacity (17).
     */
    public DoubleHashingHashTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Returns the number of key-value mappings in this hash table.
     *
     * @return the number of key-value mappings in this hash table
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this hash table contains no key-value mappings.
     *
     * @return <tt>true</tt> if this hash table contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this hash table has no space to add a new key.
     *
     * @return <tt>true</tt> if this hash table has no space to add a new key
     */
    public boolean isFull() {
        return size == table.length;
    }

    /**
     * Returns the hash table capacity.
     *
     * @return the hash table capacity
     */
    public int capacity() {
        return table.length;
    }

    /**
     * Returns <tt>true</tt> if this hash table contains a mapping for the
     * specified key.
     *
     * @param key The key whose presence in this hash table is to be tested
     * @return <tt>true</tt> if this hash table contains a mapping for the specified
     * key.
     */
    @Override
    public boolean containsKey(int key) {
        return keyPosition(key) >= 0;
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key The key whose presence in this hash table
     * @throws IllegalArgumentException if this hash table contains
     *                                  no mapping for the key.
     * @see #put(int, long)
     */
    @Override
    public long get(int key) {
        int position = keyPosition(key);
        if (position < 0) {
            throw new IllegalArgumentException("This hash table contains no mapping for the key = " + key);
        }
        return table[position].value;

    }

    /**
     * Associates the specified value with the specified key in this hash table.
     * If the hash table previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return <tt>true</tt> if the value was associated with the key in
     * this hash table
     */
    @Override
    public boolean put(int key, long value) {
        if (size == table.length) {
            return false;
        }
        int position = findPosition(key);

        if (table[position] != null) {
            if (table[position].key == key) {
                table[position].value = value;
                return true;
            } else {
                return false;
            }
        }

        table[position] = new Entry(key, value);
        size++;
        return true;
    }

    /**
     * Returns a string representation of the contents of the specified hash table.
     */
    @Override
    public String toString() {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .map(Entry::toString)
                .collect(joining(",", "DoubleHashingHashTable[", "]"));
    }

    /**
     * Hash function that is used to get a primary position to probe
     */
    private int firstHash(int key) {
        int hashVal = key % table.length;
        if (hashVal < 0) {
            hashVal += table.length;
        }
        return hashVal;
    }

    /**
     * Hash function that tells how to go about finding an empty slot
     * if a key's primary position has been filled already
     */
    private int secondHash(int key) {
        int hashVal = key % primeForHash;
        if (hashVal < 0) {
            hashVal += primeForHash;
        }
        return primeForHash - hashVal;
    }

    /**
     * Finds the position of the element with the given key. Returns
     * a prime position if the given key is not present in the hash table.
     */
    private int findPosition(int key) {
        int startPos = firstHash(key);
        if (table[startPos] == null
                || table[startPos].key == key) {
            return startPos;
        }
        int probeValue = secondHash(key);
        int currentPos = (startPos + probeValue) % table.length;  //1-th probe

        while (table[currentPos] != null
                && table[currentPos].key != key
                && currentPos != startPos) {
            currentPos = (currentPos + probeValue) % table.length; // i-th probe
        }

        return currentPos;
    }

    /**
     * Finds the position of the element with the given key. Returns <tt>-1</tt>
     * if the given key is not present in the hash table.
     */
    private int keyPosition(int key) {
        int position = findPosition(key);

        if (table[position] != null) {
            if (table[position].key == key) {
                return position;
            } else {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Primality test: tells if the argument is a prime number or not.
     *
     * @param n number to test
     * @return <tt>true</tt> if n is prime. (All numbers < 2 return false).
     */
    boolean isPrime(int n) {
        if (n == 2) {
            return true;
        }
        if (n < 2 || n % 2 == 0) {
            return false;
        }
        //if not, then just check the odds
        int sqrtN = (int) Math.sqrt(n) + 1;
        for (int i = 3; i <= sqrtN; i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    /**
     * Return the smallest prime number greater than n.
     *
     * @param n a positive number
     * @return the smallest prime number greater than n.
     * @throws IllegalArgumentException if n < 0 or not found a prime
     *                                  number in the range (n; Integer.MAX_VALUE]
     */
    int nextPrime(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number should be >=0");
        }
        if (n < 2) {
            return 2;
        }
        for (int i = n + 1 + n % 2; i < Integer.MAX_VALUE; i += 2) {
            if (isPrime(i)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Not found a prime number in the range (" + n + "; Integer.MAX_VALUE]");
    }

    /**
     * Return the biggest prime number smaller than n.
     *
     * @param n a positive number
     * @return the biggest prime number smaller than n.
     * @throws IllegalArgumentException if n < 3
     */
    int prevPrime(int n) {
        if (n < 3) {
            throw new IllegalArgumentException("Illegal value n:" + n);
        }
        for (int i = n - 1 - n % 2; i >= 3; i -= 2) {
            if (isPrime(i)) {
                return i;
            }

        }
        return 2;
    }
}
