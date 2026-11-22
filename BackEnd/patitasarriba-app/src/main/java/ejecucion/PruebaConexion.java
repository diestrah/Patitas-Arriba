package ejecucion;

import conexion.DBManager;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {
    public static void main(String[] args) {
        try {
            Connection conexion = DBManager.getInstance().getConnection();

            if (conexion != null && !conexion.isClosed()) {
                System.out.println("Conexion con la base de datos exitosa!");
            }

            conexion.close();
        } catch (SQLException e) {
            System.err.println("Error de conectividad JDBC: " + e.getMessage());
        }
    }
}
