# Лаб 9: Код хамралт — JaCoCo-гоор тестээ хөтлөх

F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)

**Оюутан:** Г. Нармандах | **Код:** B232270057

## 1. Орчин

`java -version`:

```
openjdk version "17.0.20.1" 2026-08-18
OpenJDK Runtime Environment Homebrew (build 17.0.20.1+0)
OpenJDK 64-Bit Server VM Homebrew (build 17.0.20.1+0, mixed mode, sharing)
```

`mvn -version`:

```
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Maven home: /opt/homebrew/Cellar/maven/3.9.16/libexec
Java version: 17.0.20.1, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk@17/17.0.20.1/libexec/openjdk.jdk/Contents/Home
Default locale: en_MN, platform encoding: UTF-8
OS name: "mac os x", version: "26.6.2", arch: "aarch64", family: "mac"
```

| Зүйл | Утга |
|---|---|
| Хэрэгсэл | JUnit 5.10.2 (Jupiter), JaCoCo 0.8.12, Maven Surefire 3.2.5 |
| Компайлер | `maven.compiler.release` = 17 |
| Машин | MacBook Air (Apple Silicon, macOS 26.6.2) |

> **Орчны тэмдэглэл.** Энэ машин дээр JDK 17 ба JDK 27 хоёул суусан бөгөөд Maven
> анхдагчаар 27-г сонгодог. Тиймээс Лаб 8-аас хойш `~/.zshrc`-д
> `export JAVA_HOME=$(/usr/libexec/java_home -v 17)` гэж тогтоосон. Дээрх
> `mvn -version`-ийн гаралт үүний дараах байдал.

## 2. Төслийн бүтэц

```
F.CSA313-lab9/
├── pom.xml                                       JUnit 5 + JaCoCo 0.8.12
├── cfg.png                                       scholarship()-ийн CFG, тестүүд ирмэгүүдэд
├── cfg.dot                                       CFG-ийн эх файл (Graphviz)
├── src/main/java/mn/edu/must/sqat/
│   └── ScholarshipCalculator.java                тестлэгдэх класс (3 функц)
├── src/test/java/mn/edu/must/sqat/
│   └── ScholarshipCalculatorTest.java            A–D хэсгийн 30 тест
├── results/
│   ├── mvn-verify-A.txt · jacoco-A.csv           Хэсэг A: happy path
│   ├── mvn-verify-B.txt · jacoco-B.csv           Хэсэг B+C: branch 100%
│   ├── mvn-test-D-fail.txt                       Хэсэг D: зааг утгын алдаа илэрсэн
│   └── mvn-verify-final.txt · jacoco-final.csv   засварын дараах эцсийн байдал
├── screenshots/                                  JaCoCo тайлангийн зургууд
└── .gitignore                                    target/, .idea/, .DS_Store
```

