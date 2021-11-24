package com.distichain.productsynchronizer.service;

import com.distichain.productsynchronizer.constant.ProductColumn;
import com.distichain.productsynchronizer.domain.Product;
import com.distichain.productsynchronizer.service.external.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Log4j2
@RequiredArgsConstructor
public class SynchronizeService {

    private final ProductService productService;

    @Value("${file.product.path}")
    private String productPathFile;
    @Value("${file.product-optimized.path}")
    private String productOptimizePathFile;

    public static Boolean isSynchronizing = false;
    public static Boolean isAllow = false;
    final ExecutorService executor = Executors.newFixedThreadPool(5);

    // delay 5 * 60 * 1000
    @Scheduled(fixedDelay = 5000)
    public void syncProduct() {
        if (!isAllow) return;

        isSynchronizing = true;

        // read file
        try {
            var file = new File(productPathFile);

            if (!file.exists())
                return;

            var workBook = new HSSFWorkbook(new FileInputStream(file));
            var sheet = workBook.getSheetAt(0);
            var rowNum = sheet.getLastRowNum() + 1;
            var colNum = sheet.getRow(0).getLastCellNum();

            // read header
            var colMapByName = new HashMap<String, Integer>();
            if (sheet.getRow(0).cellIterator().hasNext()) {
                for (int j = 0; j < colNum; j++) {
                    colMapByName.put(sheet.getRow(0).getCell(j).getStringCellValue(), j);
                }
            }

            for (var i = 1; i < rowNum; i++) {
                var row = sheet.getRow(i);
                var product = new Product();
                colMapByName.forEach((v, k) -> {
                    switch (ProductColumn.valueOf(v.toUpperCase())) {
                        case SKU:
                            product.setSku(row.getCell(colMapByName.get(v)).getStringCellValue());
                            break;
                        case TITLE:
                            product.setTitle(row.getCell(colMapByName.get(v)).getStringCellValue());
                            break;
                        case DESCRIPTION:
                            product.setDescription(row.getCell(colMapByName.get(v)).getStringCellValue());
                            break;
                        case PRICE:
                            product.setPrice(BigDecimal.valueOf(row.getCell(colMapByName.get(v)).getNumericCellValue()));
                            break;
                        case QUANTITY:
                            product.setQuantity((long) row.getCell(colMapByName.get(v)).getNumericCellValue());
                            break;
                    }
                });

                var productOpt = productService.getBySku(product.getSku());
                if (productOpt.isEmpty()) {
                    // create new product
                    productService.create(product);
                } else {
                    // update product
                    productService.update(product.getSku(), product);
                }
            }

            workBook.close();
        } catch (IOException e) {
            log.error("Fail to read file: {}", e.getMessage());
        } finally {
            isSynchronizing = false;
        }
    }

//     delay 5 * 60 * 1000
    @Scheduled(fixedDelay = 300000)
    public void syncProductOptimized() {
        if (!isAllow) return;

        isSynchronizing = true;

        // read file
        try {
            var file = new FileInputStream(productOptimizePathFile);
            var workBook = new HSSFWorkbook(file);
            var sheet = workBook.getSheetAt(0);
            var rowNum = sheet.getLastRowNum() + 1;
            var colNum = sheet.getRow(0).getLastCellNum();

            // read header
            var colMapByName = new HashMap<String, Integer>();
            if (sheet.getRow(0).cellIterator().hasNext()) {
                for (int j = 0; j < colNum; j++) {
                    colMapByName.put(sheet.getRow(0).getCell(j).getStringCellValue(), j);
                }
            }

            final var futures = new ArrayList<Future<?>>();
            for (var i = 1; i < rowNum; i++) {
                var row = sheet.getRow(i);
                var future = executor.submit(() -> {
                    var product = new Product();
                    colMapByName.forEach((v, k) -> {
                        switch (ProductColumn.valueOf(v)) {
                            case SKU:
                                product.setSku(row.getCell(colMapByName.get(v)).getStringCellValue());
                            case TITLE:
                                product.setTitle(row.getCell(colMapByName.get(v)).getStringCellValue());
                            case DESCRIPTION:
                                product.setDescription(row.getCell(colMapByName.get(v)).getStringCellValue());
                            case PRICE:
                                product.setPrice(BigDecimal.valueOf(row.getCell(colMapByName.get(v)).getNumericCellValue()));
                            case QUANTITY:
                                product.setQuantity(Long.valueOf(row.getCell(colMapByName.get(v)).getStringCellValue()));
                        }
                    });

                    var productOpt = productService.getBySku(product.getSku());
                    if (productOpt.isEmpty()) {
                        // create new product
                        productService.create(product);
                    } else {
                        // update product
                        productService.update(product.getSku(), product);
                    }
                });
                futures.add(future);
            }

            for (var future : futures) {
                future.get();
            }

            workBook.close();
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("Fail to read file: {}", e.getMessage());
        } finally {
            isSynchronizing = false;
        }
    }
}
