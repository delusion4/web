package eshop.web.controller;

import eshop.web.model.Category;
import eshop.web.model.Order;
import eshop.web.model.OrderStatus;
import eshop.web.model.Product;
import eshop.web.model.User;
import eshop.web.model.UserInformation;
import eshop.web.model.Vendor;
import eshop.web.service.CategoryService;
import eshop.web.service.OrderService;
import eshop.web.service.ProductService;
import eshop.web.service.UserService;
import eshop.web.service.VendorService;
import eshop.web.util.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final CategoryService categoryService;

    private final OrderService orderService;

    private final ProductService productService;

    private final UserService userService;

    private final VendorService vendorService;

    @GetMapping
    public String showAdminPanel() {
        return "admin/admin-panel";
    }

    @GetMapping("categories")
    public String showCategoryPage(Model model) {
        model.addAttribute("categories" ,categoryService.findAllCategories());
        return "admin/category/categories";
    }

    @GetMapping("categories/delete/{id}")
    public String deleteCategory(@PathVariable UUID id) {
        Category category = categoryService.findCategoryById(id);
        categoryService.deleteCategory(category);
        log.info("category with id {} deleted", id);
        return "redirect:/admin/categories";
    }

    @GetMapping("categories/edit/{id}")
    public String showCategoryEditPage(Model model, @PathVariable UUID id) {
        model.addAttribute("category", categoryService.findCategoryById(id));
        return "admin/category/category_form";
    }

    @GetMapping("categories/new")
    public String showNewCategoryPage(@ModelAttribute("category") Category category) {
        return "admin/category/category_new";
    }

    @PostMapping("categories/new")
    public String submitNew(@ModelAttribute("category") @Valid Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/category/category_new";
        }
        categoryService.saveCategory(category);
        log.info("category created: {}", category);
        return "redirect:/admin/categories";
    }

    @PostMapping("categories/edit/{id}")
    public String submitEdit(@ModelAttribute("category") @Valid Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/category/category_form";
        }

        categoryService.saveCategory(category);
        log.info("category saved: {}", category);
        return "redirect:/admin/categories";
    }

    @GetMapping("users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/user/users";
    }

    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {
        User user = userService.findUserById(id);
        userService.deleteUser(user);
        log.info("user with id {} deleted", id);
        return "redirect:/admin/users";
    }

    @GetMapping("users/edit/{id}")
    public String showUserEditPage(Model model, @PathVariable UUID id) {
        model.addAttribute("user", userService.findUserById(id));
        model.addAttribute("userInformation", userService.findUserById(id).getUserInformation());
        model.addAttribute("roles", Role.values());
        return "admin/user/user_form";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userInformation", new UserInformation());
        model.addAttribute("roles", Role.values());
        return "admin/user/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, @ModelAttribute UserInformation userInformation) {
        userService.connectUserWithInfo(user, userInformation);
        userService.saveUser(user);
        log.info("user saved: {}", user);
        return "redirect:/admin/users";
    }

    @GetMapping("products")
    public String productsPage(Model model) {
        model.addAttribute("products", productService.findAllProducts());
        model.addAttribute("listCategories", categoryService.findAllCategories());
        return "admin/product/products";
    }

    @GetMapping("products/delete/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        Product product = productService.findProductById(id);
        productService.deleteProduct(product);
        log.info("product with id {} deleted", id);
        return "redirect:/admin/products";
    }

    @GetMapping("products/edit/{id}")
    public String showProductEditPage(Model model, @PathVariable UUID id) {
        model.addAttribute("product", productService.findProductById(id));
        model.addAttribute("vendorList", vendorService.findAllVendors());
        model.addAttribute("categoryList", categoryService.findAllSubCategories());
        return "admin/product/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        log.info("product saved: {}", product);
        return "redirect:/admin/products";
    }

    @GetMapping("products/new")
    public String showProductEditPage(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("vendorList", vendorService.findAllVendors());
        model.addAttribute("categoryList", categoryService.findAllSubCategories());
        return "admin/product/product_form";
    }

    @GetMapping("products/search")
    public String searchProduct(HttpServletRequest request, Model model,
                                @RequestParam String keyword,
                                @RequestParam String categoryId) {
        UUID catId = UUID.fromString(categoryId);
        List<Product> products = productService.findByCategoryAndNamePart(keyword, categoryService.findCategoryById(catId));
        log.info("found products: {}", products);
        model.addAttribute("products", products);
        model.addAttribute("listCategories", categoryService.findAllCategories());
        return "admin/product/products";
    }

    @GetMapping("vendors")
    public String vendorsPage(Model model) {
        model.addAttribute("vendors", vendorService.findAllVendors());
        return "admin/vendor/vendors";
    }

    @GetMapping("vendors/edit/{id}")
    public String vendorEdit(Model model, @PathVariable UUID id) {
        model.addAttribute("vendor", vendorService.findVendorById(id));
        return "admin/vendor/vendor_form";
    }

    @PostMapping("vendors/save")
    public String vendorSave(@ModelAttribute Vendor vendor) {
        vendorService.saveVendor(vendor);
        log.info("vendor saved: {}", vendor);
        return "redirect:/admin/vendors";
    }

    @GetMapping("vendors/new")
    public String vendorCreate(Model model) {
        model.addAttribute("vendor", new Vendor());
        return "admin/vendor/vendor_form";
    }

    @GetMapping("vendors/delete/{id}")
    public String deleteVendor(Model model, @PathVariable UUID id) {
        vendorService.deleteVendor(vendorService.findVendorById(id));
        log.info("vendor with id {} deleted", id);
        return "redirect:/admin/vendors";
    }

    @GetMapping("orders")
    public String ordersPage(Model model) {
        model.addAttribute("orders", orderService.FindAllOrders());
        return "admin/orders/orders";
    }

    @GetMapping("orders/delete/{id}")
    public String deleteOrder(Model model, @PathVariable UUID id) {
        orderService.deleteOrder(orderService.FindOrderById(id));
        log.info("order with id {} deleted", id);
        return "redirect:/admin/orders";
    }

    @GetMapping("orders/edit/{id}")
    public String orderEdit(Model model, @PathVariable UUID id) {
        model.addAttribute("order", orderService.FindOrderById(id));
        model.addAttribute("OrderStatus", OrderStatus.values());
        return "admin/orders/order_form";
    }

    @PostMapping("orders/save")
    public String orderSave(@ModelAttribute Order order) {
        order.setQuantity(orderService.FindOrderById(order.getId()).getQuantity());
        order.setProduct(orderService.FindOrderById(order.getId()).getProduct());
        orderService.saveOrder(order);
        return "redirect:/admin/orders";
    }
}
