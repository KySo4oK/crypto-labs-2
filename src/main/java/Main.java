import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.valueOf;

public class Main {

    public static final List<String> ALL_SYMBOLS = getAllSymbols();
    private static List<Integer> signs = List.of(32, 33, 44, 46, 39, 34, 58, 59, 63, 96, 45);

    public static void main(String[] args) throws IOException {
        byte[] bytes = "280dc9e47f3352c307f6d894ee8d534313429a79c1d8a6021f8a8eabca919cfb685a0d468973625e757490daa981ea6b".getBytes();
        List<String> lines = Files.lines(Paths.get("data.txt")).collect(Collectors.toList());
//        tryXorRowsBetween(lines);
//        tryByGuessingPerRow(lines);
        HashMap<Integer, String> xored = getIndexOfLineWithXorValue(lines);
//        xored.values().forEach(System.out::println);
        int length = lines.get(0).length();
        ArrayList<Integer> integers = getIntValuesOfPossibleChars();

        //show possible values
        analyze(lines, xored, length, integers);


        xorWithNumberForFirstRow(xored, length,1 ,((int) 'W'));

        print( xored, length, integers, 1);
    }

    private static void analyze(List<String> lines, HashMap<Integer, String> xored, int length, ArrayList<Integer> integers) {
        for (int j = 50; j < lines.get(18).length(); j++) {
            print(xored, length, integers, j);
        }
    }

    private static void print(HashMap<Integer, String> xored, int length, ArrayList<Integer> integers, int j) {
        System.out.println(j + "!");
        int finalJ = j;
        integers.forEach(integer -> xorWithNumberForFirstRow(xored, length, finalJ, integer));
        System.out.println();
    }

    private static void xorWithNumberForFirstRow(HashMap<Integer, String> xored, int length, int index, Integer integer) {
        List<String> collect = xored.values().stream()
                .filter(x -> x.length() > index)
                .map(xor -> valueOf(getXor(length, integer, xor).charAt(index)))
                .distinct()
                .filter(s -> ((int) s.charAt(0)) != integer)
                .collect(Collectors.toList());
        System.out.println(((char) integer.intValue()));
        System.out.println(collect);
    }

    private static ArrayList<Integer> getIntValuesOfPossibleChars() {
        List<Integer> lower = IntStream.range(97, 123).boxed().collect(Collectors.toList());
        List<Integer> upper = IntStream.range(65, 91).boxed().collect(Collectors.toList());
        ArrayList<Integer> integers = new ArrayList<>(lower);
        integers.addAll(upper);
        integers.addAll(signs);
        return integers;
    }

