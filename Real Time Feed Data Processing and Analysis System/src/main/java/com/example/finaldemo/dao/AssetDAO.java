package com.example.finaldemo.dao;

import com.example.finaldemo.proto.AssetDtoResponse;
import com.example.finaldemo.proto.AssetPageResponse;
import com.example.finaldemo.proto.entity.AssetEntity;
import com.example.finaldemo.proto.entity.AssetEntityList;
import com.example.finaldemo.proto.entity.AssetEntityPageResponse;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface AssetDAO {
    public AssetEntity fetchLiveAssetData(String metal, String currency, String unit);

    public AssetEntityList fetchAssetData();

    public String fetchAssetName(String metalCode);

    public boolean addAssetData(AssetEntity assetEntity);

    public AssetEntityList fetchAssets(Integer pageNum, Integer limit);

    public AssetEntityPageResponse fetchAssets(String pageToken, int limit, boolean reverse, Timestamp to, Timestamp from);

}
