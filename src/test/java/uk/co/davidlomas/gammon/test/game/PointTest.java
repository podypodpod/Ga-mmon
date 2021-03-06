/*
  GNU General Public License

  This file is part of GA-mmon.

  GA-mmon is free software: you can redistribute it and/or modify it under the
  terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  GA-mmon is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
  A PARTICULAR PURPOSE. See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with
  GA-mmon. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.davidlomas.gammon.test.game;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.davidlomas.gammon.game.Point;
import uk.co.davidlomas.gammon.test.helpers.SettingsUtil;

import static org.junit.Assert.*;

public class PointTest {

    private Point testSubject;

    @BeforeClass
    public static void beforeClass() {
        SettingsUtil.resetSettings();
    }

    @AfterClass
    public static void afterClass() {
        SettingsUtil.resetSettings();
    }

    @Before
    public void setup() {
        testSubject = new Point();
    }

    @Test
    public void shouldAddOnePieceToBlackCount() {
        // Given
        testSubject.setBlackCount(5);

        // When
        testSubject.addBlackPiece();

        // Then
        assertEquals(6, testSubject.getBlackCount());
    }

    @Test
    public void shouldAddOnePieceToBlackCountWhenTrueIsSentIn() {
        // Given
        testSubject.setBlackCount(4);

        // When
        testSubject.addPiece(true);

        // Then
        assertEquals(5, testSubject.getBlackCount());
    }

    @Test
    public void shouldAddOnePieceToRedCount() {
        // Given
        testSubject.setRedCount(3);

        // When
        testSubject.addRedPiece();

        // Then
        assertEquals(4, testSubject.getRedCount());
    }

    @Test
    public void shouldAddOnePieceToRedCountWhenFalseIsSentIn() {
        // Given
        testSubject.setRedCount(4);

        // When
        testSubject.addPiece(false);

        // Then
        assertEquals(5, testSubject.getRedCount());
    }

    @Test
    public void shouldCloneThePoint() {
        // Given
        testSubject.setRedCount(7);
        testSubject.setBlackCount(0);

        final Point newPoint;

        // When
        newPoint = new Point(testSubject);

        // Then
        assertEquals(7, newPoint.getRedCount());
    }

    @Test
    public void shouldRemoveOnePieceFromBlackCountWhenTrueIsSentIn() {
        // Given
        testSubject.setBlackCount(4);

        // When
        testSubject.removePiece(true);

        // Then
        assertEquals(3, testSubject.getBlackCount());
    }

    @Test
    public void shouldRemoveOnePieceFromRedCount() {
        // Given
        testSubject.setRedCount(4);

        // When
        testSubject.removePiece(false);

        // Then
        assertEquals(3, testSubject.getRedCount());
    }

    @Test
    public void shouldReturnAValueOfBlackCountIfGreaterThanRed() {

        // Given
        testSubject.setBlackCount(3);
        testSubject.setRedCount(0);

        // When
        final int x = testSubject.numEither();

        // Then
        assertEquals(3, x);
    }

    @Test
    public void shouldReturnAValueOfRedCountIfGreaterThanBlack() {

        // Given
        testSubject.setBlackCount(0);
        testSubject.setRedCount(5);

        // When
        final int x = testSubject.numEither();

        // Then
        assertEquals(5, x);
    }

    @Test
    public void shouldReturnBooleanFalseWhenColBlackIsPresent() {
        // Given
        testSubject.setRedCount(0);
        testSubject.setBlackCount(1);

        // When
        final Boolean x = testSubject.getCol();

        // Then
        assertEquals(true, x);
    }

    @Test
    public void shouldReturnBooleanTrueWhenColRedIsPresent() {
        // Given
        testSubject.setRedCount(1);
        testSubject.setBlackCount(0);

        // When
        final Boolean x = testSubject.getCol();

        // Then
        assertEquals(false, x);
    }

    @Test
    public void shouldReturnFalseWhenBothRedAndBlackAreNotEmpty() {

        // Given
        testSubject.setBlackCount(3);
        testSubject.setRedCount(45);

        // When
        final boolean b = testSubject.isEmpty();

        // Then
        assertFalse(b);
    }

    @Test
    public void shouldReturnFalseWhenPassedTwoDifferentPointsDifCol() {
        // Given
        testSubject.setBlackCount(3);

        final Point OtherPoint = new Point();
        OtherPoint.setRedCount(4);

        // When
        final boolean x = testSubject.equals(OtherPoint);

        // Then
        assertFalse(x);
    }

    @Test
    public void shouldReturnFalseWhenPassedTwoDifferentPointsWithDifferentNumbers() {
        // Given
        testSubject.setBlackCount(3);

        final Point OtherPoint = new Point();
        OtherPoint.setBlackCount(4);

        // Then
        assertFalse(testSubject.equals(OtherPoint));
    }

    @Test
    public void shouldReturnTrueWhenBothRedAndBlackAreEmpty() {
        // When
        final boolean b = testSubject.isEmpty();

        // Then
        assertTrue(b);
    }

    @Test
    public void shouldReturnTrueWhenPassedTwoIdenticalPoints() {
        // Given
        testSubject.setBlackCount(4);

        final Point OtherPoint = new Point();
        OtherPoint.setBlackCount(4);

        // When
        final boolean x = testSubject.equals(OtherPoint);

        // Then
        assertTrue(x);
    }

    @Test
    public void shouldTakeOnePieceFromBlackCount() {
        // Given
        testSubject.setBlackCount(5);

        // When
        testSubject.removeBlackPiece();

        // Then
        assertEquals(4, testSubject.getBlackCount());
    }

    @Test
    public void shouldTakeOnePieceFromRedCount() {
        // Given
        testSubject.setRedCount(5);

        // When
        testSubject.removeRedPiece();

        // Then
        assertEquals(4, testSubject.getRedCount());
    }
}