    private static HashMap<Integer, String> getIndexOfLineWithXorValue(List<String> lines) {
        HashMap<Integer, String> xored = new HashMap<>();
        int xorWith = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (i != xorWith) {
                xored.put(i, getXorOfLines(lines, xorWith, i));
            }
        }
        return xored;
    }

    private static boolean isContainsOnlyLettersAndSpace(List<String> collect) {
        int upper = 0;
        int lower = 0;
        int spaces = 0;
        int signsCount = 0;

        for (String s : collect) {
            char c = s.charAt(0);
            if (c == 32) {
                spaces++;
            } else if (isLowerCase(c)) {
                upper++;
            } else if (isUpperCase(c)) {
                lower++;
            } else if (inRange(c, 48, 57)) {
                return false;
            } else if (signs.contains(((int) c))) {
                signsCount++;
            }
        }

        return upper + lower + spaces + signsCount > 0;
    }

    private static String getXor(int length, Integer integer, String xor) {
        return xor(xor, getLineWIth(valueOf((char) integer.intValue()), length));
    }

    private static String getLineWIth(String a, int length) {
        return String.valueOf(a).repeat(Math.max(0, length));
    }

    public static List<String> getAllSymbols() {
        List<String> strings = IntStream.range(1, 127)
                .mapToObj(c -> (char) c)
                .map(String::valueOf)
                .collect(Collectors.toList());
        strings.addAll(List.of("\n", "\t", "\r"));
        return strings;
    }

    private static void tryByGuessingPerRow(List<String> lines) {
        Optional<Integer> min = lines.stream().map(String::length).min(Integer::compareTo);
        Optional<Integer> max = lines.stream().map(String::length).max(Integer::compareTo);
        if (min.isPresent()) {
            Integer minVal = min.get();
            List<String> collect = lines.stream()
                    .map(line -> line.substring(0, minVal))
                    .collect(Collectors.toList());
            List<List<Character>> chars = new ArrayList<>();
            for (int i = 0; i < minVal; i++) {
                int finalI = i;
                List<Character> collect1 = collect.stream()
                        .map(s -> s.charAt(finalI))
                        .distinct()
                        .collect(Collectors.toList());
                chars.add(collect1);
            }
//            for (int i = 0; i < 5/*chars.size()*/; i++) {
//                /*System.out.println(chars.get(i));*/
//                List<Character> characters = chars.get(i);
//                System.out.println(i + " letter");
//                for (int j = 0; j < characters.size(); j++) {
//                    for (String s : ALL_SYMBOLS) {
//                        String letter = valueOf(characters.get(j));
//                        String xor = xor(s, letter);
//                        int xorInt = xor.charAt(0);
//                        if (xorInt <= 128 && xorInt >= 32){
//                            System.out.println(letter + "-" + xor + "-" + s);
//                        }
//                    }
//                }
//            }
//            for (int i = 32; i < 128; i++) {
            tryGuessKey("                            Z^SU", lines);


//            Stream.of("W","]","U","V","\\", "R", "T")
//                    .forEach(s -> {
//                        System.out.println(s);
//                        tryGuessKey(s + s + s + "    C", lines);
//                    });
////                System.out.println(((char) i) + "---");
////            }
        }

//        max.ifPresent(max1 -> byGuessingKey(max1, lines));
    }

    private static void byGuessingKey(Integer max, List<String> lines) {
        List<String> allSymbols = getAllSymbols();
        AtomicLong count = new AtomicLong();
//        for (int i = 0; i < allSymbols.size(); i++) {
//            for (int j = 0; j < allSymbols.size(); j++) {
        for (int k = 0; k < allSymbols.size(); k++) {
            for (int m = 0; m < allSymbols.size(); m++) {
                for (int l = 0; l < allSymbols.size(); l++) {
                    String key = /*allSymbols.get(i) + allSymbols.get(j) +*/ allSymbols.get(k) + allSymbols.get(l) + allSymbols.get(m);
                    tryGuessKeyWithCheck(key, lines).ifPresent(s -> {
                        System.out.println(key);
                        System.out.println(s);
                        count.getAndIncrement();
                    });
                }
            }
//                }
//            }
        }
        System.out.println(count.get());
    }

    private static Optional<String> tryGuessKeyWithCheck(String key, List<String> lines) {
        int first = xor(valueOf(lines.get(0).charAt(0)), valueOf(key.charAt(0))).charAt(0);
        if (first < 65 || first > 90) {
            return Optional.empty();
        }
        String result = lines.stream()
                .map(line -> {
                    StringBuilder res = new StringBuilder();
                    List<String> collect = line.chars()
                            .mapToObj(i -> valueOf(((char) i)))
                            .collect(Collectors.toList());
                    for (int i = 0; i < key.length(); i++) {
                        res.append(xor(collect.get(i), valueOf(key.charAt(i))));
                    }
                    return res.toString();
                }).collect(Collectors.joining());

//        boolean allContains = Stream.of(" ", "e", "a", "o", "t", "i", "n", "s", "h", "th")
//                .allMatch(result::contains);
        if (isNormalText(result) && isNormalKey(key)) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private static boolean isNormalText(String result) {
        int upper = 0;
        int lower = 0;
        int signs = 0;
        int length = result.length();
        for (int i = 0; i < length; i++) {
            char c = result.charAt(i);
            if (inRange(c, 32, 64) && !inRange(c, 48, 57)) {
                signs++;
            } else if (isLowerCase(c)) {
                upper++;
            } else if (isUpperCase(c)) {
                lower++;
            }
        }

        int letters = upper + lower;
        return letters > 50 && (length - letters) == signs && lower > 35 && upper >= 14;
    }

    private static boolean isNormalKey(String result) {
        int upper = 0;
        int lower = 0;
        int signs = 0;
        int length = result.length();
        for (int i = 0; i < length; i++) {
            char c = result.charAt(i);
            if (inRange(c, 32, 64) && !inRange(c, 48, 57)) {
                signs++;
            } else if (isLowerCase(c)) {
                upper++;
            } else if (isUpperCase(c)) {
                lower++;
            }
        }

        int letters = upper + lower;
        return length - (letters + signs) == 1;
    }

    private static boolean isUpperCase(char c) {
        return inRange(c, 65, 90);
    }

    private static boolean inRange(char c, int bottom, int top) {
        return c >= bottom && c <= top;
    }

    private static boolean isLowerCase(char c) {
        return inRange(c, 97, 122);
    }

    private static void tryGuessKey(String key, List<String> lines) {
        for (String line : lines) {
            List<String> collect = line.chars()
                    .mapToObj(i -> valueOf(((char) i)))
                    .collect(Collectors.toList());
            for (int i = 0; i < line.length(); i++) {
                System.out.print(xor(collect.get(i), valueOf(key.charAt(i))));
            }
            System.out.println();
        }
    }

    private static void tryXorRowsBetween(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.size(); j++) {
                if (i != j) {
                    String xor = getXorOfLines(lines, i, j);
                    System.out.println(i + "*");
                }
            }
        }
    }

    private static String getXorOfLines(List<String> lines, int i, int j) {
        return xor(lines.get(i), lines.get(j));
    }

    public static String xor(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int length = Math.min(a.length(), b.length());
        for (int i = 0; i < length; i++)
            sb.append((char) ((a.charAt(i) ^ b.charAt(i))));
        return sb.toString();
    }
}
