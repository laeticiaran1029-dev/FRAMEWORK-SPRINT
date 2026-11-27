package framework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Mapfile {
    private String fileName;
    private String extension;
    private byte[] content;

    public Mapfile(String fileName, String extension, byte[] content) {
        this.fileName = fileName;
        this.extension = extension;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    // Ecrit le fichier dans le dossier "directory" (valeur lue dans web.xml)
    // et retourne le chemin complet du fichier cree.
    public String save(String directory) throws IOException {
        Path dir = Paths.get(directory);
        Files.createDirectories(dir);
        // getFileName() retire tout chemin du nom envoye par le navigateur (ex: ../../x)
        Path target = dir.resolve(Paths.get(fileName).getFileName());
        Files.write(target, content);
        return target.toString();
    }
}
