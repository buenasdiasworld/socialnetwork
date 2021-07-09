package main.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import main.AbstractIntegrationIT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


public class StorageControllerIT extends AbstractIntegrationIT {

  public String originalImage = "originalImage.jpg";

  @Value("${upload.path}")
  public String uploadPath;

  @WithUserDetails("user@user.ru")
  @Test
  public void shouldStore() throws Exception {

    File imgs = new File("imgs");
    imgs.mkdir();

    BufferedImage bufferedImage = new BufferedImage(600, 600,
        BufferedImage.TYPE_INT_RGB);

    File f = new File(uploadPath + "/" + originalImage);

    ImageIO.write(bufferedImage, "jpg", f);

    byte[] bytes = Files.readAllBytes(f.toPath());
    long bytesLength = bytes.length;

    MockMultipartFile file
        = new MockMultipartFile("file", originalImage, MediaType.IMAGE_JPEG_VALUE,
        bytes); // ! name = controller param name !!

    ResultActions resultActions = mockMvc
        .perform(multipart("/api/v1/storage").file(file).param("type", "IMAGE"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.bytes").value(bytesLength));

    MvcResult result = resultActions.andReturn();
    String generatedPhotoName = JsonPath
        .read(result.getResponse().getContentAsString(), "$.data.fileName");

    File f1 = new File(uploadPath + "/" + generatedPhotoName);

    f.delete();
    f1.delete();
    imgs.delete();


  }

}
