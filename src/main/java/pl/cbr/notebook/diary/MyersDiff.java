package pl.cbr.notebook.diary;

import pl.cbr.notebook.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MyersDiff {

    public static String diff(String fileA, String fileB) {
        List<String> a = Arrays.stream(fileA.split(StringUtil.ENTER)).collect(Collectors.toList());
        List<String> b = Arrays.stream(fileB.split(StringUtil.ENTER)).collect(Collectors.toList());
        return DiffUtils.changesToString(diff(a, b));
    }

    private static List<DiffOp> diff(List<String> a, List<String> b) {
        int N = a.size();
        int M = b.size();
        int max = N + M;

        int[] v = new int[2 * max + 1];
        Map<Integer, int[]> snapshots = new HashMap<>();

        int offset = max;
        v[offset + 1] = 0;
        int d;

        OUTER:
        for (d = 0; d <= max; d++) {
            snapshots.put(d, Arrays.copyOf(v, v.length));
            for (int k = -d; k <= d; k += 2) {
                int index = k + offset;
                int x;
                if (k == -d || (k != d && v[index - 1] < v[index + 1])) {
                    x = v[index + 1];
                } else {
                    x = v[index - 1] + 1;
                }
                int y = x - k;
                while (x < N && y < M && a.get(x).equals(b.get(y))) {
                    x++;
                    y++;
                }
                v[index] = x;
                if (x >= N && y >= M) {
                    break OUTER;
                }
            }
        }

        List<DiffOp> result = new ArrayList<>();
        int x = N, y = M;

        for (int dist = d; dist >= 0; dist--) {
            int[] snap = snapshots.get(dist);
            int k = x - y;
            int index = k + offset;
            int prevK;
            if (k == -dist || (k != dist && snap[index - 1] < snap[index + 1])) {
                prevK = k + 1;
            } else {
                prevK = k - 1;
            }
            int prevX = snap[prevK + offset];
            int prevY = prevX - prevK;

            // SKIP EQUAL: tylko przesuwamy
            while (x > prevX && y > prevY) {
                x--;
                y--;
            }
            if (dist > 0) {
                if (x == prevX) {
                    result.add(new DiffOp(DiffType.INSERT, y, b.get(y - 1)));
                    y--;
                } else {
                    result.add(new DiffOp(DiffType.DELETE, x, a.get(x - 1)));
                    x--;
                }
            }
        }
        Collections.reverse(result);
        return result;
    }

    public static String apply(String oldText, String diffOps) {
        return String.join(StringUtil.ENTER, apply(oldText, diffOps, false));
    }

    public static String reverseApply(String oldText, String diffOps) {
        return String.join(StringUtil.ENTER, apply(oldText, diffOps, true));
    }

    private static List<String> apply(String oldText, String diffOps, boolean reverse) {
        List<String> text = Arrays.stream(oldText.split(StringUtil.ENTER)).collect(Collectors.toList());
        List<DiffOp> diff = DiffUtils.changesFromString(diffOps);
        List<String> copyOfOldText = new ArrayList<>(text);
        List<DiffOp> changes = new ArrayList<>(diff);
        if (reverse) {
            return reverseApplyLogic(changes, copyOfOldText);
        } else {
            return applyLogic(changes, copyOfOldText);
        }
    }

    private static List<String> reverseApplyLogic(List<DiffOp> changes, List<String> copyOfOldText) {
        Collections.reverse(changes);
        for (DiffOp op : changes) {
            int index = op.getLineNumber() - 1; // 1-based → 0-based
            if (index >= 0 && index <= copyOfOldText.size()) {
                switch (op.getType()) {
                    case DELETE:
                        copyOfOldText.add(index, op.getContent());
                        break;
                    case INSERT:
                        if (index < copyOfOldText.size()) {
                            copyOfOldText.remove(index);
                        }
                        break;
                }
            }
        }
        return copyOfOldText;
    }

    private static List<String> applyLogic(List<DiffOp> changes, List<String> copyOfOldText) {
        for (DiffOp op : changes) {
            int index = op.getLineNumber() - 1; // 1-based → 0-based
            if (index >= 0 && index < copyOfOldText.size()) {
                switch (op.getType()) {
                    case DELETE:
                        copyOfOldText.remove(index);
                        break;
                    case INSERT:
                        copyOfOldText.add(index, op.getContent());
                        break;
                }
            }
        }
        return copyOfOldText;
    }
}

