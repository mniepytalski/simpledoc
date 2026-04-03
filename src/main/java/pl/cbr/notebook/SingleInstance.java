package pl.cbr.notebook;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class SingleInstance {

    private static FileLock lock;

    public static boolean lockInstance() {
        try {
            String lockFilePath = "myapp.lock";
            RandomAccessFile raf = new RandomAccessFile(lockFilePath, "rw");
            FileChannel channel = raf.getChannel();
            lock = channel.tryLock();
            return lock != null;
        } catch (Exception e) {
            return false;
        }
    }
}
