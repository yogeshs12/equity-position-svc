package com.yogesh.equity_position.dto;

public class CurrentPosition {
        private String securityCode;
        private long quantity;

        public CurrentPosition(String securityCode, long quantity) {
            this.securityCode = securityCode;
            this.quantity = quantity;
        }
        public String getSecurityCode() {
            return securityCode;
        }
        public void setSecurityCode(String securityCode) {
            this.securityCode = securityCode;
        }
        public long getQuantity() {
            return quantity;
        }
        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }
    }