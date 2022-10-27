/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.History;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author duyph
 */
public class test {

    public static void main(String[] args) throws SQLException {
        int mathchld = 1, ddPlayerWin = 1, idPlayerLose = 1;
        int playTime = (20);
        Date startTime = new Date(2022, 10, 25, 18, 30, 20);
        Date endTime = new Date(2022, 10, 25, 18, 30, 40);;

        History ht = new History(mathchld, playTime, startTime, endTime, ddPlayerWin, idPlayerLose);

        new HistoryDAL().addHistory(ht);
    }
}
