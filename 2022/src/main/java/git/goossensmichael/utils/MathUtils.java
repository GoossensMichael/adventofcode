package git.goossensmichael.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MathUtils {

    public static int gcd(final int a, final int b) {
        if (b==0) {
            return a;
        }

        return gcd(b,a%b);
    }

    public static final int[][] IDENTITY = new int[][] {
            { 1, 0, 0, 0 },
            { 0, 1, 0, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 0, 1 }
    };

    public static final int[][] ROTATE_X = new int[][] {
            { 1, 0,  0, 0 },
            { 0, 0, -1, 0 },
            { 0, 1,  0, 0 },
            { 0, 0,  0, 1 }
    };

    public static final int[][] ROTATE_X_INVERSE = new int[][] {
            { 1,  0,  0, 0 },
            { 0,  0,  1, 0 },
            { 0, -1,  0, 0 },
            { 0,  0,  0, 1 }
    };

    public static final int[][] ROTATE_Y = new int[][] {
            {  0, 0, 1, 0 },
            {  0, 1, 0, 0 },
            { -1, 0, 0, 0 },
            {  0, 0, 0, 1 }
    };

    public static final int[][] ROTATE_Y_INVERSE = new int[][] {
            {  0, 0, -1, 0 },
            {  0, 1,  0, 0 },
            {  1, 0,  0, 0 },
            {  0, 0,  0, 1 }
    };

    public static final int[][] ROTATE_Z = new int[][] {
            { 0, -1, 0, 0 },
            { 1,  0, 0, 0 },
            { 0,  0, 1, 0 },
            { 0,  0, 0, 1 }
    };

    public static final int[][] ROTATE_Z_INVERSE = new int[][] {
            {  0,  1, 0, 0 },
            { -1,  0, 0, 0 },
            {  0,  0, 1, 0 },
            {  0,  0, 0, 1 }
    };

    public static int[] add(final int[] position, final int[] direction) {
        return new int[] { position[0] + direction[0], position[1] + direction[1], position[2] + direction[2] };
    }

    public static int[] cross(final int[] a, final int[] b) {
        return new int[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
    }


    // Inverts a rotation matrix
//    public static int[][] invert(final int[][] m) {
//        return transpose(m);
//    }
//
//    // Switch rows with columns
//    public static int[][] transpose(final int[][] m) {
//        final int[][] t = new int[m[0].length][m.length];
//
//        for (int i = 0; i < m.length; i++) {
//            for (int j = 0; j < m[0].length; j++) {
//                t[j][i] = m[i][j];
//            }
//        }
//
//        return t;
//    }

    public enum Rotation {
        X_CLOCKWISE, X_COUNTERCLOCKWISE, Y_CLOCKWISE, Y_COUNTERCLOCKWISE
    }

    public static int[][] rotationWithRepositioning(final Rotation rotation, final int distance) {
        final int[][] result = switch (rotation) {
            case X_COUNTERCLOCKWISE -> deepCopy(ROTATE_X);
            case X_CLOCKWISE -> deepCopy(ROTATE_X_INVERSE);
            case Y_COUNTERCLOCKWISE -> deepCopy(ROTATE_Y);
            case Y_CLOCKWISE -> deepCopy(ROTATE_Y_INVERSE);
        };

        final int[][] translation = deepCopy(IDENTITY);

        switch (rotation) {
            case X_COUNTERCLOCKWISE:
                translation[1][3] = distance;
                break;
            case X_CLOCKWISE, Y_COUNTERCLOCKWISE:
                translation[2][3] = distance;
                break;
            case Y_CLOCKWISE:
                translation[0][3] = distance;
                break;
        }

        return transform(translation, result);
    }

    private static void applyRepositioning(final char axis, final int distance, final int[][] transformation) {
        switch (axis) {
            case 'x':
                transformation[1][3] = distance;
                break;
            case 'y':
                transformation[2][3] = distance;
                break;
        }
    }

    // Apply matrix to vector
    public static int[] transform(final int[][] m, final int[] p) {
        return new int[] {
                m[0][0] * p[0] + m[0][1] * p[1] + m[0][2] * p[2] + m[0][3] * p[3],
                m[1][0] * p[0] + m[1][1] * p[1] + m[1][2] * p[2] + m[1][3] * p[3],
                m[2][0] * p[0] + m[2][1] * p[1] + m[2][2] * p[2] + m[2][3] * p[3],
                m[3][0] * p[0] + m[3][1] * p[1] + m[3][2] * p[2] + m[3][3] * p[3]
        };
    }

    public static int[][] transform(final int[][] m, final int[][] n) {
        return new int[][] {
                {
                        m[0][0] * n[0][0] + m[0][1] * n[1][0] + m[0][2] * n[2][0] + m[0][3] * n[3][0],
                        m[0][0] * n[0][1] + m[0][1] * n[1][1] + m[0][2] * n[2][1] + m[0][3] * n[3][1],
                        m[0][0] * n[0][2] + m[0][1] * n[1][2] + m[0][2] * n[2][2] + m[0][3] * n[3][2],
                        m[0][0] * n[0][3] + m[0][1] * n[1][3] + m[0][2] * n[2][3] + m[0][3] * n[3][3]
                },
                {
                        m[1][0] * n[0][0] + m[1][1] * n[1][0] + m[1][2] * n[2][0] + m[1][3] * n[3][0],
                        m[1][0] * n[0][1] + m[1][1] * n[1][1] + m[1][2] * n[2][1] + m[1][3] * n[3][1],
                        m[1][0] * n[0][2] + m[1][1] * n[1][2] + m[1][2] * n[2][2] + m[1][3] * n[3][2],
                        m[1][0] * n[0][3] + m[1][1] * n[1][3] + m[1][2] * n[2][3] + m[1][3] * n[3][3]
                },
                {
                        m[2][0] * n[0][0] + m[2][1] * n[1][0] + m[2][2] * n[2][0] + m[2][3] * n[3][0],
                        m[2][0] * n[0][1] + m[2][1] * n[1][1] + m[2][2] * n[2][1] + m[2][3] * n[3][1],
                        m[2][0] * n[0][2] + m[2][1] * n[1][2] + m[2][2] * n[2][2] + m[2][3] * n[3][2],
                        m[2][0] * n[0][3] + m[2][1] * n[1][3] + m[2][2] * n[2][3] + m[2][3] * n[3][3]
                },
                {
                        m[3][0] * n[0][0] + m[3][1] * n[1][0] + m[3][2] * n[2][0] + m[3][3] * n[3][0],
                        m[3][0] * n[0][1] + m[3][1] * n[1][1] + m[3][2] * n[2][1] + m[3][3] * n[3][1],
                        m[3][0] * n[0][2] + m[3][1] * n[1][2] + m[3][2] * n[2][2] + m[3][3] * n[3][2],
                        m[3][0] * n[0][3] + m[3][1] * n[1][3] + m[3][2] * n[2][3] + m[3][3] * n[3][3]
                }
        };
    }



    public static void printMatrix(final int[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void printVector(final int[] v) {
        System.out.println(Arrays.stream(v)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", ", "(", ")")));
    }

    public static int[][] deepCopy(final int[][] original) {
        final int[][] copy = new int[original.length][original[0].length];

        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = original[i][j];
            }
        }

        return copy;
    }

    public static void main(final String[] args) {
        final int[] a = new int[] { 0, 0, 1 };
        {
            final int[] b = new int[] { 1, 0, 0 };
            System.out.println("Facing X");
            printVector(cross(a, b));
        }

        {
            final int[] b = new int[] { 0, 1, 0 };
            System.out.println("Facing Y");
            printVector(cross(a, b));
        }

        {
            final int[] b = new int[] { -1, 0, 0 };
            System.out.println("Facing -X");
            printVector(cross(a, b));
        }

        {
            final int[] b = new int[] { 0, -1, 0 };
            System.out.println("Facing -Y");
            printVector(cross(a, b));
        }
    }

}
