package com.imkiva.playground.toys;

import java.util.Arrays;
import java.util.Random;

/**
 * @author kiva
 * @date 2019-04-10
 */
public class Sort {
    public static void main(String[] args) {
        testQuickSort();
        testMergeSort();
        testHeapSort();
    }

    private static void quickSort(int[] arrays) {
        quickSort(arrays, 0, arrays.length - 1);
    }

    private static void quickSort(int[] arrays, int start, int end) {
        if (start >= end) {
            return;
        }
        int middleIndex = quickSortImpl(arrays, start, end);
        quickSort(arrays, start, middleIndex - 1);
        quickSort(arrays, middleIndex + 1, end);
    }

    private static int quickSortImpl(int[] arrays, int start, int end) {
        int middleValue = arrays[start];
        while (start < end) {
            while (arrays[end] >= middleValue && start < end) {
                end--;
            }
            arrays[start] = arrays[end];
            while (arrays[start] <= middleValue && start < end) {
                start++;
            }
            arrays[end] = arrays[start];
        }
        arrays[start] = middleValue;
        return start;
    }

    private static void mergeSort(int[] data, int left, int right) {
        if (left < right) {
            int half = (left + right) / 2;
            mergeSort(data, left, half);
            mergeSort(data, half + 1, right);
            merge(data, left, right);
        }
    }

    private static void merge(int[] a, int l, int h) {
        int mid = (l + h) / 2;
        int i = l;
        int j = mid + 1;
        int count = 0;
        int[] temp = new int[h - l + 1];
        while (i <= mid && j <= h) {
            if (a[i] < a[j]) {
                temp[count++] = a[i++];
            } else {
                temp[count++] = a[j++];
            }
        }
        while (i <= mid) {
            temp[count++] = a[i++];
        }
        while (j <= h) {
            temp[count++] = a[j++];
        }
        count = 0;
        while (l <= h) {
            a[l++] = temp[count++];
        }
    }

    public static class HeapSort {
        private int[] a;
        private static int n;
        private static int left;
        private static int right;
        private static int largest;

        public HeapSort(int[] array) {
            this.a = array;
        }

        void init(int[] a) {
            n = a.length - 1;
            for (int i = n / 2; i >= 0; i--) {
                maxHeap(a, i);
            }
        }

        void maxHeap(int[] a, int i) {
            left = 2 * i;
            right = 2 * i + 1;
            if (left <= n && a[left] > a[i]) {
                largest = left;
            } else {
                largest = i;
            }

            if (right <= n && a[right] > a[largest]) {
                largest = right;
            }
            if (largest != i) {
                exchange(i, largest);
                maxHeap(a, largest);
            }
        }

        void exchange(int i, int j) {
            int t = a[i];
            a[i] = a[j];
            a[j] = t;
        }

        public void sort() {
            init(a);
            for (int i = n; i > 0; i--) {
                exchange(0, i);
                n = n - 1;
                maxHeap(a, 0);
            }
        }
    }

    private static void testQuickSort() {
        System.out.println(":: Testing QuickSort");
        Random random = new Random();
        int[] array = new int[16];
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextInt(100);
        }

        System.out.println("Before sort: " + Arrays.toString(array));
        long then = System.nanoTime();
        quickSort(array);
        long now = System.nanoTime();
        System.out.println("After sort : " + Arrays.toString(array));
        System.out.println("Time used  : " + (now - then) + "ns");
    }

    private static void testMergeSort() {
        System.out.println(":: Testing MergeSort");
        Random random = new Random();
        int[] array = new int[16];
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextInt(100);
        }

        System.out.println("Before sort: " + Arrays.toString(array));
        long then = System.nanoTime();
        mergeSort(array, 0, array.length - 1);
        long now = System.nanoTime();
        System.out.println("After sort : " + Arrays.toString(array));
        System.out.println("Time used  : " + (now - then) + "ns");
    }

    private static void testHeapSort() {
        System.out.println(":: Testing HeapSort");
        Random random = new Random();
        int[] array = new int[16];
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextInt(100);
        }

        System.out.println("Before sort: " + Arrays.toString(array));
        long then = System.nanoTime();
        new HeapSort(array).sort();
        long now = System.nanoTime();
        System.out.println("After sort : " + Arrays.toString(array));
        System.out.println("Time used  : " + (now - then) + "ns");
    }
}
