import java.util.Arrays;

public class HotstarIntrosort {

    static final int SIZE_THRESHOLD = 16;

    static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }

            arr[j + 1] = key;
        }
    }

    static void heapSort(int[] arr, int left, int right) {
        int[] temp = Arrays.copyOfRange(arr, left, right + 1);
        Arrays.sort(temp);

        for (int i = 0; i < temp.length; i++) {
            arr[left + i] = temp[i];
        }
    }

    static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) {
                i++;

                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[right];
        arr[right] = temp;

        return i + 1;
    }

    static void introsort(int[] arr, int left, int right, int depthLimit) {
        int size = right - left + 1;

        if (size <= SIZE_THRESHOLD) {
            insertionSort(arr, left, right);
            return;
        }

        if (depthLimit == 0) {
            heapSort(arr, left, right);
            return;
        }

        int pivot = partition(arr, left, right);

        introsort(arr, left, pivot - 1, depthLimit - 1);
        introsort(arr, pivot + 1, right, depthLimit - 1);
    }

    public static void main(String[] args) {

        int[] chunks = {24, 12, 18, 31, 7, 39, 15, 22, 5, 28, 11, 35, 19, 9};

        int n = chunks.length;
        int depthLimit = 2 * (int)(Math.log(n) / Math.log(2));

        System.out.println("=== Hotstar Live-Stream Chunk Introsort ===");

        System.out.print("\nInput Chunk Timestamps: ");
        for (int x : chunks) {
            System.out.print(x + " ");
        }

        System.out.println("\n\nDepth Limit = " + depthLimit);

        introsort(chunks, 0, n - 1, depthLimit);

        System.out.print("\nSorted Chunk Timestamps: ");
        for (int x : chunks) {
            System.out.print(x + " ");
        }

        System.out.println("\n\n=== Time Complexity Summary ===");
        System.out.println("Quick Sort Phase       : O(n log n) average");
        System.out.println("Heap Sort Fallback     : O(n log n) worst case");
        System.out.println("Insertion Sort Phase   : O(k^2) for small subarrays");
        System.out.println("Overall Introsort      : O(n log n) worst case");
    }
}
