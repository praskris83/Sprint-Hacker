/**
 * 
 */
package org.adf.cashflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Prasad
 *
 */
public interface CashFlowRepo extends JpaRepository<CashFlow, String> {

//  CashFlow findByKey(String key);

  
//  @Modifying
//  @Query("update CashFlow u set u.cashFlow = ?1, u.bankName = ?2 where u.id = ?3")
//  void updateCashFlowDetails(int cashFlow, String bankName, String key);
  
  
//  @Modifying
//  @Query(value = "update hacker.cash_flow set bank_name = ?2, cash_flow=?1 where key = ?3", nativeQuery = true)
//  void updateCashFlow(int cashFlow, String bankName, String key);
  @Query(value = "select new CashFlow(c.id,c.file) from CashFlow c where c.id in :key")
  List<CashFlow> findAllKeys(@Param("key")String[] key);
}
