package uk.co.datumedge.hamcrest.json;

import static uk.co.datumedge.hamcrest.json.JSONArrayAssertComparator.actualJSONArraySameAsExpected;
import static uk.co.datumedge.hamcrest.json.JSONObjectAssertComparator.actualJSONObjectSameAsExpected;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Matcher that asserts that one JSON document is the same as another.
 *
 * @param <T>
 *            the type of the JSON document. This is typically {@code JSONObject}, {@code JSONArray} or {@code String}.
 */
public final class SameJSONAs<T> extends TypeSafeDiagnosingMatcher<T> {
	private final T expected;
	private final JSONComparator<T> comparator;

	public SameJSONAs(T expected, JSONComparator<T> comparator) {
		this.expected = expected;
		this.comparator = comparator;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected.toString());
	}

	@Override
	protected boolean matchesSafely(T actual, Description mismatchDescription) {
		try {
			JSONComparisonResult result = comparator.compare(expected, actual);
			if (result.failed()) {
				mismatchDescription.appendDescriptionOf(result);
			}
			return result.passed();
		} catch (JSONException e) {
			StringWriter out = new StringWriter();
			e.printStackTrace(new PrintWriter(out));
			mismatchDescription.appendText(out.toString());
			return false;
		}
	}

	/**
	 * Creates a matcher that allows any element ordering within JSON arrays. For example,
	 * <code>{"fib":[0,1,1,2,3]}</code> will match <code>{"fib":[3,1,0,2,1]}</code>.
	 *
	 * @return the configured matcher
	 */
	public SameJSONAs<T> allowingAnyArrayOrdering() {
		return new SameJSONAs<T>(expected, comparator.butAllowingAnyArrayOrdering());
	}

	/**
	 * Creates a matcher that allows fields not present in the expected JSON document.  For example, if the expected
	 * document is
<pre>{
    "name" : "John Smith",
    "address" : {
        "street" : "29 Acacia Road"
    }
}</pre>
	 * then the following document will match:
<pre>{
    "name" : "John Smith",
    "age" : 34,
    "address" : {
        "street" : "29 Acacia Road",
        "city" : "Huddersfield"
    }
}</pre>
	 *
	 * All array elements must exist in both documents, so the expected document
<pre>[
    { "name" : "John Smith" }
]</pre>
	 *  will not match the actual document
<pre>[
    { "name" : "John Smith" },
    { "name" : "Bob Jones" }
]</pre>
	 *
	 * @param expected
	 *            the expected {@code JSONObject} instance
	 * @return the {@code Matcher} instance
	 */
	public SameJSONAs<T> allowingExtraUnexpectedFields() {
		return new SameJSONAs<T>(expected, comparator.butAllowingExtraUnexpectedFields());
	}

	/**
	 * Creates a matcher that compares {@code JSONObject}s.
	 *
	 * @param expected the expected {@code JSONObject} instance
	 * @return the {@code Matcher} instance
	 */
	@Factory
	public static SameJSONAs<JSONObject> sameJSONObjectAs(JSONObject expected) {
		return new SameJSONAs<JSONObject>(expected, actualJSONObjectSameAsExpected());
	}

	/**
	 * Creates a matcher that compares {@code JSONArray}s.
	 *
	 * @param expected the expected {@code JSONArray} instance
	 * @return the {@code Matcher} instance
	 */
	@Factory
	public static SameJSONAs<JSONArray> sameJSONArrayAs(JSONArray expected) {
		return new SameJSONAs<JSONArray>(expected, actualJSONArraySameAsExpected());
	}

	@Factory
	public static Matcher<? super JSONArray> sameJSONArrayAs(JSONArray expected, JSONComparator<JSONArray> jsonComparator) {
		return new SameJSONAs<JSONArray>(expected, jsonComparator);
	}
}