Ажиллуулах:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mkdir -p results
mvn verify 2>&1 | tee results/mvn-verify-final.txt
cp target/site/jacoco/jacoco.csv results/jacoco-final.csv
open target/site/jacoco/index.html
```

## 3. Спецификаци (oracle)

`ScholarshipCalculator`-ийн javadoc дахь шаардлага:

| Метод | Зан төлөв |
|---|---|
| `letterGrade(score)` | 90–100 → `A`, 80–89.99 → `B`, 70–79.99 → `C`, **60–69.99 → `D`**, 0–59.99 → `F`. 0-иос бага эсвэл 100-аас их бол `IllegalArgumentException`. |
| `gpa(scores[])` | Дүнгүүдийн голч (A=4, B=3, C=2, D=1, F=0). `null` эсвэл хоосон массивт `IllegalArgumentException`. |
| `scholarship(gpa, credits, hasViolation)` | Сахилгын зөрчилтэй → 0; голч ≥ 3.8 **ба** кредит ≥ 15 → 100; голч ≥ 3.4 **ба** кредит ≥ 12 → 70; голч ≥ 3.0 → 50; бусад → 0. Голч 0–4, кредит 0+ хязгаараас гадуур бол `IllegalArgumentException`. |

## 4. Хэсэг A: Happy path ба эхний хэмжилт

Зөвхөн хэвийн урсгалын **5 тест** бичсэн: `letterGrade(95)`, `letterGrade(75)`, `gpa([95,85])`, `scholarship(3.9,16,false)`, `scholarship(3.5,13,false)`.

`results/jacoco-A.csv`:

```
INSTRUCTION_MISSED 44, INSTRUCTION_COVERED 108
BRANCH_MISSED 19,      BRANCH_COVERED 22
LINE_MISSED 9,         LINE_COVERED 16
```

| Үзүүлэлт | Хамрагдсан / Нийт | Хувь |
|---|---|---|
| Instruction | 108 / 152 | 71.1% |
| **Branch** | **22 / 41** | **53.7%** |
| **Line** | **16 / 25** | **64.0%** |
| Method | 4 / 4 | 100% |

**Ажиглалт.** Улаан (огт хүрээгүй) мөрүүд нь бүгд **алдааны ба ховор замууд** байв: гурван `throw` мөр (21, 32, 49) — буруу оролтыг огт тестлээгүй; `letterGrade`-ийн `return "D"` ба `return "F"` (26, 27) — 60-аас доош ба 60–70 хоорондын оноо тестэд ороогүй; `gpa`-ийн `switch`-ийн `case "C"`, `case "D"` (39, 40) — `gpa([95,85])` зөвхөн A, B дүн үүсгэдэг; `scholarship`-ийн `return 50` ба сүүлийн `return 0` (54, 55) — бага голчтой тохиолдол шалгагдаагүй. Шар ромбо нь **нөхцөлийн зөвхөн нэг тал дайрагдсан** мөрүүд дээр гарсан: `if (score < 0 || score > 100)` ба `if (gpa < 0 || gpa > 4 || credits < 0)` дээр бүх нөхцөл зөвхөн ХУДАЛ утгаараа л дайрагдсан, `if (hasViolation)` зөвхөн худал, `&&`-тэй хоёр if-ийн баруун талын нөхцөл зөвхөн үнэн утгаараа дайрагдсан. Өөрөөр хэлбэл happy path тестүүд кодыг «нэг чиглэлд» л гүйлгэж, эсрэг салаа бүр хоосон үлдсэн — хамралтын тайлан яг энэ нүхнүүдийг шууд зааж өгч байна.

→ [results/mvn-verify-A.txt](results/mvn-verify-A.txt) · [results/jacoco-A.csv](results/jacoco-A.csv)

> Дээрх ажиглалтын эх сурвалж нь `results/jacoco-A.csv` ба түүнээс гарсан JaCoCo-гийн
> HTML тайлан юм. `screenshots/` доторх зургууд нь Хэсэг B-гийн дараах (100%) төлөвийг
> харуулдаг тул тэдгээрийг Хэсэг A-гийн нотолгоо болгон ашиглаагүй.

## 5. Хэсэг B: CFG ба branch 100%

![scholarship() CFG](cfg.png)

CFG-г [`cfg.dot`](cfg.dot) эх файлаас Graphviz-ээр үүсгэсэн (даалгавар draw.io/Excalidraw
зэрэг хэрэгсэл ашиглахыг зөвшөөрсөн). `scholarship()`-ийн CFG нь 9 шийдвэрийн зангилаа, 6 төгсгөлийн зангилаа (1 `throw` + 5 `return`), **18 ирмэгтэй** — энэ нь JaCoCo-гийн тоолсон 18 салаатай яг тэнцэнэ. Ирмэг бүрийг дайрч буй тест:

| Ирмэг | Тест |
|---|---|
| `gpa < 0` → үнэн → `throw` | `scholarship_negativeGpa_throws` |
| `gpa < 0` → худал | бусад бүх scholarship тест |
| `gpa > 4` → үнэн → `throw` | `scholarship_gpaAbove4_throws` |
| `gpa > 4` → худал | бусад |
| `credits < 0` → үнэн → `throw` | `scholarship_negativeCredits_throws` |
| `credits < 0` → худал | бусад |
| `hasViolation` → үнэн → `return 0` | `scholarship_withViolation_returns0` |
| `hasViolation` → худал | бусад |
| `gpa >= 3.8` → үнэн | `scholarship_highGpaAndCredits_returns100`, `scholarship_highGpaButLowCredits_returns70` |
| `gpa >= 3.8` → худал | `scholarship_midGpaAndCredits_returns70`, `scholarship_gpaBelow34_returns50` |
| `credits >= 15` → үнэн → `return 100` | `scholarship_highGpaAndCredits_returns100` |
| `credits >= 15` → худал | `scholarship_highGpaButLowCredits_returns70` |
| `gpa >= 3.4` → үнэн | `scholarship_midGpaAndCredits_returns70`, `scholarship_midGpaButLowCredits_returns50` |
| `gpa >= 3.4` → худал | `scholarship_gpaBelow34_returns50`, `scholarship_lowGpa_returns0` |
| `credits >= 12` → үнэн → `return 70` | `scholarship_midGpaAndCredits_returns70` |
| `credits >= 12` → худал | `scholarship_midGpaButLowCredits_returns50` |
| `gpa >= 3.0` → үнэн → `return 50` | `scholarship_gpaBelow34_returns50` |
| `gpa >= 3.0` → худал → `return 0` | `scholarship_lowGpa_returns0` |

**100% хүрэхэд юу нэмэгдсэн бэ.** 5 тестээс **23 тест** болсон. Шийдвэрлэх нэмэлтүүд:

- `scholarship()`-ийн эхний `||`-ийн **гурван нөхцөл тус бүрд тусдаа** `assertThrows` (B7, B8, B9). JaCoCo нөхцөл бүрийг өөр салаа гэж тоолдог тул нэгийг нь мартвал 40/41 (97.6%) дээр зогсоно.
- `&&`-тэй хоёр if-ийн **баруун талын нөхцөл худал** болох тохиолдлууд: `scholarship(3.9, 12, false)` ба `scholarship(3.5, 10, false)` (B11, B12). Голч хангасан ч кредит дутуу гэсэн утга.
- `gpa()`-ийн `switch`-ийн бүх салааг нэг тестээр: `gpa([95,85,75,65,30])` нь A, B, C, D ба **`default`** (F дүн) бүгдийг дайрна (B6).
- `letterGrade`-ийн `score < 0`, `score > 100` үнэн болох хоёр тест ба B, D, F буцаах гурван тест (B1–B5).

`results/jacoco-B.csv`:

```
INSTRUCTION_MISSED 0, INSTRUCTION_COVERED 152
BRANCH_MISSED 0,      BRANCH_COVERED 41
LINE_MISSED 0,        LINE_COVERED 25
```

**`BRANCH_MISSED = 0`, `LINE_MISSED = 0`** — Branch ба Line хамралт хоёул **100%**. `results/mvn-verify-B.txt`-ийн `Tests run: 23, Failures: 0`.

→ [results/mvn-verify-B.txt](results/mvn-verify-B.txt) · [results/jacoco-B.csv](results/jacoco-B.csv)

## 6. Хэсэг C: Давталтын заагийн тестүүд

`gpa()`-ийн давталтад Лекц 9-ийн 0 / 1 / 2+ дүрмийг хэрэгжүүлсэн:

| Дүрэм | Тест | Оролт | Хүлээгдэх |
|---|---|---|---|
| 0 удаа | `gpa_nullArray_throws` | `null` | `IllegalArgumentException` |
| 0 удаа | `gpa_emptyArray_throws` | `new double[]{}` | `IllegalArgumentException` |
| 1 удаа | `gpa_singleScore_returns4` | `[95]` | `4.0` |
| 2+ удаа | `gpa_fourScores_returns2point5` | `[95, 85, 75, 65]` | `2.5` |

**Яагаад хэрэгтэй хэвээр вэ.** Эдгээрийн ихэнх нь хамралтын тоог нэмэгдүүлээгүй — давталтын бие болон `throw` мөрүүд аль хэдийн өөр тестээр дайрагдсан байсан. Гэвч хамралт нь «мөр ажилласан уу» гэдгийг л хэмждэг бөгөөд давталт **хэдэн удаа** эргэсний дараа хуримтлагдах төрлийн алдааг (эхний элементийг алгасах, сүүлийнхийг хоёр удаа нэмэх, нэг элементтэй үед хуваах алдаа) огт мэдэрдэггүй: 0, 1, 2+ гэсэн гурван тохиолдлыг тусад нь шалгасан тест л тэдгээрийг барина.

## 7. Хэсэг D: Хамралт 100% — гэхдээ алдаа амьд

Branch хамралт 100% болсны ДАРАА спецификацийн зааг утгуудыг шалгах 7 тест нэмсэн: `letterGrade(0)`, `(59.99)`, `(60)`, `(69.99)`, `(89.99)`, `(90)`, `(100)`. Нийт 30 тест.

`mvn test` → **BUILD FAILURE**:

```
[ERROR] Failures:
[ERROR]   ScholarshipCalculatorTest.letterGrade_score60_returnsD:202 expected: <D> but was: <F>
[ERROR] Tests run: 30, Failures: 1, Errors: 0, Skipped: 0
```

| Зүйл | Утга |
|---|---|
| Унасан тест | `letterGrade_score60_returnsD` |
| Алдааны мөр | `expected: <D> but was: <F>` |
| Кодын алдаа | `ScholarshipCalculator.java` мөр 26: `if (score > 60)` байсан |
| Зөв хувилбар | `if (score >= 60)` — спецификаци «60–69.99 → D» гэсэн |
| Засварын commit | `Fix letterGrade: score > 60 -> score >= 60; спецификаци 60-69.99 нь D гэсэн` |

Засварын дараа `results/jacoco-final.csv`: `BRANCH_MISSED 0`, `LINE_MISSED 0`, `Tests run: 30, Failures: 0`, `BUILD SUCCESS`.

→ [results/mvn-test-D-fail.txt](results/mvn-test-D-fail.txt) · [results/mvn-verify-final.txt](results/mvn-verify-final.txt) · [results/jacoco-final.csv](results/jacoco-final.csv)

### Гол дүгнэлт

Энэ алдаа branch хамралт 100% байхад яагаад үл үзэгдэгч байсан бэ: `if (score > 60)` ба `if (score >= 60)` хоёр нь **яг ижил хоёр салаа** үүсгэдэг — үнэн ба худал. JaCoCo тэр хоёр салааг дайрагдсан эсэхийг л тоолдог бөгөөд миний Хэсэг B-гийн тестүүд `65` (үнэн) ба `30` (худал) гэсэн хоёр утгаар хоёуланг нь дайрсан тул тоолуур 41/41 болж дүүрсэн. Гэтэл энэ хоёр оператор зөвхөн **нэг цэг дээр** — яг `score == 60` үед — өөр хариу өгдөг, тэр цэгийг дайрсан тест Хэсэг B-д байгаагүй. Өөрөөр хэлбэл хамралт нь кодын ямар ХЭСЭГ ажилласныг хэмждэг, харин тэр хэсэг ЗӨВ хариу өгсөн эсэхийг огт хэмждэггүй — сүүлийнх нь oracle-ийн, өөрөөр хэлбэл спецификацийн ажил. Хамралт зөвхөн шалгагдаагүй үлдсэн кодыг заадаг тул «юуг хараахан тестлээгүй вэ» гэсэн асуултад сайн хариулдаг ч «тестүүд маань хангалттай сайн уу» гэдэгт огт хариу өгдөггүй. Тиймээс хамралт бол зайлшгүй хэрэгсэл боловч хангалттай нотолгоо биш: 100%-д хүрсний дараа зааг утга, эквивалент ангиуд, мутаци зэрэг **утга санаанд суурилсан** шалгуурууд хэрэгтэй хэвээр үлдэнэ.

## 8. AI ашиглалтын тэмдэглэл

Энэ лабораторид AI туслахыг (Claude) тестийн классын төсөл боловсруулах, CFG-ийн ирмэг–тест харгалзааг гаргах, тайланг хэлбэржүүлэхэд ашигласан. Бүх `mvn verify` / `mvn test` ажиллуулалтыг өөрийн MacBook дээр (JDK 17.0.20.1, Maven 3.9.16) хийж, `results/` доторх долоон файлыг тэр ажиллуулалтаас үүсгэсэн; README-д бичигдсэн тоо бүр тэдгээр файлаас шууд авагдсан — таамаглаж бичсэн зүйл байхгүй. `cfg.png`-ийг AI-гийн тусламжтайгаар Graphviz-ээр үүсгэсэн бөгөөд ирмэг бүрийн
тестийн нэрийг тестийн файлтай тулган шалгасан. Хэсэг D-гийн алдааг кодоос урьдчилан хайлгүй, спецификацийн зааг утгуудыг тестэлснээр илрүүлсэн.
