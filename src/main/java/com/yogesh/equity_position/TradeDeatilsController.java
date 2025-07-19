package com.yogesh.equity_position;

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
            // set other fields as needed
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
    public List<SecurityAggregation> getAggregatedQuantityBySecurityCode() {
        List<TradeDeatils> trades = repository.findAll();
        Map<String, Long> aggregation = new HashMap<>();
        for (TradeDeatils trade : trades) {
            long qty = trade.getQuantity();
            if ("sell".equalsIgnoreCase(trade.getOrderType())) {
                qty = -qty;
            }
            aggregation.merge(trade.getSecurityCode(), qty, Long::sum);
        }
        List<SecurityAggregation> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : aggregation.entrySet()) {
            result.add(new SecurityAggregation(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public static class SecurityAggregation {
        private String securityCode;
        private long aggregatedQuantity;

        public SecurityAggregation(String securityCode, long aggregatedQuantity) {
            this.securityCode = securityCode;
            this.aggregatedQuantity = aggregatedQuantity;
        }
        public String getSecurityCode() {
            return securityCode;
        }
        public void setSecurityCode(String securityCode) {
            this.securityCode = securityCode;
        }
        public long getAggregatedQuantity() {
            return aggregatedQuantity;
        }
        public void setAggregatedQuantity(long aggregatedQuantity) {
            this.aggregatedQuantity = aggregatedQuantity;
        }
    }
}
