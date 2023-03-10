package vn.ptit.controller.manufacturer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ptit.controller.ResponseBody;
import vn.ptit.exception.InvalidRequestException;
import vn.ptit.model.PagingPayload;
import vn.ptit.service.manufacturer.GetManufacturerService;

import java.util.Collections;

@RequestMapping("/manufacturer")
@RestController
@CrossOrigin(origins = "*")
public class GetManufacturerController {
    private final GetManufacturerService getManufacturerService;

    public GetManufacturerController(GetManufacturerService getManufacturerService) {
        this.getManufacturerService = getManufacturerService;
    }


    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                  @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                  @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort) {
        PagingPayload.PagingPayloadBuilder payloadBuilder = PagingPayload.builder();
        payloadBuilder.timestamp(System.currentTimeMillis());
        payloadBuilder.data(getManufacturerService.getList(page, limit, sort));
        return new ResponseEntity<>(new ResponseBody(payloadBuilder.build(), ResponseBody.Status.SUCCESS, ResponseBody.Code.SUCCESS), HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id") Long id) {
        if (id == null)
            throw new InvalidRequestException("Require [id]");

        PagingPayload.PagingPayloadBuilder payloadBuilder = PagingPayload.builder();
        payloadBuilder.timestamp(System.currentTimeMillis());
        payloadBuilder.data(Collections.singletonList(getManufacturerService.get(id)));
        return new ResponseEntity<>(new ResponseBody(payloadBuilder.build(), ResponseBody.Status.SUCCESS, ResponseBody.Code.SUCCESS), HttpStatus.OK);
    }
}
