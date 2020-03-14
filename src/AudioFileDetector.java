
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AudioFileDetector {
	public static List<File> songsFileList;

	public static List<File> searchAudio(String path) {
		try {
			Path dir = new File(path).toPath();
			DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
			songsFileList = new ArrayList<>();
			for (Path path2 : stream) {
				String audioPath = path2.toString();
				if (audioPath.endsWith(".mp3") && (new File(audioPath).isDirectory() == false)) {
					System.out.println(new File(audioPath).getName());
					songsFileList.add(new File(audioPath));
				} else if (new File(audioPath).isDirectory()) {
					searchAudio(audioPath);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return songsFileList;
	}
}
