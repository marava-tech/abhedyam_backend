package com.abhedyam.service.interfaces;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.VideoStoreCreateRequest;
import com.abhedyam.dto.VideoStoreResponse;
import com.abhedyam.dto.VideoStoreSearchResult;
import com.abhedyam.dto.VideoStoreUpdateRequest;
import com.abhedyam.model.VideoStore;

import java.util.List;
import java.util.UUID;

public interface IVideoStoreService {
    VideoStore create(VideoStoreCreateRequest request);

    VideoStoreResponse getById(UUID id);

    PageResponse<VideoStoreResponse> list(int page, int size);

    VideoStore update(UUID id, VideoStoreUpdateRequest request);

    void delete(UUID id);

    List<VideoStoreSearchResult> search(String searchKey);
}
