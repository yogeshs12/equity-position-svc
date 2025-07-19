package com.yogesh.equity_position.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yogesh.equity_position.dto.TradeDeatils;

public interface TradeDeatilsRepository extends JpaRepository<TradeDeatils, Long> {
}
