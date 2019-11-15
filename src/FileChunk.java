import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class FileChunk {
    public static void splitFile(File f) throws IOException {
        int partCounter = 1;

        int sizeOfFiles = 1024 * 2;
        byte[] buffer = new byte[sizeOfFiles];

        String fileName = f.getName();

        try (FileInputStream fis = new FileInputStream(f);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                String filePartName = String.format("%s.%03d", fileName, partCounter++);
                File newFile = new File(f.getParent(), filePartName);
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, bytesAmount);
                }
            }
        }
    }

    public static void mergeFiles(File oneOfFiles, File into)
            throws IOException {
        mergeFiles(listOfFilesToMerge(oneOfFiles), into);
    }

    private static void mergeFiles(List<File> files, File into)
            throws IOException {
        try (FileOutputStream fos = new FileOutputStream(into);
             BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (File f : files) {
                Files.copy(f.toPath(), mergingStream);
            }
        }
    }

    public static List<File> listOfFilesToMerge(File oneOfFiles) {
        String tmpName = oneOfFiles.getName();
        String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));
        File[] files = oneOfFiles.getParentFile().listFiles(
                (File dir, String name) -> name.matches(destFileName + "[.]\\d+"));
        Arrays.sort(files);
        return Arrays.asList(files);
    }
}
