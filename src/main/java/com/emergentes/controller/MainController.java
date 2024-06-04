package com.emergentes.controller;

import com.emergentes.bean.BeanEstudiante;
import com.emergentes.entidades.Estudiante;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id;
            Estudiante est = new Estudiante();
            BeanEstudiante dao = new BeanEstudiante();
            String action = (request.getParameter("action") != null) ? request.getParameter("action") : "view";
            switch (action) {
                case "add":
                    request.setAttribute("estudiante", est);
                    request.getRequestDispatcher("frmestudiante.jsp").forward(request, response);
                    break;
                case "edit":
                    id = Integer.parseInt(request.getParameter("id"));
                    est = dao.buscar(id);
                    request.setAttribute("estudiante", est);
                    request.getRequestDispatcher("frmestudiante.jsp").forward(request, response);
                    break;
                case "delete":
                    id = Integer.parseInt(request.getParameter("id"));
                    dao.eliminar(id);
                    response.sendRedirect("MainController");
                    break;
                case "view":
                    // obtener la lista de registros
                    List<Estudiante> listaClientes = dao.listarTodos();
                    request.setAttribute("estudiantes", listaClientes);
                    request.getRequestDispatcher("estudiantes.jsp").forward(request, response);
                    break;
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Validar y obtener el valor del ID
            String idStr = request.getParameter("id");
            int id = 0; // Valor predeterminado para nuevas inserciones
            if (idStr != null && !idStr.isEmpty()) {
                id = Integer.parseInt(idStr);
            }

            // Validar y obtener otros parámetros
            String nombre = request.getParameter("nombre");
            if (nombre == null || nombre.isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }

            String apellidos = request.getParameter("apellidos");
            if (apellidos == null || apellidos.isEmpty()) {
                throw new IllegalArgumentException("Los apellidos no pueden estar vacíos");
            }

            String email = request.getParameter("email");
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("El email no puede estar vacío");
            }

            String fechaNacimientoStr = request.getParameter("fechaNacimiento");
            if (fechaNacimientoStr == null || fechaNacimientoStr.isEmpty()) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede estar vacía");
            }

            // Validar el formato de la fecha
            if (!fechaNacimientoStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException("El formato de la fecha de nacimiento debe ser YYYY-MM-DD");
            }

            Date fechaNacimiento = Date.valueOf(fechaNacimientoStr);

            // Crear objeto Estudiante y establecer sus propiedades
            Estudiante est = new Estudiante();
            BeanEstudiante dao = new BeanEstudiante();

            est.setId(id);
            est.setNombre(nombre);
            est.setApellidos(apellidos);
            est.setEmail(email);
            est.setFechaNacimiento(fechaNacimiento);

            // Insertar o editar estudiante en base al ID
            if (id == 0) {
                dao.insertar(est);
            } else {
                dao.editar(est);
            }

            // Redirigir al controlador principal
            response.sendRedirect("MainController");
        } catch (Exception ex) {
            // Manejo de otras excepciones generales
            System.out.println("Error al procesar la solicitud: " + ex.getMessage());
            request.setAttribute("error", "Error al procesar la solicitud: " + ex.getMessage());
            request.getRequestDispatcher("/errorPage.jsp").forward(request, response);
        }
    }
}
