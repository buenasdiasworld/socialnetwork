package main.service;

import lombok.RequiredArgsConstructor;
import main.core.ContextUtilities;
import main.data.response.base.Response;
import main.data.response.type.Storage;
import main.model.Person;
import main.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    private static final String IMG_DIR = "/img/";

    private final PersonRepository personRepository;

    @Value("${upload.path}")
    public String uploadPath;

    public Response<Storage> store(MultipartFile file, String type) {
        String originalFilename = file.getOriginalFilename();
        int originalFileLastDot = (originalFilename != null) ? originalFilename.lastIndexOf(".") : -1;
        String fileFormat = (originalFilename != null) ? originalFilename.substring(originalFileLastDot + 1) : "";
        String uuidFile = UUID.randomUUID().toString();
        String resultName = uuidFile + "_" + originalFilename;

        try {
            file.transferTo(Paths.get(uploadPath + "/" + resultName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Person photoOwner = ContextUtilities.getCurrentPerson();
        photoOwner.setPhotoURL(IMG_DIR + resultName);
        personRepository.save(photoOwner);

        Storage storage = new Storage();

        storage.setId(IMG_DIR + resultName);
        storage.setOwnerId(photoOwner.getId());
        storage.setFileName(resultName);
        storage.setRelativeFilePath("/" + resultName);
        storage.setRawFileURL(IMG_DIR + resultName);
        storage.setFileFormat(fileFormat);
        storage.setBytes(file.getSize());
        storage.setFileType(type);
        storage.setCreatedAt(new Date().getTime());

        Response<Storage> response = new Response<>();
        response.setData(storage);

        return response;
    }


}
