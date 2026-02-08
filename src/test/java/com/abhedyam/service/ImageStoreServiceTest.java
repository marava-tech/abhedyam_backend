package com.abhedyam.service;

import com.abhedyam.dto.ImageStoreCreateRequest;
import com.abhedyam.dto.ImageStoreUpdateRequest;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.ImageStore;
import com.abhedyam.repository.ImageStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageStoreServiceTest {

    @Mock
    private ImageStoreRepository imageStoreRepository;

    @InjectMocks
    private ImageStoreService imageStoreService;

    private ImageStore entity;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        entity = new ImageStore();
        entity.setId(id);
        entity.setName("Rice");
        entity.setTags(List.of("rice", "grocery"));
        entity.setImageUrl("https://example.com/rice.png");
        entity.setDescription("Rice bag");
        entity.setIsActive(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
    }

    @Test
    void create_success() {
        ImageStoreCreateRequest request = new ImageStoreCreateRequest();
        request.setName("Rice");
        request.setTags(List.of("rice"));
        request.setImageUrl("https://example.com/rice.png");
        when(imageStoreRepository.save(any(ImageStore.class))).thenReturn(entity);

        ImageStore result = imageStoreService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Rice");
        verify(imageStoreRepository).save(any(ImageStore.class));
    }

    @Test
    void getById_success() {
        when(imageStoreRepository.findById(id)).thenReturn(Optional.of(entity));

        var response = imageStoreService.getById(id);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Rice");
    }

    @Test
    void getById_notFound() {
        when(imageStoreRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageStoreService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void list_success() {
        Page<ImageStore> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);
        when(imageStoreRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(page);

        var result = imageStoreService.list(0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Rice");
    }

    @Test
    void update_success() {
        when(imageStoreRepository.findById(id)).thenReturn(Optional.of(entity));
        when(imageStoreRepository.save(any(ImageStore.class))).thenReturn(entity);
        ImageStoreUpdateRequest request = new ImageStoreUpdateRequest();
        request.setName("Rice Premium");

        ImageStore result = imageStoreService.update(id, request);

        assertThat(result).isNotNull();
        verify(imageStoreRepository).save(eq(entity));
        assertThat(entity.getName()).isEqualTo("Rice Premium");
    }

    @Test
    void update_notFound() {
        when(imageStoreRepository.findById(id)).thenReturn(Optional.empty());
        ImageStoreUpdateRequest request = new ImageStoreUpdateRequest();

        assertThatThrownBy(() -> imageStoreService.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success() {
        when(imageStoreRepository.findById(id)).thenReturn(Optional.of(entity));

        imageStoreService.delete(id);

        verify(imageStoreRepository).delete(entity);
    }

    @Test
    void delete_notFound() {
        when(imageStoreRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageStoreService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void search_emptyKey_returnsEmpty() {
        List<com.abhedyam.dto.ImageStoreSearchResult> result = imageStoreService.search("   ");

        assertThat(result).isEmpty();
    }

    @Test
    void search_nullKey_returnsEmpty() {
        List<com.abhedyam.dto.ImageStoreSearchResult> result = imageStoreService.search(null);

        assertThat(result).isEmpty();
    }

    @Test
    void search_byName_returnsResults() {
        when(imageStoreRepository.findByNameContainingIgnoreCase(eq("rice"), any(Pageable.class)))
                .thenReturn(List.of(entity));
        when(imageStoreRepository.findByTagContainingIgnoreCase(any(), any(Pageable.class)))
                .thenReturn(List.of());

        List<com.abhedyam.dto.ImageStoreSearchResult> result = imageStoreService.search("rice");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rice");
    }
}
