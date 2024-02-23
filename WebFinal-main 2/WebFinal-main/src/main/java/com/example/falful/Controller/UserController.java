package com.example.falful.Controller;

import com.example.falful.entity.Items;
import com.example.falful.entity.Order;
import com.example.falful.pojo.ContactPojo;
import com.example.falful.pojo.OrderPojo;
import com.example.falful.pojo.ShippingAddressPojo;
import com.example.falful.pojo.UserPojo;
import com.example.falful.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Base64;
import java.util.Collection;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

private final UserService userService;
private  final ItemsServices itemsServices;
private  final ContactService contactService;
private  final ShippingServices shippingServices;
private  final OrderServices orderServices;

    @GetMapping("/create")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new UserPojo());
        return "User/signup";
    }
    @PostMapping("/saveuser")
    public String createUser(@Valid UserPojo userPojo){
        userService.save(userPojo);
        return "Saved Successfully";
    }

    @GetMapping("/homepage")
    public String getHome(Model model, Authentication authentication) {

        if (authentication!=null){
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().equals("Admin")) {
                    return "redirect:/admin/viewallproduct";
                }
            }
        }
        List<Items> items = itemsServices.getFourRandomData();
        model.addAttribute("productList", items);
        return "User/index";
    }

    @GetMapping("/profile")
    public String Profile(Model model,Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "User/login";
        }
        model.addAttribute("update", new UserPojo());
        model.addAttribute("loggeduser",userService.findByEmail(principal.getName()));


        return "User/Profile";
    }



    @GetMapping("/myorder/{id}")
    public  String getAppliedJos(@PathVariable("id") Integer id, Model model,Principal principal){
        List<Order> order = orderServices.findorderById(id);
        model.addAttribute("productList", order);
        model.addAttribute("userdata",userService.findByEmail(principal.getName()));
        return "User/MyOrder";
    }

    @GetMapping("/deleteorder/{id}")
    public String deleteContact(@PathVariable("id") Integer id) {
        orderServices.deleteByID(id);
        return "redirect:/user/profile";
    }



    @PostMapping("/updateuser")
    public String updateUser(@Valid UserPojo userPojo){
        userService.save(userPojo);
        return "redirect:/user/profile";
    }



    //    Contact page
    @GetMapping("/contact")
    public String getContact(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "User/login";
        }
        model.addAttribute("contact", new ContactPojo());
        model.addAttribute("info",userService.findByEmail(principal.getName()));
        return "User/contact";
    }


//    Save Contact
    @PostMapping("/saveContact")
    public String saveContact(@Valid ContactPojo contactPojo){
    contactService.save(contactPojo);
    return "redirect:/user/contact";
}



// about page

    @GetMapping("/about")
    public String getAbout() {
        return "User/about";
    }


//
//    ----- Checkout -----
//

    @GetMapping("/billingpage/{id}")
    public String getCartpage(@PathVariable("id") Integer id ,Model model,Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "User/login";
        }
        model.addAttribute("shipping",new ShippingAddressPojo());
        model.addAttribute("orderplace",new OrderPojo());
        Items items=itemsServices.fetchById(id);
        model.addAttribute("findItems",items);
        model.addAttribute("loggeduser",userService.findByEmail(principal.getName()));
        return "User/checkout";
    }

    @PostMapping("/saveAddress")
    public String saveAddress(@Valid ShippingAddressPojo shippingAddressPojo,OrderPojo orderPojo){
        shippingServices.save(shippingAddressPojo);
        orderServices.save(orderPojo);
        return "redirect:/user/allproduct";
    }




    @GetMapping("/allproduct")
    public String getShop(Model model) {
        List<Items> items = itemsServices.fetchAll();
//        model.addAttribute("allproduct", items.stream().map(item ->
//                Items.builder()
//                        .id(item.getId())
//                        .imageBase64(getImageBase64(item.getImage()))
//                        .price(item.getPrice())
//                        .items_name(item.getItems_name())
//                        .quantity(item.getQuantity())
//                        .build()
//        ));
        model.addAttribute("allproduct", items);
        return "User/shop";
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