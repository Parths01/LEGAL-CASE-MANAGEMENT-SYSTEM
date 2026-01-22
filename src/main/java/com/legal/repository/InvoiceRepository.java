package com.legal.repository;

import com.legal.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    interface MonthlyRevenue {
        Integer getYear();
        Integer getMonth();
        java.math.BigDecimal getTotal();
    }
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByLegalCaseCaseId(Long caseId);
    
    List<Invoice> findByClientClientId(Long clientId);
    
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal calculateTotalRevenue();
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status IN ('SENT', 'OVERDUE')")
    BigDecimal calculatePendingRevenue();

    @Query("""
            SELECT YEAR(i.invoiceDate) AS year,
                   MONTH(i.invoiceDate) AS month,
                   SUM(i.totalAmount) AS total
            FROM Invoice i
            WHERE i.status = 'PAID'
            GROUP BY YEAR(i.invoiceDate), MONTH(i.invoiceDate)
            ORDER BY year, month
            """)
    List<MonthlyRevenue> fetchMonthlyRevenue();
}
