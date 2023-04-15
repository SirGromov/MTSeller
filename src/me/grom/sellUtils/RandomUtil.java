package me.grom.sellUtils;

import java.util.Random;

public class RandomUtil {
	public static int RandInt(int start, int end) {
		int diff = end - start;
		Random random = new Random();
		int i = random.nextInt(diff + 1);
		i += start;
		return i;
	}
}
