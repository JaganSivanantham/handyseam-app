package com.handyseam.app.order;

import com.handyseam.app.customer.Customer;
import com.handyseam.app.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64; // IMPORT ADDED
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/orders")
@SessionAttributes("currentOrder")
public class OrderFlowController {

    @Autowired private ShopOrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private MeasurementRepository measurementRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private PaymentRepository paymentRepository;

    // STEP 1: Create Order & Show Measurements
    @GetMapping("/create/tailoring/details")
    public String showMeasurementStep(@RequestParam Long customerId, Model model) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        ShopOrder order = new ShopOrder();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("DRAFT");
        order.setTotalAmount(0.0);

        Measurement m = new Measurement();
        m.setOrder(order);

        // Auto-fill logic
        List<Measurement> history = measurementRepository.findLatestByCustomer(customerId);
        if (!history.isEmpty()) {
            Measurement last = history.get(0);
            m.setLength(last.getLength()); m.setChest(last.getChest()); m.setShoulder(last.getShoulder());
            m.setSleeveLength(last.getSleeveLength()); m.setSleeveWidth(last.getSleeveWidth());
            m.setChestPit(last.getChestPit()); m.setWaistPit(last.getWaistPit()); m.setHipPit(last.getHipPit());
            m.setCollar(last.getCollar()); m.setCuffLength(last.getCuffLength()); m.setCuffWidth(last.getCuffWidth());
            m.setHeight(last.getHeight()); m.setHip(last.getHip()); m.setSeat(last.getSeat());
            m.setThigh(last.getThigh()); m.setKnee(last.getKnee()); m.setBottom(last.getBottom());
            m.setInSeam(last.getInSeam());
        }

