package com.example.fm.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private static final String STORAGE_DIRECTORY = "src/uploads";

    public void saveFile(MultipartFile fileToSave) throws IOException {
        if (fileToSave == null) {
            throw new NullPointerException("fileToSave is null");
        }
        File targetFile = new File(STORAGE_DIRECTORY + File.separator + fileToSave.getOriginalFilename());

        // There are security issues with the way of saving, The hacker can manipulate
        // the file name and can save them in different location
        // so we need to check that whether the parent directory is same as what we
        // mentioned STORAGE_DIRECTORY
        if (!Objects.equals(STORAGE_DIRECTORY, targetFile.getParent())) {
            throw new SecurityException("Unsupported File name");
        }

        // This will replace the file which has same file name
        // here we can also put some other logic if same file name comes
        Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadFile(String fileName) throws FileNotFoundException {
        if (fileName == null)
            throw new NullPointerException("fileName is null");
        File fileToDownload = new File(STORAGE_DIRECTORY + File.separator + fileName);
        if (!Objects.equals(STORAGE_DIRECTORY, fileToDownload.getParent())) {
            throw new SecurityException("Unsupported File name");
        }

        if (!fileToDownload.exists()) {
            throw new FileNotFoundException("No File name: " + fileName);
        }

        return fileToDownload;
    }
}
