package elasticbeanstalk.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {
	
	private final Path rootLocation;
	
	@Autowired
	public FileSystemStorageService(StorageProperties properties){
		this.rootLocation = Paths.get(properties.getLocation());
	}	
	
	@Override
	public File store(MultipartFile file){
		try{
			if(file.isEmpty()){
				throw new StorageException("Fail to store empty file" + file.getOriginalFilename());
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
		} catch (IOException e){
			throw new StorageException("Fail to store file" + file.getOriginalFilename(), e);
		}
		String path = String.valueOf(this.rootLocation.resolve(file.getOriginalFilename()));
		return new File(path);
	}

	
	@Override
	public Stream<Path> loadAll(){
		List<Path> path = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.rootLocation)){
			for (Path entry: stream){
			path.add(rootLocation.relativize(entry));
			}
		} catch (IOException e){
			throw new StorageException("Fail to read stored files", e);
		}
		return (Stream<Path>) path;
	}
	
	@Override
	public Path load(String filename){
		return rootLocation.resolve(filename);
	}
	
	@Override
	public Resource loadAsResource(String filename){
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if(resource.exists() || resource.isReadable()){
				return resource;
			}
			else{
				throw new StorageFileNotFoundException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e){
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}
	
	@Override
	public void deleteAll(){
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}
	
	@Override
	public void init(){
		try{
			Files.createDirectory(rootLocation);
		} catch (IOException e){
			throw new StorageException("Could not initialze storage", e);
		}
	}	
}
