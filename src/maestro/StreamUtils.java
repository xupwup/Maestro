package maestro;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 *
 * @author Rick
 */
public class StreamUtils {

    /**
     * get a 4 byte int byte order: least significant first
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static int getInt(InputStream in) throws IOException {
        byte[] bytes = getBytes(in, 4);
        int ret = 0;
        for (int i = 3; i >= 0; i--) {
            ret = ret << 8 | (bytes[i] & 0xFF);
        }
        return ret;
    }

    /**
     * get a 4 byte int byte order: least significant first
     *
     * @param buffer
     * @param offset
     * @return
     */
    public static int getInt(byte[] buffer, int offset) {
        int ret = 0;
        for (int i = 3; i >= 0; i--) {
            ret = ret << 8 | (buffer[i + offset] & 0xFF);
        }
        return ret;
    }

    /**
     * get a 4 byte int byte order: least significant first
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static int getInt(RandomAccessFile in) throws IOException {
        byte[] bytes = getBytes(in, 4);
        int ret = 0;
        for (int i = 3; i >= 0; i--) {
            ret = ret << 8 | (bytes[i] & 0xFF);
        }
        return ret;
    }

    public static byte[] getBytes(InputStream in, int count) throws IOException {
        byte[] bytes = new byte[count];
        int read = 0;
        while (read < count) {
            int rd = in.read(bytes, read, count - read);
            if (rd == -1) {
                throw new IOException("Stream ended.");
            }
            read += rd;
        }
        return bytes;
    }

    public static byte[] getBytes(RandomAccessFile in, int count) throws IOException {
        byte[] bytes = new byte[count];
        int read = 0;
        while (read < count) {
            int rd = in.read(bytes, read, count - read);
            if (rd == -1) {
                throw new IOException("Stream ended.");
            }
            read += rd;
        }
        return bytes;
    }

    /**
     * byte order: least significant first
     *
     * @param n
     * @return
     */
    public static byte[] getIntBytes(int n) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (n & 0xFF);
            n = n >>> 8;
        }
        return bytes;
    }

    public static boolean firstNEquals(byte[] a, byte[] b, int n) {
        if (a.length == b.length && a.length < n) {
            n = a.length;
        } else if (a.length > b.length && b.length < n) {
            return false;
        } else if (b.length > a.length && a.length < n) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
}
