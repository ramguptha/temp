package test.com.absolute.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.util.ArrayUtilities;

public class ArrayUtilitiesTest {
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_long_to_list() {
		long[] data = new long[] {1L, 2L, 3L};

		List<Long> list = ArrayUtilities.<Long>toList(data);
		
		assertEquals(data.length, list.size());
		
		int i = 0;
		for (Long l : list){
			assertTrue(data[i] == l);
			i++;
		}
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_int_to_list() {
		int[] data = new int[] {1, 2, 3};

		List<Integer> list = ArrayUtilities.<Integer>toList(data);
		
		assertEquals(data.length, list.size());
		
		int i = 0;
		for (int l : list){
			assertTrue(data[i] == l);
			i++;
		}
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_short_to_list() {
		short[] data = new short[] {1, 2, 3};

		List<Short> list = ArrayUtilities.<Short>toList(data);
		
		assertEquals(data.length, list.size());
		
		int i = 0;
		for (short l : list){
			assertTrue(data[i] == l);
			i++;
		}
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_float_to_list() {
		float[] data = new float[] {1.1F, 2.1F, 3.1F};

		List<Float> list = ArrayUtilities.<Float>toList(data);
		
		assertEquals(data.length, list.size());
		
		int i = 0;
		for (float l : list){
			assertTrue(data[i] == l);
			i++;
		}
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_convert_doule_to_list() {
		double[] data = new double[] {1.1D, 2.1D, 3.1D};

		List<Double> list = ArrayUtilities.<Double>toList(data);
		
		assertEquals(data.length, list.size());
		
		int i = 0;
		for (double l : list){
			assertTrue(data[i] == l);
			i++;
		}
	}
}
