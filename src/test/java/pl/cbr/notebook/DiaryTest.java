package pl.cbr.notebook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DiaryTest {

    @Test
    void testFromBeginInNormalOrder() {
        String inputText = "test";
        Diary diary = new Diary();

        addDiaryEvents(diary, inputText, 0);

        Assertions.assertEquals(1, diary.getChanges().size());
        assertDiaryChange(inputText, diary.getChanges().get(0), 0);
    }

    @Test
    void testFromBeginInNormalOrderWithTextInTheMiddle() {
        String firstText = "test";
        String secondText = "ABC";
        String finalText = "teABCst";

        Diary diary = new Diary();
        addDiaryEvents(diary, firstText, 0);
        addDiaryEvents(diary, secondText, 2);

        Assertions.assertEquals(1, diary.getChanges().size());
        assertDiaryChange(finalText, diary.getChanges().get(0), 0);
    }

    @Test
    void testFromBeginInNormalOrderTwoSeparatePositions() {
        String inputText = "test";

        Diary diary = new Diary();
        addDiaryEvents(diary, inputText, 0);
        addDiaryEvents(diary, inputText, 10);

        Assertions.assertEquals(2, diary.getChanges().size());
        assertDiaryChange(inputText, diary.getChanges().get(0), 0);
        assertDiaryChange(inputText, diary.getChanges().get(1), 10);
    }

    private static void addDiaryEvents(Diary diary, String inputText, int startPosition) {
        for (int i = 0; i < inputText.length(); i++) {
            diary.event(inputText.charAt(i), startPosition++);
        }
    }

    private static void assertDiaryChange(String inputText, DiaryChange diaryChange, int expectedBegin) {
        Assertions.assertEquals(inputText, diaryChange.getChange());
        Assertions.assertEquals(expectedBegin, diaryChange.getBegin());
        Assertions.assertEquals(expectedBegin + inputText.length() + 1, diaryChange.getEnd());
    }
}