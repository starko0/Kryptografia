package Logic;

import java.util.Arrays;

public class DES {
    private static final int[] IP = {58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};

    private static final int[] FP = {40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
    private static final int[] PC1 = {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};
    private static final int[] PC2 = {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};

    private static final int[] shiftBits = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    private static final int[][] S = {
            {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13},
            {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9},
            {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12},
            {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14},
            {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3},
            {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13},
            {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12},
            {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
    };

    private static final int[] EP = {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};

    private static final int[] P = {16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25};

    private static int[] permute(int[] input, int[] permutationTable) {
        int[] output = new int[permutationTable.length];
        for (int i = 0; i < permutationTable.length; i++) {
            output[i] = input[permutationTable[i] - 1];
        }
        return output;
    }

    private static int[] leftShift(int[] input, int shiftAmount) {
        int[] output = new int[input.length];
        System.arraycopy(input, 0, output, 0, input.length);
        for (int i = 0; i < shiftAmount; i++) {
            int tempLeft = output[0];
            int tempRight = output[28];
            System.arraycopy(output, 1, output, 0, 27);
            System.arraycopy(output, 29, output, 28, 27);
            output[27] = tempLeft;
            output[55] = tempRight;
        }
        return output;
    }
    private static int[] functionF(int[] input, int[] key) {
        int[] output = new int[32];
        int[] temp = permute(input, EP);
        for (int i = 0; i < 48; i++) {
            temp[i] ^= key[i];
        }
        for (int i = 0; i < 8; i++) {
            int row = (temp[i * 6] << 1) + temp[i * 6 + 5];
            int col = (temp[i * 6 + 1] << 3) + (temp[i * 6 + 2] << 2) + (temp[i * 6 + 3] << 1) + temp[i * 6 + 4];
            int value = S[i][(row * 16) + col];
            int[] bits = new int[4];
            for (int j = 0; j < 4; j++) {
                bits[3 - j] = value % 2;
                value /= 2;
            }
            System.arraycopy(bits, 0, output, i * 4, 4);
        }
        return permute(output, P);
    }

    private static int[][] prepareKeys(byte[] key) {
        int[] fullKey = new int[64];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fullKey[i * 8 + j] = (key[i] >> (7 - j)) & 1;
            }
        }
        fullKey = permute(fullKey, PC1);
        int[][] keys = new int[16][48];
        for (int i = 0; i < 16; i++) {
            fullKey = leftShift(fullKey, shiftBits[i]);
            keys[i] = permute(fullKey, PC2);
        }
        return keys;
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        int[][] keys = prepareKeys(key);
        int[] block = new int[64];
        int paddingLength = 0;

        if (plaintext.length % 8 != 0) {
            paddingLength = 8 - (plaintext.length % 8);
        }
        byte[] paddedPlaintext = new byte[plaintext.length + paddingLength];
        System.arraycopy(plaintext, 0, paddedPlaintext, 0, plaintext.length);
        Arrays.fill(paddedPlaintext, plaintext.length, paddedPlaintext.length, (byte) paddingLength);

        byte[] ciphertext = new byte[paddedPlaintext.length];
        for (int i = 0; i < paddedPlaintext.length; i += 8) {
            for (int j = 0; j < 8; j++) {
                block[j] = (paddedPlaintext[i + j] >> 7) & 1;
                for (int k = 1; k < 8; k++) {
                    block[j + k * 8] = (paddedPlaintext[i + j] >> (7 - k)) & 1;
                }
            }
            block = permute(block, IP);
            int[] left = new int[32];
            int[] right = new int[32];
            System.arraycopy(block, 0, left, 0, 32);
            System.arraycopy(block, 32, right, 0, 32);
            for (int j = 0; j < 16; j++) {
                int[] temp = right;
                right = functionF(right, keys[j]);
                for (int k = 0; k < 32; k++) {
                    right[k] ^= left[k];
                }
                left = temp;
            }
            int[] combined = new int[64];
            System.arraycopy(right, 0, combined, 0, 32);
            System.arraycopy(left, 0, combined, 32, 32);
            combined = permute(combined, FP);
            for (int j = 0; j < 8; j++) {
                ciphertext[i + j] = 0;
                for (int k = 0; k < 8; k++) {
                    ciphertext[i + j] |= (combined[j + k * 8] << (7 - k));
                }
            }
        }

        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        int[][] keys = prepareKeys(key);
        int[] block = new int[64];
        byte[] plaintext = new byte[ciphertext.length];

        int plaintextIndex = 0;
        for (int i = 0; i < ciphertext.length; i += 8) {
            for (int j = 0; j < 8; j++) {
                if (i + j < ciphertext.length) {
                    block[j] = (ciphertext[i + j] >> 7) & 1;
                    for (int k = 1; k < 8; k++) {
                        block[j + k * 8] = (ciphertext[i + j] >> (7 - k)) & 1;
                    }
                } else {
                    block[j] = 0;
                    for (int k = 1; k < 8; k++) {
                        block[j + k * 8] = 0;
                    }
                }
            }
            block = permute(block, IP);
            int[] left = new int[32];
            int[] right = new int[32];
            System.arraycopy(block, 0, left, 0, 32);
            System.arraycopy(block, 32, right, 0, 32);
            for (int j = 15; j >= 0; j--) {
                int[] temp = right;
                right = functionF(right, keys[j]);
                for (int k = 0; k < 32; k++) {
                    right[k] ^= left[k];
                }
                left = temp;
            }
            int[] combined = new int[64];
            System.arraycopy(right, 0, combined, 0, 32);
            System.arraycopy(left, 0, combined, 32, 32);
            combined = permute(combined, FP);
            int bytesToCopy = Math.min(8, ciphertext.length - i);
            for (int j = 0; j < bytesToCopy; j++) {
                plaintext[plaintextIndex + j] = 0;
                for (int k = 0; k < 8; k++) {
                    plaintext[plaintextIndex + j] |= (combined[j + k * 8] << (7 - k));
                }
            }
            plaintextIndex += 8;
        }

        int paddingLength = plaintext[plaintext.length - 1];
        if (paddingLength > 0 && paddingLength <= 8) {
            boolean validPadding = true;
            for (int i = plaintext.length - paddingLength; i < plaintext.length; i++) {
                if (plaintext[i] != paddingLength) {
                    validPadding = false;
                    break;
                }
            }
            if (validPadding) {
                byte[] unpadded = new byte[plaintext.length - paddingLength];
                System.arraycopy(plaintext, 0, unpadded, 0, unpadded.length);
                return unpadded;
            }
        }
        return plaintext;
    }
}