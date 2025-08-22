package pl.cbr.notebook;

public class FileData {
    private final String fileName;
    private final String data;
    private final String modificationDate;

    public FileData(String fileName, String data,  String modificationDate) {
        this.fileName = fileName;
        this.data = data;
        this.modificationDate = modificationDate;
    }

    public String getFileName() {
        return fileName;
    }

    public String getData() {
        return data;
    }

    public String getModificationDate() {
        return modificationDate;
    }
}
