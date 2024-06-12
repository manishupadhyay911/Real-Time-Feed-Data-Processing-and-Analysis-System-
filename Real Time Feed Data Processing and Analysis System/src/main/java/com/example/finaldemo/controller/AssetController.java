package com.example.finaldemo.controller;

import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.proto.AssetDTO;
import com.example.finaldemo.proto.AssetDtoResponse;
import com.example.finaldemo.proto.AssetPageResponse;
import com.example.finaldemo.service.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Optional;

@RestController(ResourceConstants.ASSET_CONTROLLER)
public class AssetController {
    private final AssetService assetService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);

    public AssetController(@Qualifier(ResourceConstants.SERVICE_ASSET) AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/live-asset")
    public ResponseEntity<AssetDTO> getLiveAsset(@RequestParam String metal,
                                                 @RequestParam String currency,
                                                 @RequestParam(required = false) String unit) {
        LOGGER.info("Received Request to fetch live asset data");
       AssetDTO assetDto = assetService.fetchLiveAssetData(metal, currency, unit);
       return ResponseEntity.status(HttpStatus.OK).body(assetDto);
    }

    @GetMapping(value = "/all-assets")
    public ResponseEntity<AssetDtoResponse> getAllAssets() {
        LOGGER.info("Received Request to fetch all asset data");
        AssetDtoResponse assetDtoResponse = assetService.getAssets();
        return ResponseEntity.status(HttpStatus.OK).body(assetDtoResponse);
    }

    @GetMapping(value = "/asset")
    public ResponseEntity<AssetPageResponse> getAssets(@RequestParam String pageToken,
                                                       @RequestParam(required = false) Optional<Integer> limit,
                                                       @RequestParam(required = false) Optional<Boolean> reverse,
                                                       @RequestParam(required = false) Optional<Timestamp> to,
                                                       @RequestParam(required = false) Optional<Timestamp> from) {
        LOGGER.info("Received Request to fetch paginated asset data");
        AssetPageResponse assetPageResponse = assetService.getAssets(pageToken,
                limit.orElse(0),
                reverse.orElse(false),
                to,
                from
        );
        return ResponseEntity.status(HttpStatus.OK).body(assetPageResponse);
    }

}