        order.setMeasurement(m);
        model.addAttribute("currentOrder", order);
        return "order_step2_measurements";
    }

    @PostMapping("/saveMeasurements")
    public String saveMeasurements(@ModelAttribute("currentOrder") ShopOrder order,
                                   @ModelAttribute Measurement formData) {
        Measurement m = order.getMeasurement();
        m.setLength(formData.getLength()); m.setChest(formData.getChest()); m.setShoulder(formData.getShoulder());
        m.setSleeveLength(formData.getSleeveLength()); m.setSleeveWidth(formData.getSleeveWidth());
        m.setChestPit(formData.getChestPit()); m.setWaistPit(formData.getWaistPit()); m.setHipPit(formData.getHipPit());
        m.setCollar(formData.getCollar()); m.setCuffLength(formData.getCuffLength()); m.setCuffWidth(formData.getCuffWidth());
        m.setHeight(formData.getHeight()); m.setHip(formData.getHip()); m.setSeat(formData.getSeat());
        m.setThigh(formData.getThigh()); m.setKnee(formData.getKnee()); m.setBottom(formData.getBottom());
        m.setInSeam(formData.getInSeam());

        return "redirect:/orders/create/outfits";
    }

    // STEP 2: Show Outfits
    @GetMapping("/create/outfits")
    public String showAddOutfitsStep(@ModelAttribute("currentOrder") ShopOrder order, Model model) {
        model.addAttribute("newItem", new OrderItem());
        return "order_step3_outfits";
    }

    // --- REPLACED: ADD OUTFIT (With Camera Logic) ---
    // You need these imports at the top


    // --- ROBUST ADD OUTFIT (With Windows Fixes) ---
    @PostMapping("/addOutfit")
    public String addOutfit(@ModelAttribute("currentOrder") ShopOrder order,
                            @ModelAttribute OrderItem newItem,
                            @RequestParam("cameraImage") String cameraImage,
                            @RequestParam("styleSelect") String styleSelect, // Dropdown Value
                            @RequestParam("otherStyle") String otherStyle,   // Textbox Value
                            @RequestParam(value = "aiMode", defaultValue = "false") boolean aiMode) { // AI Flag

        newItem.setOrder(order);
        if(newItem.getQuantity() == null || newItem.getQuantity() < 1) newItem.setQuantity(1);

        // 1. HANDLE STYLE LOGIC
        if ("Others".equals(styleSelect)) {
            newItem.setStyleName(otherStyle); // Use the text input
        } else {
            newItem.setStyleName(styleSelect); // Use the dropdown
        }

        // 2. HANDLE IMAGE LOGIC
        if (cameraImage != null && !cameraImage.isEmpty()) {
            try {
                String cleanBase64 = cameraImage.split(",")[1];
                byte[] fabricBytes = Base64.getDecoder().decode(cleanBase64);

                // IF AI MODE IS ON -> RUN PYTHON
                if (aiMode) {
                    String projectDir = System.getProperty("user.dir");
                    Path fabricPath = Paths.get(projectDir, "temp_fabric.jpg");
                    Path resultPath = Paths.get(projectDir, "temp_result.jpg");

                    // Note: Ensure this path is correct based on your previous debugging
                    Path outlinePath = Paths.get(projectDir, "src", "main", "resources", "static", "images", "shirt_outline.jpg");
                    String scriptPath = projectDir + "/src/main/resources/python/virtual_tryon.py";

                    Files.write(fabricPath, fabricBytes);

                    // OS Detection for Python Command
                    String pythonCmd = System.getProperty("os.name").toLowerCase().contains("win") ? "python" : "python3";

                    ProcessBuilder pb = new ProcessBuilder(
                            pythonCmd, scriptPath,
                            fabricPath.toAbsolutePath().toString(),
                            outlinePath.toAbsolutePath().toString(),
                            resultPath.toAbsolutePath().toString()
                    );

                    Process p = pb.start();
                    p.waitFor(10, TimeUnit.SECONDS);

                    if (Files.exists(resultPath)) {
                        newItem.setImageData(Files.readAllBytes(resultPath));
                    } else {
                        // Fallback if AI fails
                        newItem.setImageData(fabricBytes);
                    }
                    Files.deleteIfExists(fabricPath);
                    Files.deleteIfExists(resultPath);

                } else {
                    // NORMAL MODE -> JUST SAVE RAW BYTES
                    newItem.setImageData(fabricBytes);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        order.getItems().add(newItem);
        double itemTotal = (newItem.getPrice() != null ? newItem.getPrice() : 0.0) * newItem.getQuantity();
        order.setTotalAmount(order.getTotalAmount() + itemTotal);

        return "redirect:/orders/create/outfits";
    }

    // STEP 3: Invoice
    @GetMapping("/create/invoice")
    public String showInvoiceStep(@ModelAttribute("currentOrder") ShopOrder order) {
        return "order_step4_invoice";
    }

    // FINAL STEP: Confirm & Save to DB
    @PostMapping("/confirmOrder")
    public String confirmOrder(@ModelAttribute("currentOrder") ShopOrder order,
                               @RequestParam String expectedDate,
                               SessionStatus sessionStatus) {

        if (expectedDate != null && !expectedDate.isEmpty()) {
            order.setExpectedFittingDate(LocalDate.parse(expectedDate));
        }
        order.setStatus("Pending Payment");
        order.setConfirmed(true);

        ShopOrder savedOrder = orderRepository.save(order);

        String customTrackingId = "TRK-" + savedOrder.getCustomer().getId() + "-" + savedOrder.getId();
        savedOrder.setTrackingId(customTrackingId);
        orderRepository.save(savedOrder);

        sessionStatus.setComplete();

        return "redirect:/orders/view/" + savedOrder.getId();
    }

    @GetMapping("/view/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        ShopOrder order = orderRepository.findById(orderId).orElseThrow();
        model.addAttribute("order", order);
        return "order_details";
    }

    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        ShopOrder order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/orders/view/" + orderId;
    }

    @PostMapping("/addPayment")
    public String addPayment(@RequestParam Long orderId, @RequestParam Double amount, @RequestParam String paymentMode) {
        ShopOrder order = orderRepository.findById(orderId).orElseThrow();
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMode(paymentMode);
        paymentRepository.save(payment);
        return "redirect:/orders/view/" + orderId;
    }

    @GetMapping("/print/measurements/{orderId}")
    public String printMeasurements(@PathVariable Long orderId, Model model) {
        ShopOrder order = orderRepository.findById(orderId).orElseThrow();
        model.addAttribute("order", order);
        model.addAttribute("m", order.getMeasurement());
        return "print_measurements";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }

    // --- ADDED: DISPLAY IMAGE FROM DB ---
    @GetMapping("/image/{itemId}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId).orElse(null);
        if (item != null && item.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(item.getImageData());
        }
        return ResponseEntity.notFound().build();
    }
}