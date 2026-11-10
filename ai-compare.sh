#!/bin/bash
# Нэмэлт даалгавар (Сонголт 2): AI-гийн үүсгэсэн тестийн классыг ХОЁР төлөвт тулгана:
#   (1) АЛДААТАЙ код (score > 60)  — Хэсэг A-гийн дараах бодит төлөв
#   (2) ЗАССАН код  (score >= 60)  — эцсийн төлөв
# Төслийг /tmp-д хуулж ажиллуулна — үндсэн репогийн src/test, results/ хөндөгдөхгүй.

cd "$(dirname "$0")" || exit 1
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

REPO="$(pwd)"
WORK=/tmp/lab9-ai
OUT="$REPO/results/ai-tests-vs-bug.txt"
SRC=src/main/java/mn/edu/must/sqat/ScholarshipCalculator.java

rm -rf "$WORK"; mkdir -p "$WORK"
cp pom.xml "$WORK"/; cp -R src "$WORK"/
rm -f "$WORK"/src/test/java/mn/edu/must/sqat/ScholarshipCalculatorTest.java
cp ai-test/AiScholarshipCalculatorTest.java "$WORK"/src/test/java/mn/edu/must/sqat/
cd "$WORK" || exit 1

{ echo "AI (Google Gemini)-гийн тестийн класс — хоёр төлөвт тулгасан хэмжилт"
  echo "Үүсгэсэн: $(date '+%Y-%m-%d %H:%M')"
  echo "Тестийн класс: ai-test/AiScholarshipCalculatorTest.java"
  echo ""; } > "$OUT"

run() {
  mvn verify -Dmaven.test.failure.ignore=true > /tmp/lab9-ai-last.txt 2>&1
  {
    echo "=================== $1 ==================="
    grep -o 'if (score >[=]* 60)' "$SRC" | head -1 | sed 's/^/код: /'
    grep -o 'Tests run: [0-9]*, Failures: [0-9]*, Errors: [0-9]*, Skipped: [0-9]*' /tmp/lab9-ai-last.txt | tail -1
    if [ -f target/site/jacoco/jacoco.csv ]; then
      tail -1 target/site/jacoco/jacoco.csv | awk -F, '{printf "Instruction %d/%d  Branch %d/%d  Line %d/%d\n",$5,$4+$5,$7,$6+$7,$9,$8+$9}'
    fi
    grep '^\[ERROR\]   AiScholarshipCalculatorTest' /tmp/lab9-ai-last.txt | sed 's/^\[ERROR\]   /  унав: /' | sort -u
    echo ""
  } >> "$OUT"
  printf "%-26s %s\n" "$1" "$(grep -o 'Tests run: [0-9]*, Failures: [0-9]*, Errors: [0-9]*, Skipped: [0-9]*' /tmp/lab9-ai-last.txt | tail -1)"
}

# (1) алдаатай код руу буцаах
perl -pi -e 's{if \(score >= 60\) return "D";}{if \(score > 60\)  return "D";}' "$SRC"
run "1-АЛДААТАЙ-код(> 60)"

# (2) засагдсан код
perl -pi -e 's{if \(score > 60\)  return "D";}{if \(score >= 60\) return "D";}' "$SRC"
run "2-ЗАССАН-код(>= 60)"

echo ""
echo "Гаралт: results/ai-tests-vs-bug.txt"
cat "$OUT"
