package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.dto.ResponseResult;
import com.parking.entity.ParkingLot;
import com.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;

    @GetMapping
    public ResponseResult<Page<ParkingLot>> getParkingLots(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<ParkingLot> page = new Page<>(current, size);
        Page<ParkingLot> result = (Page<ParkingLot>) parkingLotService.getParkingLots(page, keyword);
        return ResponseResult.success(result);
    }

    @GetMapping("/{id}")
    public ResponseResult<ParkingLot> getParkingLotById(@PathVariable Long id) {
        ParkingLot parkingLot = parkingLotService.getParkingLotById(id);
        if (parkingLot != null) {
            return ResponseResult.success(parkingLot);
        } else {
            return ResponseResult.error("停车场不存在");
        }
    }

    @PostMapping
    public ResponseResult<ParkingLot> createParkingLot(@RequestBody ParkingLot parkingLot) {
        ParkingLot created = parkingLotService.createParkingLot(parkingLot);
        return ResponseResult.success("创建成功", created);
    }

    @PutMapping("/{id}")
    public ResponseResult<ParkingLot> updateParkingLot(@PathVariable Long id, @RequestBody ParkingLot parkingLot) {
        parkingLot.setId(id);
        ParkingLot updated = parkingLotService.updateParkingLot(parkingLot);
        return ResponseResult.success("更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> deleteParkingLot(@PathVariable Long id) {
        boolean result = parkingLotService.deleteParkingLot(id);
        if (result) {
            return ResponseResult.success("删除成功", true);
        } else {
            return ResponseResult.error("删除失败");
        }
    }

    @GetMapping("/{id}/available-spots")
    public ResponseResult<Integer> getAvailableSpotsCount(@PathVariable Long id) {
        int count = parkingLotService.getAvailableSpotsCount(id);
        return ResponseResult.success(count);
    }
}