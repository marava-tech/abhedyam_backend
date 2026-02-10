package com.abhedyam.service.interfaces;

import com.abhedyam.dto.ImageStoreCreateRequest;
import com.abhedyam.dto.ImageStoreResponse;
import com.abhedyam.dto.ImageStoreSearchResult;
import com.abhedyam.dto.ImageStoreUpdateRequest;
import com.abhedyam.dto.PageResponse;
import com.abhedyam.model.ImageStore;

import java.util.List;
import java.util.UUID;

public interface IImageStoreService {

    ImageStore create(ImageStoreCreateRequest request);

    PageResponse<ImageStoreResponse> list(int page, int size);

    ImageStore update(UUID id, ImageStoreUpdateRequest request);

    List<ImageStoreSearchResult> search(String searchKey);
}
