package uk.co.davidlomas.gammon.test.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.settings.GameSettings;
import uk.co.davidlomas.gammon.test.helpers.Settings;

public class GameSettingsTest {

  @BeforeClass
  public static void beforeClass() {
    Settings.resetSettings();
  }

  @AfterClass
  public static void afterClass() {
    Settings.resetSettings();
  }

  @Before
  public void setup() {
    // reset all values to default as its static and the tests can therefore
    // Influence each other

    GameSettings.setAreBothAIsCache(null);
    GameSettings.setDisplayGUICache(null);
    GameSettings.setIsP1BlackCache(null);
    GameSettings.setMultiThreadingCache(null);

    GameSettings.setTimeDelayCache(-1);
  }

  @Test
  public void settingP1BlackThenGettingReturnsExpected() {
    GameSettings.setIsP1BlackCache(true);
    assertTrue(GameSettings.isP1Black());
  }

  @Test
  public void settingDisplayGuiThenGettingReturnsExpected() {
    GameSettings.setDisplayGUICache(false);
    assertFalse(GameSettings.getDisplayGUI());
  }

  @Test
  public void settingAreBothAIsThenGettingReturnsExpected() {
    GameSettings.setAreBothAIsCache(false);
    assertFalse(GameSettings.getAreBothAI());
  }

  @Test
  public void settingMultiThreadingThenGettingReturnsExpected() {
    GameSettings.setMultiThreadingCache(true);
    assertTrue(GameSettings.getMultiThreading());
  }

  @Test
  public void settingTimeDelayThenGettingReturnsExpected() {
    final int newTimeDelayCache = 4;
    GameSettings.setTimeDelayCache(newTimeDelayCache);
    assertEquals(newTimeDelayCache, GameSettings.getTimeDelay());
  }

}
