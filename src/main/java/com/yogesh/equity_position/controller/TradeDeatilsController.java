package com.yogesh.equity_position.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yogesh.equity_position.dao.TradeDeatilsRepository;
import com.yogesh.equity_position.dto.TradeDeatils;
import com.yogesh.equity_position.dto.CurrentPosition;

@RestController
@RequestMapping("/api/trades")
public class TradeDeatilsController {
    @Autowired
    private TradeDeatilsRepository repository;

    @PostMapping
    public TradeDeatils addTrade(@RequestBody TradeDeatils trade) {
        return repository.save(trade);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TradeDeatils> updateTrade(@PathVariable Long id, @RequestBody TradeDeatils trade) {
        Optional<TradeDeatils> existing = repository.findById(id);
        if (existing.isPresent()) {
            TradeDeatils t = existing.get();
            t.setTradeId(trade.getTradeId());
            t.setVersion(trade.getVersion());
            t.setSecurityCode(trade.getSecurityCode());
            t.setQuantity(trade.getQuantity());
            t.setOrderType(trade.getOrderType());
            
            return ResponseEntity.ok(repository.save(t));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<TradeDeatils> getAllTrades() {
        return repository.findAll();
    }

    @GetMapping("/aggregate")
    public List<CurrentPosition> getAggregatedQuantityBySecurityCode() {
        List<TradeDeatils> trades = repository.findAll();
        Map<String, Long> netPositions = new HashMap<>();

        for (TradeDeatils trade : trades) {
            long qty = trade.getQuantity();
            // Subtract quantity for sell trades
            if ("sell".equalsIgnoreCase(trade.getOrderType())) {
                qty = -qty;
            }
            netPositions.merge(trade.getSecurityCode(), qty, (a,b)-> a+b);
        }

        List<CurrentPosition> positions = new ArrayList<>();
        for (Map.Entry<String, Long> entry : netPositions.entrySet()) {
            positions.add(new CurrentPosition(entry.getKey(), entry.getValue()));
        }
        return positions;
    }

    
}
