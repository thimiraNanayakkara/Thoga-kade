/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.thogakade.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lk.ijse.thogakade.db.DBConnection;
import lk.ijse.thogakade.model.Order;


/**
 *
 * @author niroth
 */
public class OrderController {
    
    public static String getLastOrderId() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT id FROM Orders ORDER BY id DESC LIMIT 1");
        return rst.next() ? rst.getString("id") : null;
    }

    public static boolean AddNewOrder(Order order) throws ClassNotFoundException, SQLException {
        try{
        DBConnection.getInstance().getConnection().setAutoCommit(false);
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement stm = connection.prepareStatement("insert into orders values(?,?,?)");
        stm.setObject(1, order.getId());
        stm.setObject(2, order.getDate());
        stm.setObject(3, order.getCustomerId());
        boolean isAddedOrder = stm.executeUpdate()>0;
        if(isAddedOrder){
           boolean isAddedOrderDetail = OrderDetailController.addNewOrderDetail(order.getOrderDetailList());
           if(isAddedOrderDetail){
                  boolean isUpdatedItem = ItemController.updateItemStock(order.getOrderDetailList());
                  if(isUpdatedItem){
                      DBConnection.getInstance().getConnection().commit();
                     return true;
                  }
           }
        }
        DBConnection.getInstance().getConnection().rollback();
        return false;
        }finally{
           DBConnection.getInstance().getConnection().setAutoCommit(true);
        }
    }
    
    
}
