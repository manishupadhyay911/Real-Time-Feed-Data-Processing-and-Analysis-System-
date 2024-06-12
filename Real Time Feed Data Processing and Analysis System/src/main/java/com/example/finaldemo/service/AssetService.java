package com.example.finaldemo.service;

import com.example.finaldemo.proto.AssetDTO;
import com.example.finaldemo.proto.AssetDtoResponse;
import com.example.finaldemo.proto.AssetPageResponse;

import java.sql.Timestamp;
import java.util.Optional;


public interface AssetService {
     AssetDTO fetchLiveAssetData(String metal, String currency, String unit);

     AssetDtoResponse getAssets();

     void addLiveAssetData(AssetDTO assetDto);

     public String fetchAssetName(String metalCode);

     AssetDtoResponse getAssets(Integer pageNum, Integer limit);

     AssetPageResponse getAssets(String pageToken, int limit, boolean reverse, Optional<Timestamp> to, Optional<Timestamp> from);

}
