import java.awt.Color;
import java.util.Arrays;

import edu.princeton.cs.algs4.*;

public class SeamCarver {
    // RGB of the pixel
    private int[][] color;

    private double[][] energy;

    private int width;

    private int height;
    
    private int[] seamH, seamV;

//    private int[][] edgeToV, edgeToH;
//
//    private double[][] distToV, distToH;
//
    private double distToBottom, distToRight;

    private int edgeToBottom, edgeToRight;

    public SeamCarver(Picture picture) {
        // create a seam carver object based on the given picture
        width = picture.width();
        height = picture.height();
        color = new int[width][height];
        energy = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                color[i][j] = picture.get(i, j).getRGB();
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                energy[i][j] = calculateEnergy(i, j);
            }
        }
        updateH();
        updateV();
    }

    public Picture picture() {
        // current picture
        Picture ret = new Picture(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ret.set(i, j, new Color(color[i][j]));
            }
        }
        return ret;
    }

    public int width() {
        // width of current picture
        return width;
    }

    public int height() {
        // height of current picture
        return height;
    }

    public double energy(int x, int y) {
        // return energy of the pixel
        return energy[x][y];
    }

    // calculate the energy of one pixel
    private double calculateEnergy(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
            return 1000;
        return Math.sqrt(xenergy(x, y) + yenergy(x, y));
    }

    // energy helper function
    private double xenergy(int x, int y) {
        int rgb1 = color[x - 1][y];
        int rgb2 = color[x + 1][y];
        int red = ((rgb1 >> 16) & 0xff) - ((rgb2 >> 16) & 0xff);
        int green = ((rgb1 >> 8) & 0xff) - ((rgb2 >> 8) & 0xff);
        int blue = (rgb1 & 0xff) - (rgb2 & 0xff);
        // System.out.printf("%d, %d, %d, %d, %d",x, y, red, blue, green);
        // System.out.println();
        return (red * red + blue * blue + green * green);
    }

    // energy helper function
    private double yenergy(int x, int y) {
        int rgb1 = color[x][y - 1];
        int rgb2 = color[x][y + 1];
        int red = ((rgb1 >> 16) & 0xff) - ((rgb2 >> 16) & 0xff);
        int green = ((rgb1 >> 8) & 0xff) - ((rgb2 >> 8) & 0xff);
        int blue = (rgb1 & 0xff) - (rgb2 & 0xff);
        // System.out.printf("%d, %d, %d, %d, %d",x, y, red, blue, green);
        // System.out.println();
        return (red * red + blue * blue + green * green);
    }

    public int[] findHorizontalSeam() {
        return Arrays.copyOf(seamH, seamH.length);
    }
    
    public int[] findVerticalSeam() {
        return Arrays.copyOf(seamV, seamV.length);
    }
    
    private int[] findSeamH(int edgeToRight, int[][] edgeToH) {
        // sequence of indices for horizontal seam
        int[] ret = new int[width];
        for (int i = width - 1; i >= 0; i--) {
            ret[i] = edgeToRight;
            edgeToRight = edgeToH[i][edgeToRight];
        }
        return ret;
    }

    private int[] findSeamV(int edgeToBottom, int[][] edgeToV) {
        // sequence of indices for vertical seam
        int[] ret = new int[height];
        for (int i = height - 1; i >= 0; i--) {
            ret[i] = edgeToBottom;
            edgeToBottom = edgeToV[edgeToBottom][i];
        }
        return ret;
    }

    public void removeHorizontalSeam(int[] seam) {
        // remove horizontal seam from current picture
        if (seam == null)
            throw new NullPointerException();
        if (seam.length != width)
            throw new IllegalArgumentException();
        int pre = seam[0];
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height || seam[i]-pre > 1 || seam[i]-pre < -1)
                throw new IllegalArgumentException();
            pre = seam[i];
        }
        for (int i = 0; i < width; i++) {
            for (int j = seam[i]; j < height-1; j++) {
                color[i][j] = color[i][j + 1];
            }
        }
        height--;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                energy[i][j] = calculateEnergy(i, j);
            }
        }
        updateH();
        updateV();
    }

    public void removeVerticalSeam(int[] seam) {
        // remove vertical seam from current picture
        if (seam == null)
            throw new NullPointerException();
        if (seam.length != height)
            throw new IllegalArgumentException();
        int pre = seam[0];
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width || seam[i]-pre > 1 || seam[i]-pre < -1)
                throw new IllegalArgumentException();
            pre = seam[i];
        }
        for (int i = 0; i < height; i++) {
            for (int j = seam[i]; j < width - 1; j++) {
                color[j][i] = color[j + 1][i];
            }
        }
        width--;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                energy[i][j] = calculateEnergy(i, j);
            }
        }
        updateH();
        updateV();
    }

    private void updateV() {
        double[][] distToV = new double[width][height];
        int[][] edgeToV = new int[width][height];
        // initialize distance
        for (int i = 0; i < width; i++) {
            distToV[i][0] = energy[i][0];
            for (int j = 1; j < height; j++) {
                distToV[i][j] = Integer.MAX_VALUE;
            }
        }
        distToBottom = Integer.MAX_VALUE;
        edgeToBottom = -1;
        // visit pixels in topological order
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                relaxV(i, j, distToV, edgeToV);
            }
        }
        seamV = findSeamV(edgeToBottom, edgeToV);
    }

    private void relaxV(int i, int j, double[][] distToV, int[][] edgeToV) {
        if (j == height - 1) {
            if (distToBottom > distToV[i][j]) {
                distToBottom = distToV[i][j];
                edgeToBottom = i;
            }
        } else {
            int row = j + 1;
            for (int k = -1; k <= 1; k++) {
                int col = i + k;
                if (col >= 0 && col < width && distToV[col][row] > distToV[i][j] + energy[col][row]) {
                    distToV[col][row] = distToV[i][j] + energy[col][row];
                    edgeToV[col][row] = i;
                }
            }
        }
    }

    private void updateH() {
        double[][] distToH = new double[width][height];
        int[][] edgeToH = new int[width][height];
        // initialize distance
        for (int i = 1; i < width; i++) {
            for (int j = 0; j < height; j++) {
                distToH[i][j] = Integer.MAX_VALUE;
            }
        }
        distToRight = Integer.MAX_VALUE;
        edgeToRight = -1;
        for (int i = 0; i < height; i++) {
            distToH[0][i] = energy[0][i];
        }
        // visit pixels in topological order
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                relaxH(i, j, distToH, edgeToH);
            }
        }
        seamH = findSeamH(edgeToRight, edgeToH);
    }

    private void relaxH(int i, int j, double[][] distToH, int[][] edgeToH) {
        if (i == width - 1) {
            if (distToRight > distToH[i][j]) {
                distToRight = distToH[i][j];
                edgeToRight = j;
            }
        } else {
            int col = i + 1;
            for (int k = -1; k <= 1; k++) {
                int row = j + k;
                if (row >= 0 && row < height && distToH[col][row] > distToH[i][j] + energy[col][row]) {
                    distToH[col][row] = distToH[i][j] + energy[col][row];
                    edgeToH[col][row] = j;
                }
            }
        }
    }

//    private void printEnergy() {
//        for (int i = 0; i < width; i++) {
//            System.out.println(Arrays.toString(energy[i]));
//        }
//    }
//
//    private void printColor() {
//        for (int i = 0; i < width; i++) {
//            System.out.println(Arrays.toString(color[i]));
//        }
//    }

    public static void main(String[] args) {
        Picture p = new Picture("seamCarving//12x10.png");
        SeamCarver s = new SeamCarver(p);
        // s.printEnergy();
        // s.printColor();
        System.out.println(Arrays.toString(s.findHorizontalSeam()));
        System.out.println(Arrays.toString(s.findVerticalSeam()));
    }

}