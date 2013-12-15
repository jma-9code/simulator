package tools;

import java.util.Comparator;

public class ComparatorFactory {

	private static Comparator<Object> withToStringComparator = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null) {
				return -1;
			}

			if (o2 == null) {
				return 1;
			}

			return o1.toString().compareTo(o2.toString());
		};
	};

	public static Comparator<Object> withToString() {
		return withToStringComparator;
	}
}
