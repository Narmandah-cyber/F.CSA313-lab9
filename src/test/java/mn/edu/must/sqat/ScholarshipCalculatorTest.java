package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Хэсэг A — зөвхөн хэвийн урсгалын (happy path) тестүүд.
 * Зорилго нь анхны хамралтыг хэмжих: алдааны замууд, хязгаарын утгууд
 * энд ЗОРИУД ороогүй тул JaCoCo тайлан улаан, шар мөрүүдтэй гарна.
 */
class ScholarshipCalculatorTest {

    private final ScholarshipCalculator calc = new ScholarshipCalculator();

    @Test
    @DisplayName("A1: 95 оноо -> A")
    void letterGrade_score95_returnsA() {
        assertEquals("A", calc.letterGrade(95));
    }

    @Test
    @DisplayName("A2: 75 оноо -> C")
    void letterGrade_score75_returnsC() {
        assertEquals("C", calc.letterGrade(75));
    }

    @Test
    @DisplayName("A3: [95, 85] дүнгийн голч -> 3.5")
    void gpa_twoScores_returnsAverage() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), 1e-9);
    }

    @Test
    @DisplayName("A4: голч 3.9, кредит 16, зөрчилгүй -> 100%")
    void scholarship_highGpaAndCredits_returns100() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
    }

    @Test
    @DisplayName("A5: голч 3.5, кредит 13, зөрчилгүй -> 70%")
    void scholarship_midGpaAndCredits_returns70() {
        assertEquals(70, calc.scholarship(3.5, 13, false));
    }
}
