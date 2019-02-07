package com.gmail.onishchenko.oleksii.hashmap;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class DoubleHashingHashTableTest {

    private DoubleHashingHashTable instance;

    @BeforeEach
    void setUp() {
        instance = new DoubleHashingHashTable();
    }

    @Nested
    class CreatingHashTable {
        @Test
        void defaultConstructor() {
            //Then
            assertThat(instance.capacity()).isEqualTo(DoubleHashingHashTable.DEFAULT_CAPACITY);
        }

        @ParameterizedTest
        @ValueSource(ints = {-10, -2, 0, DoubleHashingHashTable.MAX_CAPACITY + 1})
        void illegalDesiredCapacity(int capacity) {
            //When
            assertThrows(IllegalArgumentException.class, () -> new DoubleHashingHashTable(capacity));
        }

        @DisplayName("success")
        @TestFactory
        List<DynamicTest> success() {
            return asList(
                    createSuccessTest(1, 2),
                    createSuccessTest(2, 2),
                    createSuccessTest(3, 3),
                    createSuccessTest(4, 5),
                    createSuccessTest(178909, 178909),
                    createSuccessTest(178910, 178921)
            );
        }

        private DynamicTest createSuccessTest(int desiredCapacity, int expectedCapacity) {
            return dynamicTest("desiredCapacity = " + desiredCapacity, () -> {
                //When
                instance = new DoubleHashingHashTable(desiredCapacity);

                //Then
                assertThat(instance.capacity()).isEqualTo(expectedCapacity);
            });
        }
    }

    @DisplayName(value = "Testing int size(); boolean isEmpty(); boolean isFull()")
    @Nested
    class Size {
        @Test
        void afterCreating() {
            //Then
            assertThat(instance.size()).isEqualTo(0);
            assertThat(instance.isEmpty()).isTrue();
            assertThat(instance.isFull()).isFalse();

        }

        @Test
        void addOneElement() {
            //Given
            instance.put(10, 123);

            //Then
            assertThat(instance.size()).isEqualTo(1);
            assertThat(instance.isEmpty()).isFalse();
            assertThat(instance.isFull()).isFalse();
        }


        @Test
        void addSeveralElements() {
            //Given
            instance.put(10, 111);
            instance.put(15, 222);
            instance.put(20, 333);

            //Then
            assertThat(instance.size()).isEqualTo(3);
            assertThat(instance.isEmpty()).isFalse();
            assertThat(instance.isFull()).isFalse();
        }

        @Test
        void addElementsWithSameKey() {
            //Given
            instance.put(10, 111);
            instance.put(15, 222);
            instance.put(10, 333);

            //Then
            assertThat(instance.size()).isEqualTo(2);
            assertThat(instance.isEmpty()).isFalse();
            assertThat(instance.isFull()).isFalse();
        }

        @Test
        void fullHashTable() {
            //Given
            int size = instance.capacity();
            for (int i = 0; i < size; i++) {
                instance.put(i, i * i);
            }

            //Then
            assertThat(instance.size()).isEqualTo(size);
            assertThat(instance.isEmpty()).isFalse();
            assertThat(instance.isFull()).isTrue();
        }
    }

    @DisplayName(value = "boolean containsKey(int);")
    @Nested
    class ContainsKey {
        @Test
        void emptyHashTable() {
            //When
            boolean result = instance.containsKey(7);

            //Then
            assertThat(result).isFalse();
        }

        @Test
        void keyNotFound() {
            //Given
            instance.put(1, 7);
            instance.put(2, 13);

            //When
            boolean result = instance.containsKey(7);

            //Then
            assertThat(result).isFalse();
        }

        @Test
        void keyIsPresent() {
            //Given
            instance.put(1, 7);
            instance.put(2, 13);
            instance.put(18, 777);

            //When
            boolean result = instance.containsKey(18);

            //Then
            assertThat(result).isTrue();
        }

        @Test
        void fullHashTableWithSameFirstHashKeys() {
            //Given
            int capacity = instance.capacity();
            for (int i = 0; i < capacity; i++) {
                instance.put(-i * capacity, i * i);
            }

            //When
            boolean result = instance.containsKey(capacity);

            //Then
            assertThat(result).isFalse();
        }
    }

    @DisplayName(value = "int get();")
    @Nested
    class Get {
        @Test
        void keyNotFound() {
            //Given
            instance.put(1, 7);
            instance.put(2, 13);

            //When
            assertThrows(IllegalArgumentException.class, () -> instance.get(7));
        }

        @Test
        void keyIsPresent() {
            //Given
            instance.put(1, 7);
            instance.put(2, 13);
            instance.put(18, 777);

            //When
            long result = instance.get(1);

            //Then
            assertThat(result).isEqualTo(7);
        }


        @Test
        void updatedEntry() {
            //Given
            instance.put(1, 7);
            instance.put(2, 13);
            instance.put(18, 777);
            instance.put(18, 10);
            instance.put(18, 19);

            //When
            long result = instance.get(18);

            //Then
            assertThat(result).isEqualTo(19);
        }
    }

    @DisplayName(value = "boolean put();")
    @Nested
    class Put {
        @Test
        void emptyHashTable() {
            //When
            boolean result = instance.put(1, 7);

            //When
            assertThat(result).isTrue();
        }

        @Test
        void fullHashTable() {
            //Given
            instance = new DoubleHashingHashTable(2);
            instance.put(1, 7);
            instance.put(2, 13);

            //When
            boolean result = instance.put(18, 777);

            //Then
            assertThat(result).isFalse();
        }

        @Test
        void keyIsPresent() {
            //Given
            instance.put(-2, 13);

            //When
            boolean result = instance.put(-2, 777);

            //Then
            assertThat(result).isTrue();
        }

        @Test
        void keyWithSameFirstHash() {
            //Given
            int capacity = instance.capacity();
            boolean result = true;

            //When
            for (int i = 0; i < capacity; i++) {
                result = result & instance.put(i * capacity, i * i);
            }

            //Then
            assertThat(result).isTrue();
            for (int i = 0; i < capacity; i++) {
                long value = instance.get(i * capacity);
                assertThat(value).isEqualTo(i * i);
            }
        }
    }

    @DisplayName(value = "String toString();")
    @Nested
    class ToString {
        @Test
        void emptyHashTable() {
            //When
            String result = instance.toString();

            //Then
            assertThat(result).isEqualTo("DoubleHashingHashTable[]");
        }

        @Test
        void withElements() {
            //Given
            instance.put(1, 10);
            instance.put(2, 20);

            //When
            String result = instance.toString();

            //Then
            assertThat(result).isEqualTo("DoubleHashingHashTable[1=10,2=20]");
        }
    }

    @DisplayName(value = "boolean isPrime();")
    @Nested
    class IsPrime {
        @ParameterizedTest
        @ValueSource(ints = {2, 3, 5, 7, 631, 4969, 64381, 129803, 213641, 254993, 973051})
        void primeValues(int number) {
            //When
            boolean result = instance.isPrime(number);

            //Then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 4, 9, 630, 4971, 64379, 129801, 213639, 254995, 973027})
        void notPrimeValues(int number) {
            //When
            boolean result = instance.isPrime(number);

            //Then
            assertThat(result).isFalse();
        }
    }

    @DisplayName(value = "int nextPrime(int);")
    @Nested
    class NextPrime {
        @ParameterizedTest
        @ValueSource(ints = {-4971, -64379, Integer.MAX_VALUE - 1})
        void illegalArgument(int number) {
            //When
            assertThrows(IllegalArgumentException.class, () -> instance.nextPrime(number));
        }

        @TestFactory
        List<DynamicTest> success() {
            return asList(
                    createSuccessTest(0, 2),
                    createSuccessTest(1, 2),
                    createSuccessTest(2, 3),
                    createSuccessTest(8, 11),
                    createSuccessTest(31261, 31267),
                    createSuccessTest(970748, 970777),
                    createSuccessTest(178909, 178921)
            );
        }

        private DynamicTest createSuccessTest(int value, int expected) {
            return dynamicTest("starting with " + value, () -> {
                //When
                int result = instance.nextPrime(value);

                //Then
                assertThat(result).isEqualTo(expected);
            });
        }
    }

    @DisplayName(value = "int prevPrime(int);")
    @Nested
    class PrevPrime {
        @ParameterizedTest
        @ValueSource(ints = {-4971, -10, 0, 1, 2})
        void illegalArgument(int number) {
            //When
            assertThrows(IllegalArgumentException.class, () -> instance.prevPrime(number));
        }

        @TestFactory
        List<DynamicTest> success() {
            return asList(
                    createSuccessTest(3, 2),
                    createSuccessTest(12, 11),
                    createSuccessTest(31305, 31277),
                    createSuccessTest(970775, 970747),
                    createSuccessTest(178921, 178909)
            );
        }

        private DynamicTest createSuccessTest(int value, int expected) {
            return dynamicTest("starting with " + value, () -> {
                //When
                int result = instance.prevPrime(value);

                //Then
                assertThat(result).isEqualTo(expected);
            });
        }
    }

    @Test
    void equals() {
        EqualsVerifier.forClass(DoubleHashingHashTable.Entry.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .usingGetClass()
                .verify();
    }
}