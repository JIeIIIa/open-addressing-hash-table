package com.gmail.onishchenko.oleksii.hashmap;

public interface HashTable {

    /**
     * Returns <tt>true</tt> if this hash table contains a mapping for the
     * specified key.
     *
     * @param key The key whose presence in this hash table is to be tested
     * @return <tt>true</tt> if this hash table contains a mapping for the specified
     * key.
     */
    boolean containsKey(int key);


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
    boolean put(int key, long value);

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key The key whose presence in this hash table
     * @throws IllegalArgumentException if this hash table contains
     *                                  no mapping for the key.
     * @see #put(int, long)
     */
    long get(int key);

    /**
     * Returns the number of key-value mappings in this hash table.
     *
     * @return the number of key-value mappings in this hash table
     */
    int size();

    /**
     * Returns <tt>true</tt> if this hash table contains no key-value mappings.
     *
     * @return <tt>true</tt> if this hash table contains no key-value mappings
     */
    boolean isEmpty();
}