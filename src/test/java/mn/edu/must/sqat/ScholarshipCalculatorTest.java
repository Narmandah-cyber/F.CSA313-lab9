package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ScholarshipCalculator-ийн бүтцийн тестүүд.
 *
 *   Хэсэг A — хэвийн урсгал (happy path)
 *   Хэсэг B — CFG-ийн бүх ирмэгийг дайрч branch/line хамралтыг 100% болгох
 *   Хэсэг C — gpa()-ийн давталтын заагууд (0 / 1 / 2+ дүрэм)
 *   Хэсэг D — letterGrade()-ийн ЗААГ утгууд: хамралт 100% байсан ч
 *             эдгээрийн нэг нь кодын алдааг илрүүлнэ
 */
class ScholarshipCalculatorTest {

    private final ScholarshipCalculator calc = new ScholarshipCalculator();

    // =================================================================
    // Хэсэг A — хэвийн урсгал
    // =================================================================

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

    // =================================================================
    // Хэсэг B — letterGrade()-ийн үлдсэн салаанууд
    // =================================================================

    @Test
    @DisplayName("B1: сөрөг оноо -> IllegalArgumentException (score < 0 үнэн)")
    void letterGrade_scoreBelowZero_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
    }

    @Test
    @DisplayName("B2: 100-аас их оноо -> IllegalArgumentException (score > 100 үнэн)")
    void letterGrade_scoreAbove100_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    @Test
    @DisplayName("B3: 85 оноо -> B")
    void letterGrade_score85_returnsB() {
        assertEquals("B", calc.letterGrade(85));
    }

    @Test
    @DisplayName("B4: 65 оноо -> D")
    void letterGrade_score65_returnsD() {
        assertEquals("D", calc.letterGrade(65));
    }

    @Test
    @DisplayName("B5: 30 оноо -> F (бүх if-ийн false салаа)")
    void letterGrade_score30_returnsF() {
        assertEquals("F", calc.letterGrade(30));
    }

    // =================================================================
    // Хэсэг B — gpa()-ийн switch-ийн бүх салаа (default буюу F оролцоно)
    // =================================================================

    @Test
    @DisplayName("B6: [95,85,75,65,30] -> switch-ийн A,B,C,D,default бүгд дайрагдана, голч 2.0")
    void gpa_allGradeKinds_coversEverySwitchBranch() {
        // A=4, B=3, C=2, D=1, F=0  ->  10 / 5 = 2.0
        assertEquals(2.0, calc.gpa(new double[]{95, 85, 75, 65, 30}), 1e-9);
    }

    // =================================================================
    // Хэсэг B — scholarship()-ийн бүх салаа
    // =================================================================

    @Test
    @DisplayName("B7: голч < 0 -> IllegalArgumentException (|| эхний нөхцөл)")
    void scholarship_negativeGpa_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-1, 15, false));
    }

    @Test
    @DisplayName("B8: голч > 4 -> IllegalArgumentException (|| хоёр дахь нөхцөл)")
    void scholarship_gpaAbove4_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 15, false));
    }

    @Test
    @DisplayName("B9: кредит < 0 -> IllegalArgumentException (|| гурав дахь нөхцөл)")
    void scholarship_negativeCredits_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }

    @Test
    @DisplayName("B10: сахилгын зөрчилтэй -> 0% (бусад нөхцөлөөс үл хамаарна)")
    void scholarship_withViolation_returns0() {
        assertEquals(0, calc.scholarship(4.0, 16, true));
    }

    @Test
    @DisplayName("B11: голч 3.9 ч кредит 12 -> 100% биш 70% (gpa>=3.8 үнэн, credits>=15 худал)")
    void scholarship_highGpaButLowCredits_returns70() {
        assertEquals(70, calc.scholarship(3.9, 12, false));
    }

    @Test
    @DisplayName("B12: голч 3.5 ч кредит 10 -> 70% биш 50% (gpa>=3.4 үнэн, credits>=12 худал)")
    void scholarship_midGpaButLowCredits_returns50() {
        assertEquals(50, calc.scholarship(3.5, 10, false));
    }

    @Test
    @DisplayName("B13: голч 3.2, кредит 16 -> 50% (gpa>=3.4 худал, gpa>=3.0 үнэн)")
    void scholarship_gpaBelow34_returns50() {
        assertEquals(50, calc.scholarship(3.2, 16, false));
    }

    @Test
    @DisplayName("B14: голч 2.5 -> 0% (бүх босго хангагдаагүй)")
    void scholarship_lowGpa_returns0() {
        assertEquals(0, calc.scholarship(2.5, 16, false));
    }

    // =================================================================
    // Хэсэг C — gpa()-ийн давталтын заагууд: 0 / 1 / 2+
    // =================================================================

    @Test
    @DisplayName("C1: давталт 0 удаа — null массив -> IllegalArgumentException")
    void gpa_nullArray_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
    }

    @Test
    @DisplayName("C2: давталт 0 удаа — хоосон массив -> IllegalArgumentException")
    void gpa_emptyArray_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    @Test
    @DisplayName("C3: давталт 1 удаа — [95] -> 4.0")
    void gpa_singleScore_returns4() {
        assertEquals(4.0, calc.gpa(new double[]{95}), 1e-9);
    }

    @Test
    @DisplayName("C4: давталт 2+ удаа — [95,85,75,65] -> 2.5")
    void gpa_fourScores_returns2point5() {
        assertEquals(2.5, calc.gpa(new double[]{95, 85, 75, 65}), 1e-9);
    }

    // =================================================================
    // Хэсэг D — letterGrade()-ийн зааг (boundary) утгууд
    // Хамралт аль хэдийн 100% боловч эдгээр утгууд спецификацийн
    // ЯГ хилийг шалгадаг тул өөр мэдээлэл өгнө.
    // =================================================================

    @Test
    @DisplayName("D1: 0 оноо -> F (мужийн доод хил)")
    void letterGrade_score0_returnsF() {
        assertEquals("F", calc.letterGrade(0));
    }

    @Test
    @DisplayName("D2: 59.99 оноо -> F (F-ийн дээд хил)")
    void letterGrade_score59point99_returnsF() {
        assertEquals("F", calc.letterGrade(59.99));
    }

    @Test
    @DisplayName("D3: 60 оноо -> D (спецификаци: 60-69.99 -> D)")
    void letterGrade_score60_returnsD() {
        assertEquals("D", calc.letterGrade(60));
    }

    @Test
    @DisplayName("D4: 69.99 оноо -> D (D-ийн дээд хил)")
    void letterGrade_score69point99_returnsD() {
        assertEquals("D", calc.letterGrade(69.99));
    }

    @Test
    @DisplayName("D5: 89.99 оноо -> B (B-ийн дээд хил)")
    void letterGrade_score89point99_returnsB() {
        assertEquals("B", calc.letterGrade(89.99));
    }

    @Test
    @DisplayName("D6: 90 оноо -> A (A-гийн доод хил)")
    void letterGrade_score90_returnsA() {
        assertEquals("A", calc.letterGrade(90));
    }

    @Test
    @DisplayName("D7: 100 оноо -> A (мужийн дээд хил)")
    void letterGrade_score100_returnsA() {
        assertEquals("A", calc.letterGrade(100));
    }
}
