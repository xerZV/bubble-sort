import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Integer[] arr = BubbleSort.generatearrayWithNRandomNumbers(20);
        BubbleSort.print(arr);
        BubbleSort.sortAndPrint(arr, OrderType.ASCENDING);
        System.out.println();

//        testSorting();
        testLambdaVsOptimized(10, 20_000);
    }

    private static void testSorting() {
        Integer[] original = BubbleSort.generatearrayWithNRandomNumbers(10);

        Integer[] arr = Arrays.copyOf(original, original.length);
        BubbleSort.print(arr);
        BubbleSort.sortAndPrint(arr, OrderType.ASCENDING);
        System.out.println();

        arr = Arrays.copyOf(original, original.length);
        BubbleSort.print(arr);
        BubbleSort.sortAndPrint(arr, OrderType.DESCENDING);
        System.out.println();

        arr = Arrays.copyOf(original, original.length);
        BubbleSort.print(arr);
        BubbleSort.optimizedSortAndPrint(arr, OrderType.ASCENDING);
        System.out.println();

        arr = Arrays.copyOf(original, original.length);
        BubbleSort.print(arr);
        BubbleSort.optimizedSortAndPrint(arr, OrderType.DESCENDING);
        System.out.println();
    }

    private static void testLambdaVsOptimized(int benchmarkIteration, int howManyNumbers) {
        System.out.println("Test lambda bubble sort vs optimized bubble sort");
        Integer[] arr = BubbleSort.generatearrayWithNRandomNumbers(howManyNumbers);

        pre(2);

        long avgLambda = 0l;
        long avgOptimized = 0l;

        for (int i = 0; i < benchmarkIteration; i++) {
            System.out.println("Benchmark iterate: " + i);
            Integer[] arrForOptimized = Arrays.copyOf(arr, arr.length);
            long start = System.currentTimeMillis();
            BubbleSort.optimizedSort(arrForOptimized, OrderType.ASCENDING);
            avgOptimized += System.currentTimeMillis() - start;

            Integer[] arrForLambda = Arrays.copyOf(arr, arr.length);
            start = System.currentTimeMillis();
            BubbleSort.sort(arrForLambda, OrderType.ASCENDING);
            avgLambda += System.currentTimeMillis() - start;
        }

        System.out.format("Avg sort time for lambda: %.2f seconds", ((avgLambda / 10.0)) / 1000.0);
        System.out.println();
        System.out.format("Avg sort time for optimized: %.2f seconds", ((avgOptimized / 10.0)) / 1000.0);
        System.out.println();
    }

    private static void pre(int i) {
        Integer[] arr = BubbleSort.generatearrayWithNRandomNumbers(2000);
        for (int j = 0; j < i; j++) {
            BubbleSort.sort(arr, OrderType.DESCENDING);
            BubbleSort.optimizedSort(arr, OrderType.ASCENDING);
        }
    }
}

class BubbleSort {
    private BubbleSort() {
    }

    public static Integer[] generatearrayWithNRandomNumbers(int n) {
        return new Random().ints(n, -100, 100).boxed().toArray(Integer[]::new);
    }

    private static int sort(Integer[] array, Predicate<Integer> orderType) {
        AtomicInteger swapCount = new AtomicInteger();

        int length = array.length;
        IntStream.range(0, length - 1)
                .flatMap(i -> IntStream.range(1, length - i))
                .forEach(j -> {
                    if (orderType.test(j)) {
                        int temp = array[j];
                        array[j] = array[j - 1];
                        array[j - 1] = temp;
                        swapCount.getAndIncrement();
                    }
                });

        return swapCount.get();
    }

    public static int sort(Integer[] array, OrderType orderType) {
        if (orderType.equals(OrderType.ASCENDING) || orderType == null)
            return sort(array, i -> array[i - 1] > array[i]);

        return sort(array, i -> array[i - 1] < array[i]);
    }

    public static void sortAndPrint(Integer[] array, OrderType orderType) {
        int swapCount = sort(array, orderType != null ? orderType : OrderType.ASCENDING);
        System.out.println("Array is sorted in " + swapCount + " swaps.");
        print(array, orderType);
    }

    private static int optimizedSort(Integer[] array, Predicate<Integer> orderType) {
        int i = 0, length = array.length;
        AtomicInteger swapCount = new AtomicInteger();

        boolean swapNeeded = true;
        while (i < length - 1 && swapNeeded) {
            swapNeeded = false;
            for (int j = 1; j < length - i; j++) {
                if (orderType.test(j)) {
                    int temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                    swapCount.getAndIncrement();
                    swapNeeded = true;
                }
            }
            if (!swapNeeded) {
                break;
            }
            i++;
        }

        return swapCount.get();
    }

    public static int optimizedSort(Integer[] array, OrderType orderType) {
        if (orderType.equals(OrderType.ASCENDING) || orderType == null)
            return optimizedSort(array, i -> array[i - 1] > array[i]);

        return optimizedSort(array, i -> array[i - 1] < array[i]);
    }

    public static void optimizedSortAndPrint(Integer[] array, OrderType orderType) {
        int swapCount = optimizedSort(array, orderType != null ? orderType : OrderType.ASCENDING);
        System.out.println("Array is sorted in " + swapCount + " swaps.");
        print(array, orderType);
    }

    private static void print(Integer[] array, OrderType orderType) {
        if (array != null)
            System.out.println("sorted array: {" + Arrays.stream(array).map(integer -> integer.toString()).collect(Collectors.joining(", ")) + "} " + orderType.getTypeCode());
    }

    public static void print(Integer[] array) {
        if (array != null)
            System.out.println("sorted array: {" + Arrays.stream(array).map(integer -> integer.toString()).collect(Collectors.joining(", ")) + "} ");
    }
}

enum OrderType {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    OrderType(String typeCode) {
        this.typeCode = typeCode;
    }

    private String typeCode;

    public static OrderType getSortTypeByCode(final String typeCode) {
        for (OrderType orderType : values()) {
            if (orderType.getTypeCode().equalsIgnoreCase(typeCode))
                return orderType;
        }

        return ASCENDING;
    }

    public String getTypeCode() {
        return this.typeCode;
    }
}
