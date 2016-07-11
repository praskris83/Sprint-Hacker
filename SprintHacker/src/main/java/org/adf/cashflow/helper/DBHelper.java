/**
 * 
 */
package org.adf.cashflow.helper;

import java.util.List;

import javax.transaction.Transactional;

import org.adf.cashflow.CashFlow;
import org.adf.cashflow.CashFlowRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Prasad
 *
 */
@Transactional
@Service
public class DBHelper {

  @Autowired
  CashFlowRepo repo;
  
  @Autowired
  JdbcTemplate jdbcTemplate;
  
  public List<CashFlow> findAll(List<String> ids){
    return repo.findAll(ids);
  }
  
  public void save(List<CashFlow> datas){
    repo.save(datas);
  }

  public CashFlow findOne(String id) {
    return repo.findOne(id);
  }

  public List<CashFlow> findAllNative(String[] split){
    return repo.findAllKeys(split);
  }
}
