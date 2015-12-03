package wsu_airpact_project.airpact_demo;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 *  Created by Agent1729 on 11/29/2015.
 */
public class UITests
{
	// Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
	@Rule
	public ActivityTestRule<TabActivity> activityTestRule =
			new ActivityTestRule<>(TabActivity.class);

	@Test
	public void testLabels()
	{
		onView(withId(R.id.textViewAQILabel)).check(matches(withText("AQI:")));
	}

	@Test
	public void testSearchEditText()
	{
		onView(withId(R.id.editTextCitySearch)).perform(typeText("Bend OR")).check(matches(withText("Bend OR")));
	}

	@Test
	public void testSearchEditTextClearAndType()
	{
		onView(withId(R.id.editTextCitySearch)).perform(typeText("Boise ID"));
		onView(withId(R.id.buttonClearSearch)).perform(click());
		onView(withId(R.id.editTextCitySearch)).check(matches(withText("")));
		onView(withId(R.id.editTextCitySearch)).perform(typeText("Vancouver WA")).check(matches(withText("Vancouver WA")));
	}

	@Test
	public void testForecastButton()
	{
		onView(withId(R.id.toggleButtonForecast)).perform(click());
		onView(withId(R.id.LayoutForecast)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withId(R.id.LayoutCurrent)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
		onView(withId(R.id.toggleButtonCurrent)).perform(click());
		onView(withId(R.id.LayoutForecast)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
		onView(withId(R.id.LayoutCurrent)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withId(R.id.toggleButtonCurrent)).perform(click());
		onView(withId(R.id.LayoutForecast)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
		onView(withId(R.id.LayoutCurrent)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@Test
	public void testHelpScroll() throws Throwable
	{
		selectTab(2);
		onView(withId(R.id.textViewPM25Info)).perform(scrollTo()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	public void selectTab(final int n) throws Throwable
	{
		this.activityTestRule.runOnUiThread(new Runnable()
		{
			public void run()
			{
				Globals.tabActivity.mViewPager.setCurrentItem(n);
			}
		});
	}
}
