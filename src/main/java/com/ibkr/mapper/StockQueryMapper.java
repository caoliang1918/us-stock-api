package com.ibkr.mapper;

import com.ibkr.entity.StockQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface StockQueryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_query
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_query
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    int insert(StockQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_query
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    int insertSelective(StockQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_query
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    StockQuery selectByUniqueKey(@Param("uts") Date uts, @Param("symbol") String symbol , @Param("price")String price);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_query
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    int updateByPrimaryKeySelective(StockQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_query
     *
     * @mbg.generated Tue Nov 06 14:16:15 CST 2018
     */
    int updateByPrimaryKey(StockQuery record);

    List<StockQuery> selectByMap(Map<String,Object> params);
}