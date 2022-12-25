package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.math.BigInteger;
import java.util.*;

public class Day25 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day25().run();
	}
	
	public Day25() {
		super(2022, 25);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	private final Map<Character, Integer> MAP_TO_VAL = Map.of('2', 2, '1', 1, '0', 0, '-', -1, '=', -2);
	private final Map<Integer, Character> MAP_TO_CHR = Map.of(2, '2', 1, '1', 0, '0', -1, '-', -2, '=');
	private final Map<Integer, BigInteger> MIN_MAX = new HashMap<>();
	private final BigInteger FIVE = BigInteger.valueOf(5);
	
	public BigInteger fromSNAFU(String snafu) {
		BigInteger val = BigInteger.valueOf(0);
		for (int i = 0; i < snafu.length(); i++) {
			BigInteger power = BigInteger.valueOf(5).pow(snafu.length() - 1 - i);
			val = val.add(power.multiply(BigInteger.valueOf(MAP_TO_VAL.get(snafu.charAt(i)))));
		}
		
		return val;
	}
	
	private String getSNAFU(BigInteger value, int exp) {
		if (value.signum() == 0) {
			return "0".repeat(exp + 1);
		}
		
		if (exp == 0) {
			Character c = MAP_TO_CHR.get(value.intValue());
			return c == null ? null : c.toString();
		} else {
			// For all five options do
			for (int i = -2; i <= 2; i++) {
				BigInteger tmp = value.subtract(FIVE.pow(exp).multiply(BigInteger.valueOf(i)));
				
				// Check if tmp is outside the bounds for the next lowest exponent
				if (tmp.abs().compareTo(MIN_MAX.get(exp - 1)) > 0) {
					// Impossible
					continue;
				}
				
				String val = getSNAFU(tmp, exp - 1);
				if (val != null) {
					return MAP_TO_CHR.get(i) + "" + val;
				}
			}
		}
		
		return null;
	}
	
	public String toSNAFU(BigInteger val) {
		final int CACHE_SIZE = 100;
		if (MIN_MAX.isEmpty()) {
			BigInteger max = BigInteger.ZERO;
			
			// Calculate max for all exponents
			for (int i = 0; i < CACHE_SIZE; i++) {
				max = max.add(FIVE.pow(i));
				MIN_MAX.put(i, max.multiply(BigInteger.TWO));
			}
		}
		
		String result = getSNAFU(val, CACHE_SIZE);
		
		// Remove leading zeros
		return result == null ? null : result.replace('0', ' ').stripLeading().replace(' ', '0');
	}
	
	// Solve: 43 min
	public String partOne(List<String> lines) throws Exception {
		BigInteger total = BigInteger.ZERO;
		for (String line : lines) {
			total = total.add(fromSNAFU(line));
		}
		
		return toSNAFU(total);
	}
	
	public long partTwo(List<String> lines) throws Exception {
		return 0;
	}
}
