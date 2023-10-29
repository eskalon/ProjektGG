package de.eskalon.gg.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class CollectionUtilsTest {

	@Test
	public void testKeyByValue() {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(1, 1);
		map.put(2, 4);
		map.put(3, 9);
		map.put(4, 16);

		assertEquals((Integer) 1, CollectionUtils.getKeyByValue(map, 1));
		assertEquals((Integer) 2, CollectionUtils.getKeyByValue(map, 4));
		assertEquals((Integer) 3, CollectionUtils.getKeyByValue(map, 9));
		assertEquals((Integer) 4, CollectionUtils.getKeyByValue(map, 16));
		assertEquals(null, CollectionUtils.getKeyByValue(map, 5));
	}

	@Test
	public void testSortByValue() {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(1, 16);
		map.put(2, 8);
		map.put(3, 9);
		map.put(4, 5);
		map.put(5, 15);

		map = CollectionUtils.sortByValue(map);

		Integer[] tmp = map.keySet().toArray(new Integer[5]);

		assertEquals((Integer) 1, tmp[4]);
		assertEquals((Integer) 5, tmp[3]);
		assertEquals((Integer) 3, tmp[2]);
		assertEquals((Integer) 2, tmp[1]);
		assertEquals((Integer) 4, tmp[0]);
	}

	@Test
	public void testFindMostPopular() {
		Integer[] array = { 1, 2, 2, 3, 4, 5, 5, 5, 6, 7, 7, 8 };
		List<Integer> list = new ArrayList<>(Arrays.asList(array));

		assertEquals((Integer) 5, CollectionUtils.findMostPopularElement(list));
	}

}
