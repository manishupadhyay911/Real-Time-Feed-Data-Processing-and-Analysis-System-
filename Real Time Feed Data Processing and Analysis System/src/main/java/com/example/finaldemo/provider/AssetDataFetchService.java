package com.example.finaldemo.provider;

import com.example.finaldemo.constants.URLConstants;
import com.example.finaldemo.proto.AssetDTO;
import com.example.finaldemo.proto.HistoryAssetDto;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AssetDataFetchService {

    public AssetDTO getAsset(String metalCode, String currency) throws InvalidProtocolBufferException {
        String uri = String.format(URLConstants.METAL_SPOT_PRICE, metalCode, currency, "g");
        RestTemplate restTemplate = new RestTemplate();
        String jsonData = restTemplate.getForObject(uri, String.class);
        AssetDTO.Builder assetDtoBuilder = AssetDTO.newBuilder();
        JsonFormat.parser().merge(jsonData, assetDtoBuilder);
        assetDtoBuilder.setMetal(metalCode).setCurrency(currency);
        return assetDtoBuilder.build();
    }

    public HistoryAssetDto getHistoryAssets() {
        String uri = URLConstants.METAL_SPOT_PRICES;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, HistoryAssetDto.class);
    }
}
