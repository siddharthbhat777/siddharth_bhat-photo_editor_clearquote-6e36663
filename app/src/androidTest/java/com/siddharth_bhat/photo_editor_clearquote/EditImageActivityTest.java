package com.siddharth_bhat.photo_editor_clearquote;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditImageActivityTest {

    @Rule
    public ActivityTestRule<EditImageActivity> mActivityRule = new ActivityTestRule<>(EditImageActivity.class, false, false);

    @Test
    public void checkIfActivityIsLaunched() {
        mActivityRule.launchActivity(null);
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfBrushIsEnabledWhenClickedOnBrushTool() {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        assertFalse(editImageActivity.mPhotoEditor.getBrushDrawableMode());
        onView(withText(R.string.label_shape)).perform(click());
        assertTrue(editImageActivity.mPhotoEditor.getBrushDrawableMode());
    }

    @Test
    public void checkIfEraserIsEnabledWhenClickedOnEraserTool() {
        mActivityRule.launchActivity(null);
        onView(withText(R.string.label_eraser)).perform(click());
        onView(withText(R.string.label_eraser_mode)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfShapeIsEnabledWhenClickedOnBrushTool() {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        onView(withText(R.string.label_shape)).perform(click());
        onView(withText(R.string.label_shape)).check(matches(isDisplayed()));
    }

    @Test
    public void testWhenNoImageIsSavedThanToastIsVisibleOnClickedOnShareButton() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.imgShare)).perform(click());
        onView(withText(R.string.msg_save_image_to_share)).check(matches(isDisplayed()));
    }

    @Ignore("Need to Fix this test")
    public void testShareIntentWhenImageIsAvailableOnClickedOnShareButton() throws InterruptedException {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        editImageActivity.mSaveImageUri = Uri.parse("somethurl");
        Thread.sleep(2000);
        onView(withId(R.id.imgShare)).perform(click());
    }

    @Test
    public void checkIfPinchTextScalableFlagWorks_False() throws InterruptedException {

        final Intent intent = new Intent();
        intent.putExtra(EditImageActivity.PINCH_TEXT_SCALABLE_INTENT_KEY, false);
        mActivityRule.launchActivity(intent);

        Thread.sleep(2000);
        onView(ViewMatchers.withId(R.id.rvConstraintTools))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(R.string.label_emoji))));
        onView(withText(R.string.label_emoji)).perform(click());

        Thread.sleep(2000);
        onView(withId(R.id.rvEmoji))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        Thread.sleep(1000);
        onView(withId((R.id.frmBorder))).perform(click());

        ViewGroup emojiFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        final float emojiScaleXBeforePinching = emojiFrameParentView.getScaleX();
        final float emojiScaleYBeforePinching = emojiFrameParentView.getScaleY();

        onView(withId((R.id.frmBorder))).perform(PinchTestHelper.pinchOut());

        emojiFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        assertNotEquals(emojiScaleXBeforePinching, emojiFrameParentView.getScaleX());
        assertNotEquals(emojiScaleYBeforePinching, emojiFrameParentView.getScaleY());

        onView(withId(R.id.imgPhotoEditorClose)).perform(click());

        onView(withText(R.string.label_text)).perform(click());
        onView(withId(R.id.add_text_edit_text)).perform(click());
        onView(withId(R.id.add_text_edit_text)).perform(typeText("Test Text"));

        Thread.sleep(2000);
        onView(withId(R.id.add_text_done_tv)).perform(click());

        Matcher<View> testTextBox = withId(R.id.tvPhotoEditorText);
        onView(testTextBox).perform(click());

        ViewGroup textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        final float textScaleXBeforeScaling = textFrameParentView.getScaleX();
        final float textScaleYBeforeScaling = textFrameParentView.getScaleY();

        onView(testTextBox).perform(PinchTestHelper.pinchOut());

        textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        assertEquals(textScaleXBeforeScaling, textFrameParentView.getScaleX(), 0.01);
        assertEquals(textScaleYBeforeScaling, textFrameParentView.getScaleY(), 0.01);
    }

    @Test
    public void checkIfPinchTextScalableFlagWorks_True() throws InterruptedException {

        final Intent intent = new Intent();
        intent.putExtra(EditImageActivity.PINCH_TEXT_SCALABLE_INTENT_KEY, true);
        mActivityRule.launchActivity(intent);

        Thread.sleep(2000);
        onView(withText(R.string.label_text)).perform(click());
        onView(withId(R.id.add_text_edit_text)).perform(click());

        Thread.sleep(2000);
        onView(withId(R.id.add_text_edit_text)).perform(typeText("Test Text"));

        Thread.sleep(2000);
        onView(withId(R.id.add_text_done_tv)).perform(click());

        Matcher<View> testTextBox = withId(R.id.tvPhotoEditorText);
        onView(testTextBox).perform(click());

        ViewGroup textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        final float textScaleXBeforeScaling = textFrameParentView.getScaleX();
        final float textScaleYBeforeScaling = textFrameParentView.getScaleY();

        onView(testTextBox).perform(PinchTestHelper.pinchOut());

        textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        assertNotEquals(textScaleXBeforeScaling, textFrameParentView.getScaleX());
        assertNotEquals(textScaleYBeforeScaling, textFrameParentView.getScaleY());
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}
