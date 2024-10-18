package org.cardanofoundation.productaggregator.controller;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.cardanofoundation.productaggregator.model.domain.ProductAggregationRecord;
import org.cardanofoundation.productaggregator.service.ProductTracingService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Product Tracing Controller", description = "APIs for Product Tracing")
public class ProductTracingController {

    private final ProductTracingService productTracingService;

    @GetMapping("/productaggregation/latest")
    @Operation(summary = "Latest Product Aggregation",
            description = "Get the latest product aggregation")
    @ApiResponse(responseCode = "200", description = "Latest product aggregation found",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductAggregationRecord.class))})
    @ApiResponse(responseCode = "404", description = "Latest product aggregation not found",
            content = {@Content(schema = @Schema())})
    public ResponseEntity<ProductAggregationRecord> getLatestProductAggregation() {
        Optional<ProductAggregationRecord> latestProductAggregation = productTracingService.getLatestProductAggregation();
        return latestProductAggregation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/productaggregation/{slot}")
    @Operation(summary = "Product Aggregation for Slot",
            description = "Get the product aggregation for a specific slot")
    @ApiResponse(responseCode = "200", description = "Product aggregation found",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductAggregationRecord.class))})
    @ApiResponse(responseCode = "404", description = "Product aggregation not found",
            content = {@Content(schema = @Schema())})
    public ResponseEntity<ProductAggregationRecord> getProductAggregationForSlot(@PathVariable Long slot) {
        Optional<ProductAggregationRecord> productAggregation = productTracingService.getProductAggregationForSlot(slot);
        return productAggregation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/productaggregation")
    @Operation(summary = "All Product Aggregations",
            description = "Get all product aggregations")
    @ApiResponse(responseCode = "200", description = "All product aggregations found",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProductAggregationRecord.class)))
    })
    public ResponseEntity<List<ProductAggregationRecord>> getAllProductAggregations(Pageable pageable) {
        return ResponseEntity.ok(productTracingService.getAllProductAggregations(pageable));
    }

}
