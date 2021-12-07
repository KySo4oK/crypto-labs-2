import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {

    public static void main(String[] args) throws IOException, DecoderException {
        List<String> lines = Files.lines(Paths.get("data.txt")).collect(Collectors.toList());

        HashMap<Integer, String> xorMap = getXorMap(lines);

        String firstRow = "For who would bear the whips and scorns of time,".repeat(48);

        xorAndPrint(lines, xorMap, firstRow);

    }

    private static void xorAndPrint(List<String> lines, HashMap<Integer, String> xorMap, String firstRow) throws DecoderException {
        for (int i = 1; i < lines.size(); i++) {
            String xorOfXor = xorMap.get(i);
            String hexResult = xorHex(xorOfXor, toHex(firstRow));
            System.out.println(fromHex(hexResult));
        }
    }

    private static HashMap<Integer, String> getXorMap(List<String> lines) {
        HashMap<Integer, String> map = new HashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            map.put(i, xorHex(lines.get(0), lines.get(i)));
        }
        return map;
    }

    public static String toHex(String str) {
        return Hex.encodeHexString(str.getBytes(UTF_8));
    }

    public static String fromHex(String str) throws DecoderException {
        byte[] bytes = Hex.decodeHex(str.toCharArray());
        return new String(bytes, UTF_8);
    }

    private static int fromHex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        throw new IllegalArgumentException();
    }

    public static String xorHex(String a, String b) {
        int length = Math.min(a.length(), b.length());
        char[] chars = new char[length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = toHex(fromHex(a.charAt(i)) ^ fromHex(b.charAt(i)));
        }
        return new String(chars);
    }

    private static char toHex(int nybble) {
        if (nybble < 0 || nybble > 15) {
            throw new IllegalArgumentException();
        }
        return "0123456789ABCDEF".charAt(nybble);
    }

}
