package com.example.finaldemo.scheduler;

import com.example.finaldemo.proto.AssetDTO;
import com.example.finaldemo.service.AssetService;
import com.example.finaldemo.provider.AssetDataFetchService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class AssetScheduler {
    private final AssetService assetService;
    private final AssetDataFetchService assetDataFetchService;

    public AssetScheduler(AssetDataFetchService assetDataFetchService, AssetService assetService) {
        this.assetDataFetchService = assetDataFetchService;
        this.assetService = assetService;
    }

    @Scheduled(fixedRate = 300000)
    public void addLiveData() throws InvalidProtocolBufferException {
        List<String> currency = new ArrayList<>(Arrays.asList("INR", "USD", "SGD", "PKR"));
        List<String> metals = new ArrayList<>(Arrays.asList("XAG", "XAU", "XPT", "XPD"));
        for (String metal : metals) {
            for (String curr : currency) {
                AssetDTO assetDTO = assetDataFetchService.getAsset(metal, curr);
                assetService.addLiveAssetData(assetDTO);
            }
        }

    }
}
