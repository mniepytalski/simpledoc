package pl.cbr.notebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.sql.Date;

public class FileStorage {

    public static FileData load(String fileName) {
        String fileLocation = getFileLocation(fileName);
        String modificationDate = getModificationDate(fileLocation);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileLocation))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.lineSeparator();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return new FileData(fileName, stringBuilder.toString(), modificationDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean save(String fileName, String data) {
        String fileLocation = getFileLocation(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
            outputStream.write(data.getBytes());
            return true;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static boolean delete(String fileName) {
        String fileLocation = getFileLocation(fileName);
        File file = new File(fileLocation);
        if (exists(fileLocation)) {
            return file.delete();
        }
        return false;
    }

    public static void copyToBackup(String sourceFileName, String targetFileName) {
        String sourceLocation = getFileLocation(sourceFileName);
        String targetLocation = getFileLocation(targetFileName);
        Path source = Paths.get(sourceLocation);
        Path target = Paths.get(targetLocation);
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDir(String dirName) {
        File dir = new File(dirName);
        if (!exists(dirName)) {
            dir.mkdir();
            System.out.println("mkdir: " + dirName);
        }
    }

    private static String getModificationDate(String absoluteFileName) {
        Path path = Paths.get(absoluteFileName);
        FileTime fileLastModifiedDate;
        try {
            fileLastModifiedDate = Files.getLastModifiedTime(path);
            Date date = new Date(fileLastModifiedDate.toMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return simpleDateFormat.format(date);
        } catch (IOException e) {
            System.err.println("Cannot get the last modified time - " + e);
        }
        return "";
    }

    private static boolean exists(String absoluteFileName) {
        File file = new File(absoluteFileName);
        return file.exists();
    }

    private static String getFileLocation(String fileName) {
        File currentDir = new File(StorageConfig.CURRENT_DIR);
        String path = currentDir.getAbsolutePath();
        if (path.endsWith(StorageConfig.FILE_SUFFIX)) {
            path = path.substring(0, path.length() - 1);
        }
        return path + StorageConfig.FILE_SUFFIX + fileName;
    }
}
