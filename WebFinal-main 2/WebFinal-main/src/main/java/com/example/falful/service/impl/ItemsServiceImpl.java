package com.example.falful.service.impl;

import com.example.falful.entity.Items;
import com.example.falful.entity.User;
import com.example.falful.pojo.ItemsPojo;
import com.example.falful.repo.ItemsRepo;
import com.example.falful.service.ItemsServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemsServiceImpl implements ItemsServices {

    private final ItemsRepo itemsRepo;
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/falful/fruit_images/";


    @Override
    public ItemsPojo save(ItemsPojo itemsPojo) throws IOException {
        Items items;
        if (itemsPojo.getId() != null) {
            items = itemsRepo.findById(itemsPojo.getId()).orElseThrow(() -> new RuntimeException("Not Found"));
        } else {
            items = new Items();
        }
        items.setItems_name(itemsPojo.getItems_name());
        items.setPrice(itemsPojo.getPrice());
        items.setQuantity(itemsPojo.getQuantity());

        if(itemsPojo.getImage()!=null){
            StringBuilder fileNames = new StringBuilder();
            System.out.println(UPLOAD_DIRECTORY);
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, itemsPojo.getImage().getOriginalFilename());
            fileNames.append(itemsPojo.getImage().getOriginalFilename());
            Files.write(fileNameAndPath, itemsPojo.getImage().getBytes());

            items.setImage(itemsPojo.getImage().getOriginalFilename());

    }
        itemsRepo.save(items);
        return new ItemsPojo(items);
    }

    @Override
    public List<Items> fetchAll() {
        return findAllInList(itemsRepo.findAll());
    }

    @Override
    public Items fetchById(Integer id) {
        return itemsRepo.findById(id).orElseThrow(()->new RuntimeException("not found"));
    }

    @Override
    public void deleteById(Integer id) {
        itemsRepo.deleteById(id);
    }

    public List<Items> getFourRandomData() {
        return findAllInList(itemsRepo.findFourRandomData());
    }

    public List<Items> findAllInList(List<Items> list) {
        Stream<Items> allWithImage = list.stream().map(items ->
                Items.builder()
                        .id(items.getId())
                        .imageBase64(getImageBase64(items.getImage()))
                        .price(items.getPrice())
                        .items_name(items.getItems_name())
                        .quantity(items.getQuantity())
                        .build());
        list=allWithImage.toList();
        return list;
    }

    public String getImageBase64(String fileName) {
        if (fileName!=null) {
            String filePath = System.getProperty("user.dir")+"/falful/fruit_images/";
            File file = new File(filePath + fileName);
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }

}
