package mn.edu.must.sqat;

// AI туслах (Google Gemini)-ийн үүсгэсэн тестийн класс — ЗӨВХӨН харьцуулалтын
// туршилтад ашигласан. Үндсэн build-д ороогүй (src/test-д байрлуулаагүй).
// Агуулгыг нь огт өөрчлөөгүй, AI-гийн буцаасан хэвээр хадгалсан.
// Prompt: Хэсэг A-гийн төлөв (5 тест, алдаатай код `score > 60`) + спецификацийн
//         javadoc өгөөд "Branch ба Line хамралтыг 100% болгох бүх тестийг нэм" гэж хүссэн.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiScholarshipCalculatorTest {

    private ScholarshipCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ScholarshipCalculator();
    }

    // ================= LETTER GRADE TESTS =================

    @Test
    void letterGradeA() {
        assertEquals("A", calculator.letterGrade(95));
        assertEquals("A", calculator.letterGrade(90));
        assertEquals("A", calculator.letterGrade(100));
    }

    @Test
    void letterGradeB() {
        assertEquals("B", calculator.letterGrade(85));
        assertEquals("B", calculator.letterGrade(80));
        assertEquals("B", calculator.letterGrade(89.99));
    }

    @Test
    void letterGradeC() {
        assertEquals("C", calculator.letterGrade(75));
        assertEquals("C", calculator.letterGrade(70));
        assertEquals("C", calculator.letterGrade(79.99));
    }

    @Test
    void letterGradeD() {
        assertEquals("D", calculator.letterGrade(65));
        assertEquals("D", calculator.letterGrade(60.01));
    }

    @Test
    void letterGradeF() {
        assertEquals("F", calculator.letterGrade(60));
        assertEquals("F", calculator.letterGrade(59.99));
        assertEquals("F", calculator.letterGrade(0));
    }

    @Test
    void letterGradeInvalidScores() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.letterGrade(-1));

        assertThrows(IllegalArgumentException.class,
                () -> calculator.letterGrade(100.01));
    }

    // ================= GPA TESTS =================

    @Test
    void gpaCalculatesCorrectly() {
        assertEquals(3.5, calculator.gpa(new double[]{95, 85}));
    }

    @Test
    void gpaAllGrades() {
        // A=4, B=3, C=2, D=1, F=0
        assertEquals(2.0,
                calculator.gpa(new double[]{95, 85, 75, 65, 50}));
    }

    @Test
    void gpaAllGradeCategories() {
        assertEquals(4.0, calculator.gpa(new double[]{90}));
        assertEquals(3.0, calculator.gpa(new double[]{80}));
        assertEquals(2.0, calculator.gpa(new double[]{70}));
        assertEquals(1.0, calculator.gpa(new double[]{61}));
        assertEquals(0.0, calculator.gpa(new double[]{60}));
    }

    @Test
    void gpaNullArrayThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.gpa(null));
    }

    @Test
    void gpaEmptyArrayThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.gpa(new double[]{}));
    }

    @Test
    void gpaInvalidScoreThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.gpa(new double[]{95, -1}));

        assertThrows(IllegalArgumentException.class,
                () -> calculator.gpa(new double[]{101}));
    }

    // ================= SCHOLARSHIP TESTS =================

    @Test
    void scholarship100Percent() {
        assertEquals(100, calculator.scholarship(3.9, 16, false));
        assertEquals(100, calculator.scholarship(3.8, 15, false));
    }

    @Test
    void scholarship70Percent() {
        assertEquals(70, calculator.scholarship(3.5, 13, false));
        assertEquals(70, calculator.scholarship(3.4, 12, false));
    }

    @Test
    void scholarship50Percent() {
        assertEquals(50, calculator.scholarship(3.0, 0, false));
        assertEquals(50, calculator.scholarship(3.2, 10, false));
    }

    @Test
    void scholarshipZeroPercent() {
        assertEquals(0, calculator.scholarship(2.9, 15, false));
        assertEquals(0, calculator.scholarship(0, 0, false));
    }

    @Test
    void scholarshipViolationAlwaysReturnsZero() {
        assertEquals(0, calculator.scholarship(4.0, 20, true));
        assertEquals(0, calculator.scholarship(3.5, 15, true));
        assertEquals(0, calculator.scholarship(2.0, 5, true));
    }

    @Test
    void scholarshipGpaBoundaryConditions() {
        assertEquals(100, calculator.scholarship(3.8, 15, false));
        assertEquals(70, calculator.scholarship(3.4, 12, false));
        assertEquals(50, calculator.scholarship(3.0, 0, false));
        assertEquals(0, calculator.scholarship(2.99, 20, false));
    }

    @Test
    void scholarshipCreditBoundaryConditions() {
        assertEquals(0, calculator.scholarship(3.9, 14, false));
        assertEquals(70, calculator.scholarship(3.4, 12, false));
        assertEquals(50, calculator.scholarship(3.5, 11, false));
    }

    @Test
    void scholarshipInvalidGpaThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.scholarship(-0.1, 15, false));

        assertThrows(IllegalArgumentException.class,
                () -> calculator.scholarship(4.1, 15, false));
    }

    @Test
    void scholarshipInvalidCreditsThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.scholarship(3.9, -1, false));
    }

    @Test
    void scholarshipValidExtremeBoundaries() {
        assertEquals(0, calculator.scholarship(0, 0, false));
        assertEquals(50, calculator.scholarship(4, 0, false));
        assertEquals(100, calculator.scholarship(4, 15, false));
    }
}
