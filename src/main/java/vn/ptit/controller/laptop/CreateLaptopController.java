package vn.ptit.controller.laptop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ptit.controller.ResponseBody;
import vn.ptit.model.PagingPayload;
import vn.ptit.service.laptop.CreateLaptopService;
import vn.ptit.service.manufacturer.CreateManufacturerService;

@RequestMapping("/laptop")
@RestController
@CrossOrigin(origins = "*")
public class CreateLaptopController {
    private final CreateLaptopService createLaptopService;

    public CreateLaptopController(CreateLaptopService createLaptopService) {
        this.createLaptopService = createLaptopService;
    }

    @PostMapping("/insert")
    public ResponseEntity<?> create(@RequestBody CreateLaptopService.CreateInput input){
        createLaptopService.create(input);
        return new ResponseEntity<>(new ResponseBody(PagingPayload.empty(),ResponseBody.Status.SUCCESS,ResponseBody.Code.SUCCESS), HttpStatus.OK);
    }
}
