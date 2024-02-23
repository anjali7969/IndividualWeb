package com.example.falful.Controller;
import com.example.falful.entity.Contact;
import com.example.falful.entity.Items;
import com.example.falful.entity.Order;
import com.example.falful.entity.ShippingAddress;
import com.example.falful.pojo.ItemsPojo;
import com.example.falful.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private  final ItemsServices itemsServices;
    private  final ContactService contactService;
    private  final ShippingServices shippingServices;
    private  final OrderServices orderServices;
    private  final UserService userService;


    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        // Add any necessary attributes to the model
        return "Admin/dashboard"; // Assuming "dashboard" is the name of the view file
    }

    //
//    ----Add items-----
//
    @GetMapping("/additems")
    public String getAddItemsForm(Model model) {
        model.addAttribute("items", new ItemsPojo());
        return "Admin/addItems";
    }

    @PostMapping("/additems")
    public String saveItems(@Valid ItemsPojo itemsPojo,BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {

        Map<String, String> requestError = validateRequest(bindingResult);
        if (requestError != null) {
            redirectAttributes.addFlashAttribute("requestError", requestError);
            return "redirect:/admin/additems";
        }
        itemsServices.save(itemsPojo);
        redirectAttributes.addFlashAttribute("successMsg", "Items saved successfully");
        return "redirect:/admin/viewallproduct";
    }


//
//
//    ==== product =====
// ViewBillingaddress
    @GetMapping("/viewallproduct")
    public String getShop(Model model, Principal principal) {
        List<Items> items = itemsServices.fetchAll();
        model.addAttribute("allproduct", items);
        model.addAttribute("logged",userService.findByEmail(principal.getName()));
        return "Admin/MyItems";
    }

    @GetMapping("/editproduct/{id}")
    public String editBlog(@PathVariable("id") Integer id, Model model) {
        Items items = itemsServices.fetchById(id);
        model.addAttribute("items", new ItemsPojo(items));
        return "Admin/addItems";
    }

    @GetMapping("/deleteItems/{id}")
    public String deleteBlog(@PathVariable("id") Integer id) {
        itemsServices.deleteById(id);
        return "redirect:/admin/viewallproduct";
    }

//
//    --- view contact ---
//
//  ----- fetch All contact -----
//
    @GetMapping("/allcontact")
    public String getContactList(Model model, Principal principal) {
        List<Contact> contacts = contactService.findAll();
        model.addAttribute("allcontact", contacts);
        model.addAttribute("logged", userService.findByEmail(principal.getName()));
        return "Admin/ContactList";
    }
    @GetMapping("/deletecontact/{id}")
    public String deleteContact(@PathVariable("id") Integer id) {
        contactService.deleteById(id);
        return "redirect:/admin/allcontact";
    }

//
//    --- view all shipping---
//
    @GetMapping("/viewallshipping")
    public String getShippingList(Model model,Principal principal) {
    List<ShippingAddress> shippingAddresses = shippingServices.findAll();
        model.addAttribute("allshipping", shippingAddresses);
        model.addAttribute("logged",userService.findByEmail(principal.getName()));

    return "Admin/ViewShipping";
}

    @GetMapping("/ViewBillingaddress")
    public String getBillingList(Model model,Principal principal) {
        List<ShippingAddress> shippingAddresses = shippingServices.findAll();
        model.addAttribute("allbills", shippingAddresses);
        model.addAttribute("logged",userService.findByEmail(principal.getName()));

        return "Admin/ViewBillingdetails";
    }



    //
//    --- view all orders---
//
    @GetMapping("/viewallorders")
    public String getAllOrder(Model model,Principal principal) {
        List<Order> orders = orderServices.fetchAll();
        model.addAttribute("allorders", orders);
        model.addAttribute("logged",userService.findByEmail(principal.getName()));
        return "Admin/ViewOrder";
    }




    @GetMapping("/admindas")
    public String getAbout() {
        return "Admin/dashboard";
    }



    public String getImageBase64(String fileName) {
        if (fileName!=null) {
            String filePath = System.getProperty("user.dir")+ "/falful/fruit_images/";
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


    public Map<String, String> validateRequest(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return null;
        }
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return errors;

    }

}