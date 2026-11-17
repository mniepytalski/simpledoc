package pl.cbr.notebook;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.sql.Date;

@Slf4j
public class FileStorage {

    public static FileData load(String fileName) {
        String fileLocation = getFileDataLocation(fileName);
        Date modificationDate = getModificationDate(fileLocation);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileLocation, StandardCharsets.UTF_8))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.lineSeparator();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return new FileData(stringBuilder.toString(), fileName, modificationDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean save(String fileName, String data) {
        String fileLocation = getFileDataLocation(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
            outputStream.write(data
//                    .replaceAll("\n+", "\n")
                    .getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static boolean delete(String fileName) {
        String fileLocation = getFileDataLocation(fileName);
        File file = new File(fileLocation);
        if (exists(fileLocation)) {
            return file.delete();
        }
        return false;
    }

    public static void copyFromResource(String resourceFileName, String targetLocation) {
        InputStream is = FileStorage.class.getResourceAsStream(resourceFileName);
        Path target = Paths.get(targetLocation);
        try {
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void copy(String sourceLocation, String targetLocation) {
        Path source = Paths.get(sourceLocation);
        Path target = Paths.get(targetLocation);
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void createDir(String dirName) {
        File dir = new File(dirName);
        if (!exists(dirName)) {
            boolean mkdirResult = dir.mkdir();
            log.info("mkdir: {}, {}", dirName, mkdirResult);
        }
    }

    public static String getFileDataLocation(String fileName) {
        File currentDir = new File(StorageConfig.CURRENT_DIR);
        String path = currentDir.getAbsolutePath();
        if (path.endsWith(StorageConfig.FILE_SUFFIX)) {
            path = path.substring(0, path.length() - 1);
        }
        return path + StorageConfig.FILE_SUFFIX + fileName;
    }

    private static Date getModificationDate(String absoluteFileName) {
        Path path = Paths.get(absoluteFileName);
        FileTime fileLastModifiedDate;
        try {
            fileLastModifiedDate = Files.getLastModifiedTime(path);
            return new Date(fileLastModifiedDate.toMillis());
        } catch (IOException e) {
            log.error("Cannot get the last modified time - ", e);
        }
        return new Date(System.currentTimeMillis());
    }

    private static boolean exists(String absoluteFileName) {
        File file = new File(absoluteFileName);
        return file.exists();
    }
}
