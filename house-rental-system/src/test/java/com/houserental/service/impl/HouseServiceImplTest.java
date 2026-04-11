package com.houserental.service.impl;

import com.houserental.entity.House;
import com.houserental.mapper.HouseMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HouseServiceImplTest {

    @Mock
    private HouseMapper houseMapper;

    @InjectMocks
    private HouseServiceImpl houseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublish() {
        House house = new House();
        house.setTitle("Test House");
        house.setRentPrice(new BigDecimal(1500.0));

        when(houseMapper.insert(house)).thenReturn(1);

        House result = houseService.publish(house);

        assertNotNull(result);
        verify(houseMapper, times(1)).insert(house);
    }

    @Test
    void testUpdate() {
        House house = new House();
        house.setId(1L);
        house.setTitle("Updated House");

        House existingHouse = new House();
        existingHouse.setId(1L);
        existingHouse.setLandlordId(1L);

        when(houseMapper.selectById(1L)).thenReturn(existingHouse);
        when(houseMapper.updateById(existingHouse)).thenReturn(1);

        House result = houseService.update(house);

        assertNotNull(result);
        verify(houseMapper, times(1)).updateById(existingHouse);
    }

    @Test
    void testDelete() {
        Long houseId = 1L;

        House existingHouse = new House();
        existingHouse.setId(houseId);
        existingHouse.setLandlordId(1L);

        when(houseMapper.selectById(houseId)).thenReturn(existingHouse);
        when(houseMapper.updateById(existingHouse)).thenReturn(1);

        houseService.delete(houseId);

        verify(houseMapper, times(1)).updateById(existingHouse);
    }

    @Test
    void testGetById() {
        Long houseId = 1L;
        House expectedHouse = new House();
        expectedHouse.setId(houseId);
        expectedHouse.setTitle("Test House");
        expectedHouse.setDeleted(0);

        when(houseMapper.selectById(houseId)).thenReturn(expectedHouse);

        House actualHouse = houseService.getById(houseId);

        assertNotNull(actualHouse);
        assertEquals(houseId, actualHouse.getId());
        assertEquals("Test House", actualHouse.getTitle());
        verify(houseMapper, times(1)).selectById(houseId);
    }

    @Test
    void testOnline() {
        Long houseId = 1L;

        House existingHouse = new House();
        existingHouse.setId(houseId);
        existingHouse.setAuditStatus(1);

        when(houseMapper.selectById(houseId)).thenReturn(existingHouse);
        when(houseMapper.updateById(existingHouse)).thenReturn(1);

        houseService.online(houseId);

        assertEquals(2, existingHouse.getStatus());
        verify(houseMapper, times(1)).updateById(existingHouse);
    }

    @Test
    void testOffline() {
        Long houseId = 1L;

        House existingHouse = new House();
        existingHouse.setId(houseId);

        when(houseMapper.selectById(houseId)).thenReturn(existingHouse);
        when(houseMapper.updateById(existingHouse)).thenReturn(1);

        houseService.offline(houseId);

        assertEquals(3, existingHouse.getStatus());
        verify(houseMapper, times(1)).updateById(existingHouse);
    }
}
