package ru.storage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import ru.storage.objectstorage.poster.Poster;
import ru.storage.objectstorage.poster.PosterController;
import ru.storage.objectstorage.poster.PosterService;

@TestMethodOrder(OrderAnnotation.class)
public class PosterControllerTest {

	private MockMvc mockMvc;

	@Mock
	private PosterService service;

	@InjectMocks
	private PosterController controller;

	// Static ID to be used across tests
	private static Long savedMetadataId;

	private static ClassPathResource resource = new ClassPathResource("src/test/java/ru/storage/assets/image.jpg");

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void testImageIsAvailable_Success() {
	}

	// Ordered Successful Tests

	@Test
	@Order(1)
	public void testSavePoster_Success() throws Exception {
		Long metadataId = 100L; // You can set this to any value you prefer
		MockMultipartFile image = new MockMultipartFile(
				"image", "image.jpg", "image/jpeg", "test image content".getBytes());

		Poster poster = new Poster();
		poster.setId(metadataId);
		when(service.savePoster(eq(metadataId), any(MultipartFile.class))).thenReturn(poster);

		mockMvc.perform(multipart("/api/v1/posters/upload")
				.file(image)
				.param("metadataId", metadataId.toString()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(metadataId));

		// Save the metadataId for use in subsequent tests
		savedMetadataId = metadataId;
	}

	@Test
	@Order(2)
	public void testGetImagesByContentMetadataId_Success() throws Exception {
		String contentMetadataIds = savedMetadataId.toString();

		List<byte[]> images = Arrays.asList("test image content".getBytes());

		when(service.getImagesByContentMetadataId(contentMetadataIds)).thenReturn(images);

		mockMvc.perform(get("/api/v1/posters/images/{contentMetadataIds}", contentMetadataIds))
				.andExpect(status().isOk());
	}

	@Test
	@Order(3)
	public void testUpdateExistingImage_Success() throws Exception {
		Long metadataId = savedMetadataId;
		MockMultipartFile image = new MockMultipartFile(
				"image", "updated_image.jpg", "image/jpeg", "updated image content".getBytes());

		doNothing().when(service).updateExistingImage(eq(metadataId), any(MultipartFile.class));

		mockMvc.perform(multipart("/api/v1/posters/change")
				.file(image)
				.param("metadataId", metadataId.toString())
				.with(request -> {
					request.setMethod("PUT");
					return request;
				}))
				.andExpect(status().isOk())
				.andExpect(header().string("message", "Постер успешно заменён на новый."));
	}

	@Test
	@Order(4)
	public void testDeletePostersByIds_Success() throws Exception {
		String contentMetadataIds = savedMetadataId.toString();

		doNothing().when(service).deleteByIds();

		mockMvc.perform(delete("/api/v1/posters/ids/{contentMetadataIds}", contentMetadataIds))
				.andExpect(status().isOk())
				.andExpect(header().string("message", "Выбранные постеры успешно удалены"));
	}

	// Exception Tests (Not Ordered)

	@Test
	public void testSavePoster_InvalidContentType() throws Exception {
		Long metadataId = 200L;
		MockMultipartFile image = new MockMultipartFile(
				"image", "file.txt", "text/plain", "test content".getBytes());

		mockMvc.perform(multipart("/api/v1/posters/upload")
				.file(image)
				.param("metadataId", metadataId.toString()))
				.andExpect(status().isBadRequest())
				.andExpect(header().string("message", "Ошибка при сохранении постера. Файл не является изображением."));
	}

	@Test
	public void testSavePoster_Exception() throws Exception {
		Long metadataId = 300L;
		MockMultipartFile image = new MockMultipartFile(
				"image", "image.jpg", "image/jpeg", "test image content".getBytes());

		when(service.savePoster(eq(metadataId), any(MultipartFile.class)))
				.thenThrow(new RuntimeException("Database error"));

		mockMvc.perform(multipart("/api/v1/posters/upload")
				.file(image)
				.param("metadataId", metadataId.toString()))
				.andExpect(status().isInternalServerError())
				.andExpect(header().string("message", "Ошибка при сохранении постера для видео."));
	}

	@Test
	public void testGetImagesByContentMetadataId_Exception() throws Exception {
		String contentMetadataIds = "400,500,600";

		when(service.getImagesByContentMetadataId(contentMetadataIds))
				.thenThrow(new RuntimeException("Database error"));

		mockMvc.perform(get("/api/v1/posters/images/{contentMetadataIds}", contentMetadataIds))
				.andExpect(status().isInternalServerError())
				.andExpect(header().string("message", "Ошибка при получении постеров."));
	}

	@Test
	public void testUpdateExistingImage_InvalidContentType() throws Exception {
		Long metadataId = savedMetadataId != null ? savedMetadataId : 700L;
		MockMultipartFile image = new MockMultipartFile(
				"image", "file.txt", "text/plain", "test content".getBytes());

		mockMvc.perform(multipart("/api/v1/posters/change")
				.file(image)
				.param("metadataId", metadataId.toString())
				.with(request -> {
					request.setMethod("PUT");
					return request;
				}))
				.andExpect(status().isBadRequest())
				.andExpect(header().string("message", "Постер успешно заменён на новый.")); // According to code
	}

	@Test
	public void testUpdateExistingImage_Exception() throws Exception {
		Long metadataId = savedMetadataId != null ? savedMetadataId : 800L;
		MockMultipartFile image = new MockMultipartFile(
				"image", "image.jpg", "image/jpeg", "test image content".getBytes());

		doThrow(new RuntimeException("Database error"))
				.when(service).updateExistingImage(eq(metadataId), any(MultipartFile.class));

		mockMvc.perform(multipart("/api/v1/posters/change")
				.file(image)
				.param("metadataId", metadataId.toString())
				.with(request -> {
					request.setMethod("PUT");
					return request;
				}))
				.andExpect(status().isInternalServerError())
				.andExpect(header().string("message", "Неудалось заменить существующий постер."));
	}

	@Test
	public void testDeletePostersByIds_Exception() throws Exception {
		String contentMetadataIds = savedMetadataId != null ? savedMetadataId.toString() : "900,1000";

		doThrow(new RuntimeException("Database error")).when(service).deleteByIds();

		mockMvc.perform(delete("/api/v1/posters/ids/{contentMetadataIds}", contentMetadataIds))
				.andExpect(status().isInternalServerError())
				.andExpect(header().string("message", "Ошибка при удалении постеров по их идентификаторам."));
	}
}
