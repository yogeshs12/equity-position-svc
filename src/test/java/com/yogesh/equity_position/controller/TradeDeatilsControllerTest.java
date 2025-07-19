package com.yogesh.equity_position.controller;

import com.yogesh.equity_position.dao.TradeDeatilsRepository;
import com.yogesh.equity_position.dto.CurrentPosition;
import com.yogesh.equity_position.dto.TradeDeatils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TradeDeatilsControllerTest {

    @Mock
    private TradeDeatilsRepository repository;

    @InjectMocks
    private TradeDeatilsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTrade_shouldSaveAndReturnTrade() {
        TradeDeatils trade = new TradeDeatils();
        when(repository.save(any(TradeDeatils.class))).thenReturn(trade);

        TradeDeatils result = controller.addTrade(trade);

        assertNotNull(result);
        assertEquals(trade, result);
        verify(repository).save(trade);
    }

    @Test
    void updateTrade_shouldUpdateAndReturnTrade_whenFound() {
        TradeDeatils trade = new TradeDeatils();
        trade.setTradeId(1L);
        trade.setVersion(1L);
        trade.setSecurityCode("ABC");
        trade.setQuantity(10L);
        trade.setOrderType("buy");
        TradeDeatils existing = new TradeDeatils();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(TradeDeatils.class))).thenReturn(existing);

        ResponseEntity<TradeDeatils> response = controller.updateTrade(1L, trade);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(repository).save(existing);
    }

    @Test
    void updateTrade_shouldReturnNotFound_whenTradeDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<TradeDeatils> response = controller.updateTrade(1L, new TradeDeatils());

        assertEquals(404, response.getStatusCodeValue());
        
    }
    

    @Test
    void deleteTrade_shouldDeleteAndReturnNoContent_whenFound() {
        when(repository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteTrade(1L);

        assertEquals(204, response.getStatusCodeValue());
       
    }

    @Test
    void deleteTrade_shouldReturnNotFound_whenTradeDoesNotExist() {
        when(repository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteTrade(1L);

        assertEquals(404, response.getStatusCodeValue());
        
    }

    @Test
    void getAllTrades_shouldReturnAllTrades() {
        List<TradeDeatils> trades = Arrays.asList(new TradeDeatils(), new TradeDeatils());
        when(repository.findAll()).thenReturn(trades);

        List<TradeDeatils> result = controller.getAllTrades();

        assertEquals(trades, result);
        
    }

    @Test
    void getAggregatedQuantityBySecurityCode_shouldAggregateCorrectly() {
        TradeDeatils buy = new TradeDeatils();
        buy.setSecurityCode("ABC");
        buy.setOrderType("buy");
        buy.setQuantity(10L);
        TradeDeatils sell = new TradeDeatils();
        sell.setSecurityCode("ABC");
        sell.setOrderType("sell");
        sell.setQuantity(5L);
        when(repository.findAll()).thenReturn(Arrays.asList(buy, sell));

        List<CurrentPosition> result = controller.getAggregatedQuantityBySecurityCode();

        assertEquals(1, result.size());
        assertEquals("ABC", result.get(0).getSecurityCode());
        assertEquals(5L, result.get(0).getQuantity());
    }
}
