package hardcoded.aoc2021;

import java.util.*;

import hardcoded.util.Utils;

public class Day20 {
	static class Image {
		final int[] filter;
		boolean hasInfiniteOnes;
		int[] image;
		int width;
		int height;
		
		Image(String filter, List<String> input) {
			this.filter = new int[filter.length()];
			for(int i = 0; i < filter.length(); i++) {
				this.filter[i] = (filter.charAt(i) == '#' ? 1:0);
			}
			this.height = input.size();
			this.width = input.get(0).length();
			this.image = new int[width * height];
			for(int y = 0; y < input.size(); y++) {
				String line = input.get(y);
				for(int x = 0; x < line.length(); x++) {
					this.image[x + y * width] = (line.charAt(x) == '#' ? 1:0);
				}
			}
		}
		
		void expand(int padding) {
			int newWidth = width + padding * 2;
			int newHeight = (image.length / width) + padding * 2;
			int[] newImage = new int[newWidth * newHeight];
			
			// Fill the current image with the specified color
			Arrays.fill(newImage, hasInfiniteOnes ? 1:0);
			
			int offset = newWidth * padding + padding;
			// Fill the original image in the new one
			for(int i = 0; i < height; i++) {
				int newIdx = i * newWidth + offset;
				// Each row is newWidth and we have a padding of paddding 
				System.arraycopy(image, i * width, newImage, newIdx, width);
			}
			
			this.width = newWidth;
			this.height = newHeight;
			this.image = newImage;
		}
		
		int get(int x, int y) {
			if(x < 0 || y < 0 || x >= width || y >= height) {
				return hasInfiniteOnes ? 1:0;
			}
			
			return image[x + y * width];
		}
		
		int getFilter(int x, int y, int r) {
			int index = 0;
			for(int i = -r; i <= r; i++) {
				for(int j = -r; j <= r; j++) {
					index <<= 1;
					index |= get(x + j, y + i);
				}
			}
			
			return filter[index];
		}
		
		void applyFilter(int radius) {
			// First we expand the image so we can write new data
			expand(radius);
			
			int[] newImage = new int[width * height];
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					newImage[x + y * width] = getFilter(x, y, radius);
				}
			}
			
			// Check the zero pattern
			if(hasInfiniteOnes) {
				hasInfiniteOnes = filter[0b111111111] == 1;
			} else {
				hasInfiniteOnes = filter[0b000000000] == 1;
			}
			
			image = newImage;
		}
		
		void print() {
			StringBuilder sb = new StringBuilder();
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					sb.append(image[x + y * width] == 1 ? '#':'.');
				}
				sb.append("\n");
			}
			
			System.out.println(sb);
		}
		
		long countOnes() {
			if(hasInfiniteOnes) {
				return Long.MAX_VALUE;
			}
			
			int count = 0;
			for(int i = 0; i < image.length; i++) {
				count += image[i];
			}
			
			return count;
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day20");
		
		String first = lines.get(0);
		Image image = new Image(first, lines.subList(2, lines.size()));
		
		Utils.printf("Day 20\n");
		Utils.printf("PartOne: %d\n", partOne(image));
		Utils.printf("PartTwo: %d\n", partTwo(image));
	}
	
	// Solve: 54 min
	public static long partOne(Image image) throws Exception {
		image.applyFilter(1);
		image.applyFilter(1);
		return image.countOnes();
	}
	
	// Solve: 1 min
	public static long partTwo(Image image) throws Exception {
		for(int i = 0; i < 48; i++) {
			image.applyFilter(1);
		}
		return image.countOnes();
	}
}
