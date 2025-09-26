package pl.cbr.notebook.diary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MyersDiffTest {

    @Test
    public void diffTest() {

        String fileA = "To jest poczatek pliku\n" +
                "Ala ma kota\n" +
                "a kot ma Ale\n" +
                "To juz jest koniec pliku";

        String fileB = "To jest poczatek pliku\n" +
                "Ala ma psa\n" +
                "a kot ma Ale\n" +
                "To juz jest koniec pliku";

        String diff = MyersDiff.diff(fileA, fileB);

        System.out.println("diff");
        System.out.println(diff);

        String appliedPatch = MyersDiff.apply(fileA, diff);
        System.out.println();
        System.out.println("appliedPatch");
        System.out.println(appliedPatch);

        Assertions.assertEquals(fileB, appliedPatch);

        String reverseAppliedPatch = MyersDiff.reverseApply(fileB, diff);
        System.out.println();
        System.out.println("reverse appliedPatch");
        System.out.println(reverseAppliedPatch);

        Assertions.assertEquals(fileA, reverseAppliedPatch);
    }

    @Test
    public void reverseChangeTest() {

        String diff = "28:-:{\"x1\":1,\"x2\":2}\n" +
                "28:+:{\n" +
                "29:+:    \"x1\": 1,\n" +
                "30:+:    \"x2\": 2\n" +
                "31:+:}\n" +
                "\n";

        String input =
                "123\n" +
                "\n" +
                "4546\n" +
                "444\n" +
                "\n" +
                "2222\n" +
                "11\n" +
                "222\n" +
                "111\n" +
                "222\n" +
                "333\n" +
                "\n" +
                "\n" +
                "{\n" +
                "    \"a1\": 1,\n" +
                "     \"a2\": 2,\n" +
                "     \"a3\": 3\n" +
                "}\n" +
                "\n" +
                "test1\n" +
                "test2\n" +
                "test3\n" +
                "\n" +
                "test1\n" +
                "test2\n" +
                "test3\n" +
                "test4\n" +
                "{\n" + // 28
                "    \"x1\": 1,\n" +
                "    \"x2\": 2\n" +
                "}\n" +
                "\n";

        String reverseAppliedPatch = MyersDiff.reverseApply(input, diff);
        System.out.println();
        System.out.println("reverse appliedPatch");
        System.out.println(reverseAppliedPatch);
    }

    @Test
    public void reverseChangeProgressive1Test() {
        String startText = "DIARY\n" +
                "\n" +
                "\n";
        String endText = "DIARY\n" +
                "\n" +
                "000\n" +
                "111\n" +
                "222\n" +
                "333\n" +
                "444\n" +
                "555\n" +
                "666\n" +
                "777";
        String diff = "-+-+-+-0123456789-+-+-+-\n" +
                "2:+:\n" +
                "3:+:000\n" +
                "-+-+-+-0123456789-+-+-+-\n" +
                "4:+:111\n" +
                "-+-+-+-0123456789-+-+-+-\n" +
                "5:+:222\n" +
                "-+-+-+-0123456789-+-+-+-\n" +
                "6:+:333\n" +
                "-+-+-+-0123456789-+-+-+-\n" +
                "7:+:444\n" +
                "-+-+-+-0123456789-+-+-+-\n" +
                "8:+:555\n" +
                "-+-+-+-0123456789-+-+-+-\n" +
                "9:+:666\n";

    }
}
