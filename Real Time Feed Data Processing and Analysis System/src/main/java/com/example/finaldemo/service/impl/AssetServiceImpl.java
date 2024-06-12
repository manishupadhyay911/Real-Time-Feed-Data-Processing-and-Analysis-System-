package com.example.finaldemo.service.impl;

import com.example.finaldemo.constants.DBConstants;
import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.dao.AssetDAO;
import com.example.finaldemo.proto.AssetDTO;
import com.example.finaldemo.proto.AssetDtoResponse;
import com.example.finaldemo.proto.AssetPageResponse;
import com.example.finaldemo.proto.entity.AssetEntity;
import com.example.finaldemo.proto.entity.AssetEntityList;
import com.example.finaldemo.proto.entity.AssetEntityPageResponse;
import com.example.finaldemo.service.AssetService;
import com.example.finaldemo.utility.DateTimeUtils;
import com.example.finaldemo.utility.exception.AssetExceptionHandler;
import com.example.finaldemo.helper.mapper.DTOMapper;
import com.example.finaldemo.helper.mapper.EntityMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service(ResourceConstants.SERVICE_ASSET)
public class AssetServiceImpl implements AssetService {
    private final AssetDAO assetDAO;

    public AssetServiceImpl(@Qualifier(ResourceConstants.DAO_ASSET) AssetDAO assetDAO) {
        this.assetDAO = assetDAO;
    }

    @Override
    public AssetDTO fetchLiveAssetData(String metal, String currency, String unit) {
        if (metal==null || currency==null) {
            throw new AssetExceptionHandler("Insufficient values provided");
        }
        AssetEntity assetEntity = assetDAO.fetchLiveAssetData(metal, currency, unit);
        AssetDTO assetDTO = DTOMapper.assetDTO(assetEntity);
        if (assetDTO==null) {
            throw new AssetExceptionHandler("Invalid values provided");
        }
        return assetDTO;
    }

    @Override
    public AssetDtoResponse getAssets() {
        AssetEntityList assetEntityList = assetDAO.fetchAssetData();
        List<AssetDTO> assetDTOList = DTOMapper.assetDTOList(assetEntityList.getAssetEntityListList());
        return AssetDtoResponse.newBuilder().addAllAssetResponse(assetDTOList).build();
    }

    @Override
    public void addLiveAssetData(AssetDTO assetDTO) {
        AssetEntity assetEntity = EntityMapper.assetEntity(assetDTO);
        boolean inserted = assetDAO.addAssetData(assetEntity);
        if (!inserted) {
            throw new AssetExceptionHandler("Asset Insert Failed");
        }

    }

    @Override
    public String fetchAssetName(String metalCode) {
        return assetDAO.fetchAssetName(metalCode);
    }

    @Override
    public AssetDtoResponse getAssets(Integer pageNum, Integer limit) {
        if (limit <= 0 || pageNum <= 0) {
            throw new AssetExceptionHandler("Invalid value provided");
        }
        limit = Math.min(limit, 10);
        int offset = (pageNum - 1) * limit;
        offset = Math.max(0, offset);
        AssetEntityList assetEntityList = assetDAO.fetchAssets(offset, limit);
        List<AssetDTO> assetDTOList = DTOMapper.assetDTOList(assetEntityList.getAssetEntityListList());
        return AssetDtoResponse.newBuilder().addAllAssetResponse(assetDTOList).build();

    }

    @Override
    public AssetPageResponse getAssets(String pageToken, int limit, boolean reverse, Optional<Timestamp> to , Optional<Timestamp> from) {
        if (limit <= 0) {
            throw new AssetExceptionHandler("Invalid value provided");
        }
        AssetEntityPageResponse assetEntityPageResponse = assetDAO.fetchAssets(pageToken,
                limit,
                reverse,
                to.orElse(Timestamp.valueOf(DBConstants.LARGEST_TIMESTAMP)),
                from.orElse(Timestamp.valueOf(DBConstants.SMALLEST_TIMESTAMP))
        );
        if (assetEntityPageResponse==null) {
            throw new AssetExceptionHandler("Invalid Token provided");
        }
         return DTOMapper.assetPageResponse(assetEntityPageResponse);
    }
}
